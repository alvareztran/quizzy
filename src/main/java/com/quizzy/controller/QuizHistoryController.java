package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.User;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.QuizHistoryView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class QuizHistoryController {

    private final QuizHistoryView view;
    private final ResultService resultService = ServiceFactory.getResultService();
    private final QuizService quizService = ServiceFactory.getQuizService();

    public QuizHistoryController() {
        this.view = new QuizHistoryView();
        initEventHandlers();
        loadHistoryData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public QuizHistoryView getQuizHistoryView() {
        return view;
    }

    private void initEventHandlers() {
        view.getNavDashboardBtn().setOnAction(e -> backToDashboard());
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showHistory());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void loadHistoryData() {
        User currentUser = SessionManager.getCurrentUser();
        view.getHistoryListContainer().getChildren().clear();

        if (currentUser == null) {
            populatePlaceholderHistory();
            return;
        }

        try {
            List<Result> results = resultService.getResultsByUserId(currentUser.getUserId());

            if (results == null || results.isEmpty()) {
                populatePlaceholderHistory();
                return;
            }

            // Calculate Statistics
            int total = results.size();
            view.getTotalQuizzesValLabel().setText(String.valueOf(total));

            BigDecimal totalScore = BigDecimal.ZERO;
            BigDecimal maxScore = BigDecimal.ZERO;

            for (Result r : results) {
                if (r.getScore() != null) {
                    totalScore = totalScore.add(r.getScore());
                    if (r.getScore().compareTo(maxScore) > 0) {
                        maxScore = r.getScore();
                    }
                }
            }

            BigDecimal avg = totalScore.divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
            double avgPct = (avg.doubleValue() / 10.0) * 100;
            double maxPct = (maxScore.doubleValue() / 10.0) * 100;

            view.getAvgScoreValLabel().setText(String.format("%.0f%%", avgPct));
            view.getBestScoreValLabel().setText(String.format("%.0f%%", maxPct));

            // Render History Cards
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (int i = results.size() - 1; i >= 0; i--) {
                Result r = results.get(i);
                String quizTitle = "General Practice Quiz";

                try {
                    Quiz quiz = quizService.getQuizById(r.getQuizId());
                    if (quiz != null && quiz.getQuizName() != null) {
                        quizTitle = quiz.getQuizName();
                    }
                } catch (Exception e) {
                    // Fallback
                }

                String dateStr = r.getFinishedAt() != null ? r.getFinishedAt().format(formatter) : "Aug 22, 2026";
                double scorePct = r.getScore() != null ? (r.getScore().doubleValue() / 10.0) * 100 : 82.0;

                VBox card = createHistoryCard(quizTitle, dateStr, String.format("%.0f%%", scorePct), i == results.size() - 1, r);
                view.getHistoryListContainer().getChildren().add(card);
            }

        } catch (Exception e) {
            populatePlaceholderHistory();
        }
    }

    private void populatePlaceholderHistory() {
        view.getTotalQuizzesValLabel().setText("12");
        view.getAvgScoreValLabel().setText("85%");
        view.getBestScoreValLabel().setText("98%");

        VBox c1 = createHistoryCard("Computer Science Basic", "Aug 22, 2026", "82%", true, null);
        VBox c2 = createHistoryCard("Java Programming", "Aug 20, 2026", "90%", false, null);
        VBox c3 = createHistoryCard("Database Fundamentals", "Aug 15, 2026", "75%", false, null);

        view.getHistoryListContainer().getChildren().addAll(c1, c2, c3);
    }

    private VBox createHistoryCard(String quizTitle, String dateStr, String scorePctStr, boolean isPrimary, Result resultObj) {
        VBox card = new VBox();
        card.setPadding(new Insets(18, 24, 18, 24));
        card.getStyleClass().add("card");
        card.setStyle(isPrimary
                ? "-fx-background-color: #ffffff; -fx-border-color: #6366f1; -fx-border-width: 1.5px; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-cursor: hand;"
                : "-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1px; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-cursor: hand;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        String iconStr = quizTitle.toLowerCase().contains("data") ? "≡" : quizTitle.toLowerCase().contains("java") ? "💻" : "💻";
        Label iconL = new Label(iconStr);
        iconL.setAlignment(Pos.CENTER);
        iconL.setPrefSize(42, 42);
        iconL.setStyle(isPrimary
                ? "-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 16px; -fx-background-radius: 10px;"
                : "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 16px; -fx-background-radius: 10px;");

        VBox titleBox = new VBox(3);
        Label titleL = new Label(quizTitle);
        titleL.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label dateL = new Label(dateStr);
        dateL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        titleBox.getChildren().addAll(titleL, dateL);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scoreL = new Label(scorePctStr);
        scoreL.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");

        Label arrowL = new Label("›");
        arrowL.setStyle("-fx-font-size: 20px; -fx-text-fill: #94a3b8;");

        row.getChildren().addAll(iconL, titleBox, spacer, scoreL, arrowL);
        card.getChildren().add(row);

        card.setOnMouseClicked(e -> {
            if (resultObj != null) {
                SessionManager.setLastResult(resultObj);
            }
            SceneManager.showResult();
        });

        return card;
    }

    private void backToDashboard() {
        if (SessionManager.isAdmin()) {
            SceneManager.showMain();
        } else {
            SceneManager.showPlayerDashboard();
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

}
