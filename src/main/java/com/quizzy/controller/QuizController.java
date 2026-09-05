package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Question;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.service.QuestionService;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.PaginationButtonRenderer;
import com.quizzy.util.Paginator;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.QuizView;
import com.quizzy.view.component.ConfirmDialog;
import com.quizzy.view.component.QuizFormDialog;
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

public class QuizController {

    private final QuizView view;
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuestionService questionService = ServiceFactory.getQuestionService();
    private final ResultService resultService = ServiceFactory.getResultService();

    private final ObservableList<Quiz> displayedQuizList = FXCollections.observableArrayList();
    private final List<Quiz> allQuizzes = new ArrayList<>();
    private final List<Topic> allTopics = new ArrayList<>();
    private final Map<Integer, String> topicMap = new HashMap<>();
    private final Paginator<Quiz> paginator = new Paginator<>(10);

    public QuizController() {
        this.view = new QuizView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public QuizView getQuizView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> loadData());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getCreateQuizBtn().setOnAction(e -> openCreateQuizDialog());

        view.getSearchQuizzesField().textProperty().addListener((obs, oldV, newV) -> filterQuizzes());
        view.getTopicFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterQuizzes());
        view.getSortComboBox().valueProperty().addListener((obs, oldV, newV) -> filterQuizzes());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchQuizzesField().clear();
            view.getTopicFilterComboBox().setValue("All Topics");
            view.getSortComboBox().setValue("Sort by: Newest");
            filterQuizzes();
        });

        view.getPerPageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.contains("25")) {
                    paginator.setPageSize(25);
                } else if (newVal.contains("50")) {
                    paginator.setPageSize(50);
                } else {
                    paginator.setPageSize(10);
                }
                paginator.setPage(1);
                renderPage();
            }
        });

        view.getTopicNameColumn().setCellValueFactory(cellData -> {
            int topicId = cellData.getValue().getTopicId();
            String name = topicMap.getOrDefault(topicId, "Topic #" + topicId);
            return new SimpleStringProperty(name);
        });

        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = NavIconHelper.createEditActionButton();
            private final Button deleteBtn = NavIconHelper.createDeleteActionButton();
            private final HBox btnBox = new HBox(8, editBtn, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                editBtn.setOnAction(e -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    openEditQuizDialog(quiz);
                });

                deleteBtn.setOnAction(e -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    deleteQuiz(quiz);
                });
            }

            @Override
            protected void updateItem(Quiz item, boolean empty) {
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
        view.getQuizTable().setItems(displayedQuizList);
        loadData();
    }

    private void loadData() {
        try {
            allQuizzes.clear();
            allTopics.clear();
            topicMap.clear();

            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                allTopics.addAll(topics);
                view.getTopicFilterComboBox().getItems().setAll("All Topics");
                for (Topic t : topics) {
                    topicMap.put(t.getTopicId(), t.getTopicName());
                    view.getTopicFilterComboBox().getItems().add(t.getTopicName());
                }
                view.getTopicFilterComboBox().setValue("All Topics");
            }

            List<Quiz> quizzes = quizService.getAllQuizzes();
            if (quizzes != null) {
                allQuizzes.addAll(quizzes);
            }

            filterQuizzes();
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load quizzes from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalQuizzesCard().getValueLabel().setText(String.valueOf(allQuizzes.size()));
        view.getActiveQuizzesCard().getValueLabel().setText(String.valueOf(allQuizzes.size()));
        view.getTotalTopicsCard().getValueLabel().setText(String.valueOf(topicMap.size()));

        int totalQ = 0;
        for (Quiz q : allQuizzes) {
            totalQ += q.getNumberOfQuestions();
        }
        view.getTotalQuestionsCard().getValueLabel().setText(String.valueOf(totalQ));
    }

    private void filterQuizzes() {
        String keyword = view.getSearchQuizzesField().getText();
        String selectedTopicName = view.getTopicFilterComboBox().getValue();
        String sortOption = view.getSortComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Quiz> filtered = new java.util.ArrayList<>(allQuizzes.stream().filter(q -> {
            boolean matchSearch = (search == null)
                    || (q.getQuizName() != null && q.getQuizName().toLowerCase().contains(search));

            boolean matchTopic = true;
            if (selectedTopicName != null && !"All Topics".equals(selectedTopicName)) {
                String tName = topicMap.get(q.getTopicId());
                matchTopic = selectedTopicName.equals(tName);
            }

            return matchSearch && matchTopic;
        }).toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Sort by: ID", "Sort by: Oldest" -> filtered.sort(java.util.Comparator.comparingInt(Quiz::getQuizId));
                case "Sort by: Newest" -> filtered.sort((a, b) -> Integer.compare(b.getQuizId(), a.getQuizId()));
                case "Sort by: Name A-Z" -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getQuizName() != null ? a.getQuizName() : "",
                        b.getQuizName() != null ? b.getQuizName() : ""
                ));
                case "Sort by: Questions Count" -> filtered.sort((a, b) -> Integer.compare(b.getNumberOfQuestions(), a.getNumberOfQuestions()));
                default -> filtered.sort(java.util.Comparator.comparingInt(Quiz::getQuizId));
            }
        } else {
            filtered.sort(java.util.Comparator.comparingInt(Quiz::getQuizId));
        }

        paginator.setItems(filtered);
        renderPage();
    }

    private void renderPage() {
        displayedQuizList.setAll(paginator.getCurrentPageItems());
        view.getPaginationInfoLabel().setText(paginator.getPaginationInfoText("quizzes"));
        PaginationButtonRenderer.renderButtons(
                view.getPaginationButtonsBox(),
                paginator.getCurrentPage(),
                paginator.getTotalPages(),
                page -> {
                    paginator.setPage(page);
                    renderPage();
                }
        );
    }

    private void openCreateQuizDialog() {
        Optional<Quiz> result = QuizFormDialog.showQuizDialog(null, allTopics);
        result.ifPresent(newQuiz -> {
            try {
                if (!quizService.createQuiz(newQuiz)) {
                    showError("Cannot create quiz. Title may already exist.");
                    return;
                }
                showInfo("Quiz created successfully.");
                loadData();
            } catch (Exception e) {
                showError("Unable to create quiz. Database error.");
            }
        });
    }

    private void openEditQuizDialog(Quiz quiz) {
        if (quiz == null) return;

        Optional<Quiz> result = QuizFormDialog.showQuizDialog(quiz, allTopics);
        result.ifPresent(updatedQuiz -> {
            try {
                if (!quizService.updateQuiz(updatedQuiz)) {
                    showError("Cannot update quiz. Title may already exist.");
                    return;
                }
                showInfo("Quiz updated successfully.");
                loadData();
            } catch (Exception e) {
                showError("Unable to update quiz. Database error.");
            }
        });
    }

    private void deleteQuiz(Quiz quiz) {
        if (quiz == null) return;

        List<Question> questions = questionService.getQuestionsByQuizId(quiz.getQuizId());
        if (questions != null && !questions.isEmpty()) {
            ConfirmDialog.showCannotDeleteAlert(
                    "Cannot Delete Quiz",
                    "Quiz '" + quiz.getQuizName() + "' cannot be deleted because it currently contains " + questions.size() + " question(s).",
                    "To protect question bank integrity, please delete all questions belonging to this quiz before deleting the quiz itself."
            );
            return;
        }

        List<Result> results = resultService.getResultsByQuizId(quiz.getQuizId());
        if (results != null && !results.isEmpty()) {
            ConfirmDialog.showCannotDeleteAlert(
                    "Cannot Delete Quiz",
                    "Quiz '" + quiz.getQuizName() + "' cannot be deleted because there are " + results.size() + " student exam record(s) associated with it.",
                    "To maintain test history integrity, you must delete or clear related test attempts before deleting this quiz."
            );
            return;
        }

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Quiz Assessment?",
                "Are you sure you want to delete quiz '" + quiz.getQuizName() + "'?",
                "This quiz has no dependent questions or student results. Deleting it will permanently remove this quiz assessment."
        );

        if (!confirm) return;

        try {
            if (!quizService.deleteQuiz(quiz.getQuizId())) {
                showError("Unable to delete quiz. Please ensure any related dependencies are cleared first.");
                return;
            }
            showInfo("Quiz deleted successfully.");
            loadData();
        } catch (Exception e) {
            showError("Unable to delete quiz. Database error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Quiz Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
