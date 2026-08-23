package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
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
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class QuizController {

    private final QuizView view;
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final TopicService topicService = ServiceFactory.getTopicService();

    private final ObservableList<Quiz> displayedQuizList = FXCollections.observableArrayList();
    private final List<Quiz> allQuizzes = new ArrayList<>();
    private final List<Topic> allTopics = new ArrayList<>();
    private final Map<Integer, String> topicMap = new HashMap<>();

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
        // Navigation Buttons
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> loadData());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showResult());

        // Logout via User Profile ContextMenu Item
        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        // Header Action
        view.getCreateQuizBtn().setOnAction(e -> openCreateQuizDialog());

        // Search & Filters
        view.getSearchQuizzesField().textProperty().addListener((obs, oldV, newV) -> filterQuizzes());
        view.getTopicFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterQuizzes());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchQuizzesField().clear();
            view.getTopicFilterComboBox().setValue("All Topics");
            view.getSortComboBox().setValue("Sort by: Newest");
            filterQuizzes();
        });

        // Topic Name Column Factory
        view.getTopicNameColumn().setCellValueFactory(cellData -> {
            int topicId = cellData.getValue().getTopicId();
            String name = topicMap.getOrDefault(topicId, "Topic #" + topicId);
            return new SimpleStringProperty(name);
        });

        // Setup Actions Column Center Aligned
        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏");
            private final Button deleteBtn = new Button("🗑");
            private final HBox btnBox = new HBox(8, editBtn, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                editBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13px; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI Symbol', sans-serif;");
                deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13px; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI Symbol', sans-serif;");

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

        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d quizzes", displayedQuizList.size(), allQuizzes.size())
        );
    }

    private void filterQuizzes() {
        String keyword = view.getSearchQuizzesField().getText();
        String selectedTopicName = view.getTopicFilterComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Quiz> filtered = allQuizzes.stream().filter(q -> {
            boolean matchSearch = (search == null)
                    || (q.getQuizName() != null && q.getQuizName().toLowerCase().contains(search));

            boolean matchTopic = true;
            if (selectedTopicName != null && !"All Topics".equals(selectedTopicName)) {
                String tName = topicMap.get(q.getTopicId());
                matchTopic = selectedTopicName.equals(tName);
            }

            return matchSearch && matchTopic;
        }).toList();

        displayedQuizList.setAll(filtered);
        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d quizzes", displayedQuizList.size(), allQuizzes.size())
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

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Quiz?",
                "Are you sure you want to delete quiz '" + quiz.getQuizName() + "'? This action cannot be undone."
        );

        if (!confirm) return;

        try {
            if (!quizService.deleteQuiz(quiz.getQuizId())) {
                showError("Unable to delete quiz. Please check associated questions.");
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
