package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.model.User;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.PlayerDashboardView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PlayerDashboardController {

    private final PlayerDashboardView view;
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final ResultService resultService = ServiceFactory.getResultService();

    public PlayerDashboardController() {
        this.view = new PlayerDashboardView();
        initEventHandlers();
        loadDashboardData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public PlayerDashboardView getPlayerDashboardView() {
        return view;
    }

    private void initEventHandlers() {
        view.getNavDashboardBtn().setOnAction(e -> SceneManager.showPlayerDashboard());
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showResult());

        view.getViewAllTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getResumeQuizBtn().setOnAction(e -> SceneManager.showSelectQuiz());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void loadDashboardData() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getFullName() != null && !currentUser.getFullName().isBlank()
                    ? currentUser.getFullName()
                    : currentUser.getUserName();
            view.getWelcomeTitleLabel().setText("Welcome back, " + name + "!");
        }

        try {
            List<Quiz> quizzes = quizService.getAllPlayableQuizzes();
            int totalQ = quizzes != null ? quizzes.size() : 0;
            view.getTotalQuizzesValLabel().setText(String.valueOf(totalQ));
        } catch (Exception e) {
            view.getTotalQuizzesValLabel().setText("0");
        }

        if (currentUser != null) {
            try {
                List<Result> results = resultService.getResultsByUserId(currentUser.getUserId());
                if (results != null && !results.isEmpty()) {
                    view.getCompletedValLabel().setText(String.valueOf(results.size()));

                    BigDecimal totalScore = BigDecimal.ZERO;
                    for (Result r : results) {
                        if (r.getScore() != null) {
                            totalScore = totalScore.add(r.getScore());
                        }
                    }
                    BigDecimal avg = totalScore.divide(BigDecimal.valueOf(results.size()), 1, RoundingMode.HALF_UP);
                    double pct = (avg.doubleValue() / 10.0) * 100;
                    view.getAvgScoreValLabel().setText(String.format("%.0f%%", pct));
                } else {
                    view.getCompletedValLabel().setText("0");
                    view.getAvgScoreValLabel().setText("0%");
                }
            } catch (Exception e) {
                view.getCompletedValLabel().setText("0");
                view.getAvgScoreValLabel().setText("0%");
            }
        }

        try {
            List<Topic> topics = topicService.getAllTopics();
            view.getTopicsListContainer().getChildren().clear();

            if (topics != null && !topics.isEmpty()) {
                for (int i = 0; i < Math.min(3, topics.size()); i++) {
                    Topic topic = topics.get(i);
                    List<Quiz> topicQuizzes = quizService.getPlayableQuizzesByTopicId(topic.getTopicId());
                    int quizCount = topicQuizzes != null ? topicQuizzes.size() : 0;

                    VBox topicCard = createTopicCard(topic.getTopicName(), quizCount, i == 0);
                    view.getTopicsListContainer().getChildren().add(topicCard);
                }

                Topic firstTopic = topics.get(0);
                view.getContinueQuizTitleLabel().setText(firstTopic.getTopicName() + " Practice");
                view.getContinueQuizSubtitleLabel().setText("Interactive assessment & questions");
            } else {
                Label noTopicLabel = new Label("No topics available yet.");
                noTopicLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
                view.getTopicsListContainer().getChildren().add(noTopicLabel);
            }
        } catch (Exception ignored) {
        }
    }

    private VBox createTopicCard(String topicName, int quizCount, boolean isActive) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-cursor: hand;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconL = new Label("📁");
        iconL.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-padding: 6 10; -fx-background-radius: 8px;");

        VBox titleBox = new VBox(2);
        Label titleL = new Label(topicName);
        titleL.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label countL = new Label(quizCount + " Quizzes");
        countL.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        titleBox.getChildren().addAll(titleL, countL);
        topRow.getChildren().addAll(iconL, titleBox);

        HBox bar = new HBox();
        bar.setPrefHeight(3);
        bar.setStyle(isActive ? "-fx-background-color: #6366f1; -fx-background-radius: 2px;" : "-fx-background-color: #e5e7eb; -fx-background-radius: 2px;");

        card.getChildren().addAll(topRow, bar);
        card.setOnMouseClicked(e -> SceneManager.showSelectQuiz());
        return card;
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

}
