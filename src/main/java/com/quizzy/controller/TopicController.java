package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.TopicView;
import com.quizzy.view.component.ConfirmDialog;
import com.quizzy.view.component.TopicFormDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class TopicController {

    private final TopicView view;
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuizService quizService = ServiceFactory.getQuizService();

    private final ObservableList<Topic> displayedTopicList = FXCollections.observableArrayList();
    private final List<Topic> allTopics = new ArrayList<>();

    public TopicController() {
        this.view = new TopicView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public TopicView getTopicView() {
        return view;
    }

    private void initEventHandlers() {
        // Navigation Buttons
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> loadTopics());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showResult());

        // Logout via User Profile ContextMenu Item
        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        // Header Action
        view.getCreateTopicBtn().setOnAction(e -> openCreateTopicDialog());

        // Search & Filters
        view.getSearchTopicsField().textProperty().addListener(
                (observable, oldValue, newValue) -> filterTopics(newValue)
        );

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchTopicsField().clear();
            view.getStatusFilterComboBox().setValue("All Status");
            view.getSortComboBox().setValue("Sort by: Newest");
            filterTopics(null);
        });

        // Setup Actions Table Column Center Aligned
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
                    Topic topic = getTableView().getItems().get(getIndex());
                    openEditTopicDialog(topic);
                });

                deleteBtn.setOnAction(e -> {
                    Topic topic = getTableView().getItems().get(getIndex());
                    deleteTopic(topic);
                });
            }

            @Override
            protected void updateItem(Topic item, boolean empty) {
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
        view.getTopicTable().setItems(displayedTopicList);
        loadTopics();
    }

    private void loadTopics() {
        try {
            allTopics.clear();
            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                allTopics.addAll(topics);
            }
            filterTopics(view.getSearchTopicsField().getText());
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load topics from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalTopicsCard().getValueLabel().setText(String.valueOf(allTopics.size()));
        view.getActiveTopicsCard().getValueLabel().setText(String.valueOf(allTopics.size()));

        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            int quizCount = quizzes != null ? quizzes.size() : 0;
            view.getTotalQuizzesCard().getValueLabel().setText(String.valueOf(quizCount));

            int qCount = 0;
            if (quizzes != null) {
                for (Quiz q : quizzes) {
                    qCount += q.getNumberOfQuestions();
                }
            }
            view.getTotalQuestionsCard().getValueLabel().setText(String.valueOf(qCount));
        } catch (Exception e) {
            view.getTotalQuizzesCard().getValueLabel().setText("0");
            view.getTotalQuestionsCard().getValueLabel().setText("0");
        }

        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d topics", displayedTopicList.size(), allTopics.size())
        );
    }

    private void filterTopics(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            displayedTopicList.setAll(allTopics);
        } else {
            String search = keyword.trim().toLowerCase();
            List<Topic> filtered = allTopics.stream()
                    .filter(t -> (t.getTopicName() != null && t.getTopicName().toLowerCase().contains(search))
                            || (t.getDescription() != null && t.getDescription().toLowerCase().contains(search)))
                    .toList();

            displayedTopicList.setAll(filtered);
        }

        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d topics", displayedTopicList.size(), allTopics.size())
        );
    }

    private void openCreateTopicDialog() {
        Optional<Topic> result = TopicFormDialog.showTopicDialog(null);
        result.ifPresent(newTopic -> {
            try {
                if (!topicService.createTopic(newTopic)) {
                    showError("Cannot create topic. Topic name may already exist.");
                    return;
                }
                showInfo("Topic created successfully.");
                loadTopics();
            } catch (Exception e) {
                showError("Unable to create topic. Database connection error.");
            }
        });
    }

    private void openEditTopicDialog(Topic topic) {
        if (topic == null) return;

        Optional<Topic> result = TopicFormDialog.showTopicDialog(topic);
        result.ifPresent(updatedTopic -> {
            try {
                if (!topicService.updateTopic(updatedTopic)) {
                    showError("Cannot update topic. Topic name may already exist.");
                    return;
                }
                showInfo("Topic updated successfully.");
                loadTopics();
            } catch (Exception e) {
                showError("Unable to update topic. Database connection error.");
            }
        });
    }

    private void deleteTopic(Topic topic) {
        if (topic == null) return;

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Topic?",
                "Are you sure you want to delete topic '" + topic.getTopicName() + "'? This action cannot be undone."
        );

        if (!confirm) return;

        try {
            if (!topicService.deleteTopic(topic.getTopicId())) {
                showError("Unable to delete topic. Please make sure no quizzes are assigned to this topic.");
                return;
            }
            showInfo("Topic deleted successfully.");
            loadTopics();
        } catch (Exception e) {
            showError("Unable to delete topic. Database connection error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Topic Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Topic Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
