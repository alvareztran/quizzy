package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Question;
import com.quizzy.model.Quiz;
import com.quizzy.service.AnswerService;
import com.quizzy.service.QuestionService;
import com.quizzy.service.QuizService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.QuestionView;
import com.quizzy.view.component.ConfirmDialog;
import com.quizzy.view.component.QuestionFormDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class QuestionController {

    private final QuestionView view;
    private final QuestionService questionService = ServiceFactory.getQuestionService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final AnswerService answerService = ServiceFactory.getAnswerService();

    private final ObservableList<Question> displayedQuestionList = FXCollections.observableArrayList();
    private final List<Question> allQuestions = new ArrayList<>();
    private final List<Quiz> allQuizzes = new ArrayList<>();
    private final Map<Integer, String> quizMap = new HashMap<>();
    private final List<Question> currentFilteredList = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    public QuestionController() {
        this.view = new QuestionView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public QuestionView getQuestionView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> loadData());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getCreateQuestionBtn().setOnAction(e -> openCreateQuestionDialog());

        view.getSearchQuestionsField().textProperty().addListener((obs, oldV, newV) -> filterQuestions());
        view.getQuizFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterQuestions());
        view.getDifficultyFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterQuestions());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchQuestionsField().clear();
            view.getQuizFilterComboBox().setValue("All Quizzes");
            view.getDifficultyFilterComboBox().setValue("All Difficulty");
            filterQuestions();
        });

        view.getPerPageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.contains("25")) {
                    pageSize = 25;
                } else if (newVal.contains("50")) {
                    pageSize = 50;
                } else {
                    pageSize = 10;
                }
                currentPage = 1;
                renderPage();
            }
        });

        view.getQuizNameColumn().setCellValueFactory(cellData -> {
            int qQuizId = cellData.getValue().getQuizId();
            String quizName = quizMap.getOrDefault(qQuizId, "Quiz #" + qQuizId);
            return new SimpleStringProperty(quizName);
        });

        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = NavIconHelper.createEditActionButton();
            private final Button deleteBtn = NavIconHelper.createDeleteActionButton();
            private final HBox btnBox = new HBox(8, editBtn, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                editBtn.setOnAction(e -> {
                    Question question = getTableView().getItems().get(getIndex());
                    openEditQuestionDialog(question);
                });

                deleteBtn.setOnAction(e -> {
                    Question question = getTableView().getItems().get(getIndex());
                    deleteQuestion(question);
                });
            }

            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void initializeData() {
        view.getQuestionTable().setItems(displayedQuestionList);
        loadData();
    }

    private void loadData() {
        try {
            allQuestions.clear();
            allQuizzes.clear();
            quizMap.clear();

            List<Quiz> quizzes = quizService.getAllQuizzes();
            if (quizzes != null) {
                allQuizzes.addAll(quizzes);
                view.getQuizFilterComboBox().getItems().setAll("All Quizzes");
                for (Quiz q : quizzes) {
                    quizMap.put(q.getQuizId(), q.getQuizName());
                    view.getQuizFilterComboBox().getItems().add(q.getQuizName());
                }
                view.getQuizFilterComboBox().setValue("All Quizzes");
            }

            List<Question> questions = questionService.getAllQuestions();
            if (questions != null) {
                allQuestions.addAll(questions);
            }

            filterQuestions();
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load questions from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalQuestionsCard().getValueLabel().setText(String.valueOf(allQuestions.size()));

        long easyCount = allQuestions.stream().filter(q -> "Easy".equalsIgnoreCase(q.getDifficulty())).count();
        long medCount = allQuestions.stream().filter(q -> "Medium".equalsIgnoreCase(q.getDifficulty())).count();
        long hardCount = allQuestions.stream().filter(q -> "Hard".equalsIgnoreCase(q.getDifficulty())).count();

        view.getEasyQuestionsCard().getValueLabel().setText(String.valueOf(easyCount));
        view.getMediumQuestionsCard().getValueLabel().setText(String.valueOf(medCount));
        view.getHardQuestionsCard().getValueLabel().setText(String.valueOf(hardCount));
    }

    private void filterQuestions() {
        String keyword = view.getSearchQuestionsField().getText();
        String selectedQuizName = view.getQuizFilterComboBox().getValue();
        String selectedDiff = view.getDifficultyFilterComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Question> filtered = allQuestions.stream().filter(q -> {
            boolean matchSearch = (search == null)
                    || (q.getContent() != null && q.getContent().toLowerCase().contains(search));

            boolean matchQuiz = true;
            if (selectedQuizName != null && !"All Quizzes".equals(selectedQuizName)) {
                String qName = quizMap.get(q.getQuizId());
                matchQuiz = selectedQuizName.equals(qName);
            }

            boolean matchDiff = true;
            if (selectedDiff != null && !"All Difficulty".equals(selectedDiff)) {
                matchDiff = selectedDiff.equalsIgnoreCase(q.getDifficulty());
            }

            return matchSearch && matchQuiz && matchDiff;
        }).toList();

        currentFilteredList.clear();
        currentFilteredList.addAll(filtered);
        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = currentFilteredList.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        if (fromIndex < total) {
            displayedQuestionList.setAll(currentFilteredList.subList(fromIndex, toIndex));
            view.getPaginationInfoLabel().setText(
                    String.format("Showing %d to %d of %d questions", (fromIndex + 1), toIndex, total)
            );
        } else {
            displayedQuestionList.clear();
            view.getPaginationInfoLabel().setText("Showing 0 to 0 of 0 questions");
        }

        renderPaginationButtons(totalPages);
    }

    private void renderPaginationButtons(int totalPages) {
        HBox box = view.getPaginationButtonsBox();
        box.getChildren().clear();

        Button prevBtn = new Button("<");
        stylePaginationBtn(prevBtn, false);
        prevBtn.setDisable(currentPage <= 1);
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });
        box.getChildren().add(prevBtn);

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            Button p1 = new Button("1");
            stylePaginationBtn(p1, currentPage == 1);
            p1.setOnAction(e -> {
                currentPage = 1;
                renderPage();
            });
            box.getChildren().add(p1);

            if (startPage > 2) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }
        }

        for (int p = startPage; p <= endPage; p++) {
            final int pageNum = p;
            Button pageBtn = new Button(String.valueOf(pageNum));
            boolean isActive = (pageNum == currentPage);
            stylePaginationBtn(pageBtn, isActive);
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                renderPage();
            });
            box.getChildren().add(pageBtn);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }

            Button pLast = new Button(String.valueOf(totalPages));
            stylePaginationBtn(pLast, currentPage == totalPages);
            pLast.setOnAction(e -> {
                currentPage = totalPages;
                renderPage();
            });
            box.getChildren().add(pLast);
        }

        Button nextBtn = new Button(">");
        stylePaginationBtn(nextBtn, false);
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
            }
        });
        box.getChildren().add(nextBtn);
    }

    private void stylePaginationBtn(Button btn, boolean isActive) {
        if (isActive) {
            btn.getStyleClass().add("button-primary");
            btn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        } else {
            btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 4 10; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        }
    }

    private void openCreateQuestionDialog() {
        Optional<QuestionFormDialog.QuestionFormResult> result = QuestionFormDialog.showQuestionDialog(null, null, allQuizzes);
        result.ifPresent(formResult -> {
            try {
                if (!questionService.createQuestion(formResult.question)) {
                    showError("Cannot create question. Invalid data.");
                    return;
                }

                // Retrieve created question ID to bind answers
                List<Question> quizQuestions = questionService.getQuestionsByQuizId(formResult.question.getQuizId());
                Question createdQuestion = null;
                if (quizQuestions != null && !quizQuestions.isEmpty()) {
                    createdQuestion = quizQuestions.get(quizQuestions.size() - 1);
                }

                if (createdQuestion != null && formResult.answers != null) {
                    for (Answer ans : formResult.answers) {
                        ans.setQuestionId(createdQuestion.getQuestionId());
                        answerService.createAnswer(ans);
                    }
                }

                showInfo("Question and 4 answer choices created successfully.");
                loadData();
            } catch (Exception e) {
                showError("Unable to create question and answers. Database error.");
            }
        });
    }

    private void openEditQuestionDialog(Question question) {
        if (question == null) return;

        List<Answer> existingAnswers = answerService.getAnswersByQuestionId(question.getQuestionId());
        Optional<QuestionFormDialog.QuestionFormResult> result = QuestionFormDialog.showQuestionDialog(question, existingAnswers, allQuizzes);

        result.ifPresent(formResult -> {
            try {
                if (!questionService.updateQuestion(formResult.question)) {
                    showError("Cannot update question. Invalid data.");
                    return;
                }

                if (formResult.answers != null) {
                    for (Answer ans : formResult.answers) {
                        if (ans.getAnswerId() > 0) {
                            answerService.updateAnswer(ans);
                        } else {
                            ans.setQuestionId(question.getQuestionId());
                            answerService.createAnswer(ans);
                        }
                    }
                }

                showInfo("Question and answer choices updated successfully.");
                loadData();
            } catch (Exception e) {
                showError("Unable to update question and answers. Database error.");
            }
        });
    }

    private void deleteQuestion(Question question) {
        if (question == null) return;

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Question?",
                "Are you sure you want to delete this question? Associated answer choices will also be removed."
        );

        if (!confirm) return;

        try {
            // Delete associated answers first
            List<Answer> answers = answerService.getAnswersByQuestionId(question.getQuestionId());
            if (answers != null) {
                for (Answer ans : answers) {
                    answerService.deleteAnswer(ans.getAnswerId());
                }
            }

            questionService.deleteQuestion(question.getQuestionId());
            showInfo("Question and answers deleted successfully.");
            loadData();
        } catch (Exception e) {
            showError("Unable to delete question. Database error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Question Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Question Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
