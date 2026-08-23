package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.SelectQuizView;
import java.util.ArrayList;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SelectQuizController {

    private final SelectQuizView view;
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final ObservableList<Topic> topicList = FXCollections.observableArrayList();
    private final List<Topic> allTopics = new ArrayList<>();

    private Quiz selectedQuiz = null;
    private final List<QuizCardHolder> quizCardHolders = new ArrayList<>();
    private List<Quiz> currentTopicQuizzes = new ArrayList<>();

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
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showHistory());

        view.getRefreshBtn().setOnAction(e -> refreshData());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());

        view.getSearchTopicField().textProperty().addListener((obs, oldVal, query) -> filterTopics(query));
        view.getSearchQuizField().textProperty().addListener((obs, oldVal, query) -> filterQuizzes(query));

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
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean isSel = isSelected();
                    HBox box = new HBox();
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(10, 14, 10, 14));

                    Label nameL = new Label(topic.getTopicName());
                    if (isSel) {
                        box.setStyle("-fx-background-color: #eef2ff; -fx-border-color: #4f46e5; -fx-border-width: 0 0 0 3.5px; -fx-border-radius: 0 8 8 0; -fx-background-radius: 0 8 8 0;");
                        nameL.setStyle("-fx-font-weight: 700; -fx-font-size: 14px; -fx-text-fill: #4f46e5;");
                    } else {
                        box.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-background-radius: 8;");
                        nameL.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-text-fill: #334155;");
                    }

                    box.getChildren().add(nameL);
                    setGraphic(box);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 2 0; -fx-cursor: hand;");
                }
            }
        });

        loadTopics();
    }

    private void loadTopics() {
        try {
            allTopics.clear();
            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                allTopics.addAll(topics);
            }
            filterTopics(view.getSearchTopicField().getText());
        } catch (Exception e) {
            showError("Failed to load topics from database.");
        }
    }

    private void filterTopics(String query) {
        String search = (query != null) ? query.trim().toLowerCase() : "";
        List<Topic> filtered = new ArrayList<>();
        for (Topic t : allTopics) {
            if (search.isEmpty() || (t.getTopicName() != null && t.getTopicName().toLowerCase().contains(search))) {
                filtered.add(t);
            }
        }
        topicList.setAll(filtered);
        if (!topicList.isEmpty()) {
            view.getTopicListView().getSelectionModel().select(0);
        } else {
            view.getTopicListView().getSelectionModel().clearSelection();
            loadQuizzesByTopic(null);
        }
    }

    private void loadQuizzesByTopic(Topic topic) {
        currentTopicQuizzes.clear();
        if (topic == null) {
            filterQuizzes(view.getSearchQuizField().getText());
            return;
        }

        try {
            List<Quiz> quizzes = quizService.getQuizzesByTopicId(topic.getTopicId());
            if (quizzes != null) {
                currentTopicQuizzes = quizzes;
            }
            filterQuizzes(view.getSearchQuizField().getText());
        } catch (Exception e) {
            showError("Failed to load quizzes for selected topic.");
        }
    }

    private void filterQuizzes(String query) {
        quizCardHolders.clear();
        view.getQuizCardsContainer().getChildren().clear();
        selectedQuiz = null;

        Topic selTopic = view.getTopicListView().getSelectionModel().getSelectedItem();
        String topicName = (selTopic != null) ? selTopic.getTopicName() : "General";

        if (currentTopicQuizzes == null || currentTopicQuizzes.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setPadding(new Insets(40, 20, 40, 20));
            Label emptyLabel = new Label("No quizzes available under \"" + topicName + "\" yet.");
            emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
            emptyBox.getChildren().add(emptyLabel);
            view.getQuizCardsContainer().getChildren().add(emptyBox);
            return;
        }

        String search = (query != null) ? query.trim().toLowerCase() : "";
        List<Quiz> filtered = new ArrayList<>();
        for (Quiz q : currentTopicQuizzes) {
            boolean matchName = (q.getQuizName() != null && q.getQuizName().toLowerCase().contains(search));
            if (search.isEmpty() || matchName) {
                filtered.add(q);
            }
        }

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setPadding(new Insets(40, 20, 40, 20));
            Label emptyLabel = new Label("No quizzes match \"" + query + "\".");
            emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
            emptyBox.getChildren().add(emptyLabel);
            view.getQuizCardsContainer().getChildren().add(emptyBox);
            return;
        }

        selectedQuiz = filtered.get(0);
        for (Quiz quiz : filtered) {
            QuizCardHolder holder = createQuizCard(quiz, topicName);
            quizCardHolders.add(holder);
            view.getQuizCardsContainer().getChildren().add(holder.card);
        }

        updateCardStyles();
    }

    private void updateCardStyles() {
        for (QuizCardHolder holder : quizCardHolders) {
            boolean isSel = (selectedQuiz != null && holder.quiz.getQuizId() == selectedQuiz.getQuizId());
            if (isSel) {
                holder.card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #4f46e5; -fx-border-width: 1.5px; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(79, 70, 229, 0.14), 16, 0, 0, 4); -fx-cursor: hand;");
                holder.startBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: 700; -fx-background-radius: 8; -fx-cursor: hand;");
            } else {
                holder.card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 2); -fx-cursor: hand;");
                holder.startBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #4f46e5; -fx-border-width: 1.5px; -fx-text-fill: #4f46e5; -fx-font-size: 14px; -fx-font-weight: 700; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        }
    }

    private QuizCardHolder createQuizCard(Quiz quiz, String topicName) {
        QuizCardHolder holder = new QuizCardHolder();
        holder.quiz = quiz;

        VBox card = new VBox(14);
        card.setPrefWidth(350);
        card.setMinWidth(310);
        card.setMaxWidth(400);
        card.setPadding(new Insets(24, 24, 22, 24));
        card.getStyleClass().add("card");

        Label titleL = new Label(quiz.getQuizName());
        titleL.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        Label descL = new Label("Comprehensive practice assessment covering " + topicName + " fundamentals.");
        descL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-line-spacing: 2px;");
        descL.setWrapText(true);
        descL.setMinHeight(42);

        HBox statsBox = new HBox();
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 0, 10, 0));

        VBox qBox = new VBox(2);
        qBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(qBox, Priority.ALWAYS);
        Label qNum = new Label(String.valueOf(quiz.getNumberOfQuestions()));
        qNum.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #4f46e5;");
        Label qLbl = new Label("Questions");
        qLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748b;");
        qBox.getChildren().addAll(qNum, qLbl);

        Region vDivider = new Region();
        vDivider.setPrefWidth(1);
        vDivider.setMinWidth(1);
        vDivider.setMaxWidth(1);
        vDivider.setPrefHeight(32);
        vDivider.setStyle("-fx-background-color: #e2e8f0;");

        VBox tBox = new VBox(2);
        tBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(tBox, Priority.ALWAYS);
        int mins = quiz.getTimeLimit() > 0 ? quiz.getTimeLimit() : 15;
        Label tNum = new Label(String.valueOf(mins));
        tNum.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #4f46e5;");
        Label tLbl = new Label("Minutes");
        tLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748b;");
        tBox.getChildren().addAll(tNum, tLbl);

        statsBox.getChildren().addAll(qBox, vDivider, tBox);

        Button startQuizBtn = new Button("Start Quiz  →");
        startQuizBtn.setMaxWidth(Double.MAX_VALUE);
        startQuizBtn.setPrefHeight(42);

        card.getChildren().addAll(titleL, descL, statsBox, startQuizBtn);

        holder.card = card;
        holder.startBtn = startQuizBtn;

        card.setOnMouseClicked(e -> {
            selectedQuiz = quiz;
            updateCardStyles();
            if (e.getClickCount() == 2) {
                SessionManager.setSelectedQuiz(quiz);
                SceneManager.showTakeQuiz();
            }
        });

        startQuizBtn.setOnAction(e -> {
            selectedQuiz = quiz;
            updateCardStyles();
            SessionManager.setSelectedQuiz(quiz);
            SceneManager.showTakeQuiz();
        });

        return holder;
    }

    private static class QuizCardHolder {
        Quiz quiz;
        VBox card;
        Button startBtn;
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
