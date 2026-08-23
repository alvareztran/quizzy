package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Question;
import com.quizzy.service.AnswerService;
import com.quizzy.service.QuestionService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.AnswerView;
import com.quizzy.view.component.ConfirmDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class AnswerController {

    private final AnswerView view;
    private final AnswerService answerService = ServiceFactory.getAnswerService();
    private final QuestionService questionService = ServiceFactory.getQuestionService();

    private final ObservableList<Answer> displayedAnswerList = FXCollections.observableArrayList();
    private final List<Answer> allAnswers = new ArrayList<>();
    private final List<Question> allQuestions = new ArrayList<>();
    private final Map<Integer, String> questionMap = new HashMap<>();
    private final List<Answer> currentFilteredList = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    public AnswerController() {
        this.view = new AnswerView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public AnswerView getAnswerView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> loadData());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        view.getSearchAnswersField().textProperty().addListener((obs, oldV, newV) -> filterAnswers());
        view.getQuestionFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterAnswers());
        view.getStatusFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterAnswers());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchAnswersField().clear();
            view.getQuestionFilterComboBox().setValue("All Questions");
            view.getStatusFilterComboBox().setValue("All Correct Status");
            filterAnswers();
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

        view.getQuestionContentColumn().setCellValueFactory(cellData -> {
            int qId = cellData.getValue().getQuestionId();
            String content = questionMap.getOrDefault(qId, "Question #" + qId);
            return new SimpleStringProperty(content);
        });

        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = NavIconHelper.createDeleteActionButton();
            private final HBox btnBox = new HBox(8, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                deleteBtn.setOnAction(e -> {
                    Answer answer = getTableView().getItems().get(getIndex());
                    deleteAnswer(answer);
                });
            }

            @Override
            protected void updateItem(Answer item, boolean empty) {
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
        view.getAnswerTable().setItems(displayedAnswerList);
        loadData();
    }

    private void loadData() {
        try {
            allAnswers.clear();
            allQuestions.clear();
            questionMap.clear();

            List<Question> questions = questionService.getAllQuestions();
            if (questions != null) {
                allQuestions.addAll(questions);
                view.getQuestionFilterComboBox().getItems().setAll("All Questions");
                for (Question q : questions) {
                    questionMap.put(q.getQuestionId(), q.getContent());
                    view.getQuestionFilterComboBox().getItems().add(q.getContent());

                    List<Answer> qAnswers = answerService.getAnswersByQuestionId(q.getQuestionId());
                    if (qAnswers != null) {
                        allAnswers.addAll(qAnswers);
                    }
                }
                view.getQuestionFilterComboBox().setValue("All Questions");
            }

            filterAnswers();
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load answers from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalAnswersCard().getValueLabel().setText(String.valueOf(allAnswers.size()));

        long correctCount = allAnswers.stream().filter(Answer::isIsCorrect).count();
        long incorrectCount = allAnswers.size() - correctCount;

        view.getCorrectAnswersCard().getValueLabel().setText(String.valueOf(correctCount));
        view.getIncorrectAnswersCard().getValueLabel().setText(String.valueOf(incorrectCount));
        view.getQuestionsCoveredCard().getValueLabel().setText(String.valueOf(questionMap.size()));
    }

    private void filterAnswers() {
        String keyword = view.getSearchAnswersField().getText();
        String selectedQuestion = view.getQuestionFilterComboBox().getValue();
        String selectedStatus = view.getStatusFilterComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Answer> filtered = allAnswers.stream().filter(ans -> {
            boolean matchSearch = (search == null)
                    || (ans.getAnswerContent() != null && ans.getAnswerContent().toLowerCase().contains(search));

            boolean matchQuestion = true;
            if (selectedQuestion != null && !"All Questions".equals(selectedQuestion)) {
                String qContent = questionMap.get(ans.getQuestionId());
                matchQuestion = selectedQuestion.equals(qContent);
            }

            boolean matchStatus = true;
            if ("Correct Only".equals(selectedStatus)) {
                matchStatus = ans.isIsCorrect();
            } else if ("Incorrect Only".equals(selectedStatus)) {
                matchStatus = !ans.isIsCorrect();
            }

            return matchSearch && matchQuestion && matchStatus;
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
            displayedAnswerList.setAll(currentFilteredList.subList(fromIndex, toIndex));
            view.getPaginationInfoLabel().setText(
                    String.format("Showing %d to %d of %d answers", (fromIndex + 1), toIndex, total)
            );
        } else {
            displayedAnswerList.clear();
            view.getPaginationInfoLabel().setText("Showing 0 to 0 of 0 answers");
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

    private void deleteAnswer(Answer answer) {
        if (answer == null) return;

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Answer Choice?",
                "Are you sure you want to delete this answer choice?"
        );

        if (!confirm) return;

        try {
            if (!answerService.deleteAnswer(answer.getAnswerId())) {
                showError("Unable to delete answer choice.");
                return;
            }
            showInfo("Answer choice deleted successfully.");
            loadData();
        } catch (Exception e) {
            showError("Unable to delete answer choice. Database error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Answer Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Answer Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
