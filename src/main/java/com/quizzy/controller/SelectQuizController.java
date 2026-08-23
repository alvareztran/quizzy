package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.SelectQuizView;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SelectQuizController {

    private final SelectQuizView view;
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final ObservableList<Topic> topicList = FXCollections.observableArrayList();

    private Quiz selectedQuiz = null;

    public SelectQuizController() {
        this.view = new SelectQuizView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public SelectQuizView getSelectQuizView() {
        return view;
    }

    private void initEventHandlers() {
        view.getNavDashboardBtn().setOnAction(e -> SceneManager.showPlayerDashboard());
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showResult());

        view.getBackToDashboardBtn().setOnAction(e -> backToDashboard());
        view.getRefreshBtn().setOnAction(e -> refreshData());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());

        view.getTopicListView().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedTopic) -> loadQuizzesByTopic(selectedTopic)
        );
    }

    private void initializeData() {
        view.getTopicListView().setItems(topicList);
        view.getTopicListView().setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Topic topic, boolean empty) {
                super.updateItem(topic, empty);
                if (empty || topic == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    boolean isSel = isSelected();
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(12, 16, 12, 16));

                    String iconStr = getTopicIcon(topic.getTopicName());
                    Label iconL = new Label(iconStr);
                    iconL.setStyle(isSel ? "-fx-font-size: 16px; -fx-text-fill: #ffffff;" : "-fx-font-size: 16px; -fx-text-fill: #6366f1;");

                    Label nameL = new Label(topic.getTopicName());
                    nameL.setStyle(isSel
                            ? "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #ffffff;"
                            : "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #334155;");

                    box.getChildren().addAll(iconL, nameL);
                    box.setStyle(isSel
                            ? "-fx-background-color: #6366f1; -fx-background-radius: 8px;"
                            : "-fx-background-color: transparent; -fx-background-radius: 8px;");

                    setGraphic(box);
                    setText(null);
                }
            }
        });

        loadTopics();
    }

    private String getTopicIcon(String name) {
        if (name == null) return "📁";
        String lower = name.toLowerCase();
        if (lower.contains("math")) return "➗";
        if (lower.contains("science") || lower.contains("computer")) return "💻";
        if (lower.contains("data")) return "≡";
        if (lower.contains("oop")) return "⚙";
        return "📁";
    }

    private void loadTopics() {
        try {
            topicList.setAll(topicService.getAllTopics());
            if (!topicList.isEmpty()) {
                view.getTopicListView().getSelectionModel().select(0);
            }
        } catch (Exception e) {
            showError("Failed to load topics from database.");
        }
    }

    private void loadQuizzesByTopic(Topic topic) {
        view.getQuizCardsContainer().getChildren().clear();
        selectedQuiz = null;

        if (topic == null) {
            return;
        }

        try {
            List<Quiz> quizzes = quizService.getQuizzesByTopicId(topic.getTopicId());
            if (quizzes == null || quizzes.isEmpty()) {
                VBox emptyBox = new VBox(12);
                emptyBox.setPadding(new Insets(30));
                Label emptyLabel = new Label("No quizzes available under " + topic.getTopicName() + " yet.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
                emptyBox.getChildren().add(emptyLabel);
                view.getQuizCardsContainer().getChildren().add(emptyBox);
                return;
            }

            for (int i = 0; i < quizzes.size(); i++) {
                Quiz quiz = quizzes.get(i);
                boolean isPrimary = (i == 0);
                VBox quizCard = createQuizCard(quiz, topic.getTopicName(), isPrimary);
                HBox.setHgrow(quizCard, Priority.ALWAYS);
                view.getQuizCardsContainer().getChildren().add(quizCard);
            }

            selectedQuiz = quizzes.get(0);

        } catch (Exception e) {
            showError("Failed to load quizzes for selected topic.");
        }
    }

    private VBox createQuizCard(Quiz quiz, String topicName, boolean isPrimary) {
        VBox card = new VBox(16);
        card.setPrefWidth(280);
        card.setMinWidth(260);
        card.setPadding(new Insets(24));
        card.getStyleClass().add("card");
        card.setStyle(isPrimary
                ? "-fx-background-color: #ffffff; -fx-border-color: #6366f1; -fx-border-width: 1.5px; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(99, 102, 241, 0.12), 16, 0, 0, 4);"
                : "-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1px; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 10, 0, 0, 3);");

        // Top Row: Icon & Level Badge
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconL = new Label("💻");
        iconL.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-padding: 8 10; -fx-background-radius: 8px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String levelText = (quiz.getNumberOfQuestions() <= 10) ? "Beginner" : (quiz.getNumberOfQuestions() <= 20) ? "Intermediate" : "Advanced";
        Label levelBadge = new Label(levelText);
        levelBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6px;");

        topRow.getChildren().addAll(iconL, spacer, levelBadge);

        // Quiz Name
        Label titleL = new Label(quiz.getQuizName());
        titleL.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        // Quiz Description
        Label descL = new Label("Comprehensive practice assessment covering " + topicName + " fundamentals.");
        descL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-line-spacing: 3px;");
        descL.setWrapText(true);

        // Metadata Row
        HBox metaRow = new HBox(14);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label qCountL = new Label("≡ " + quiz.getNumberOfQuestions() + " Questions");
        qCountL.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b;");

        int mins = quiz.getTimeLimit() > 0 ? quiz.getTimeLimit() : 15;
        Label timeL = new Label("⏱ " + mins + " Minutes");
        timeL.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b;");

        metaRow.getChildren().addAll(qCountL, timeL);

        // Full-Width Start Quiz Button
        Button startQuizBtn = new Button("Start Quiz →");
        startQuizBtn.setMaxWidth(Double.MAX_VALUE);
        startQuizBtn.setPrefHeight(40);

        if (isPrimary) {
            startQuizBtn.getStyleClass().add("button-primary");
            startQuizBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px;");
        } else {
            startQuizBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #6366f1; -fx-text-fill: #6366f1; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        }

        startQuizBtn.setOnAction(e -> {
            SessionManager.setSelectedQuiz(quiz);
            SceneManager.showTakeQuiz();
        });

        card.getChildren().addAll(topRow, titleL, descL, metaRow, startQuizBtn);
        return card;
    }

    private void backToDashboard() {
        if (SessionManager.isAdmin()) {
            SceneManager.showMain();
        } else {
            SceneManager.showPlayerDashboard();
        }
    }

    private void refreshData() {
        loadTopics();
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Select Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
