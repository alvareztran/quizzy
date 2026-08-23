package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.User;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.ResultView;
import java.time.Duration;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ResultController {

    private final ResultView view;
    private final ResultService resultService = ServiceFactory.getResultService();
    private final QuizService quizService = ServiceFactory.getQuizService();

    public ResultController() {
        this.view = new ResultView();
        initEventHandlers();
        loadResultData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public ResultView getResultView() {
        return view;
    }

    private void initEventHandlers() {
        view.getNavDashboardBtn().setOnAction(e -> backToDashboard());
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showResult());

        view.getTryAgainBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getBackDashboardBtn().setOnAction(e -> backToDashboard());
        view.getReviewAnswersBtn().setOnAction(e -> SceneManager.showSelectQuiz());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void loadResultData() {
        Result result = SessionManager.getLastResult();
        User currentUser = SessionManager.getCurrentUser();

        if (result == null && currentUser != null) {
            try {
                List<Result> userResults = resultService.getResultsByUserId(currentUser.getUserId());
                if (userResults != null && !userResults.isEmpty()) {
                    result = userResults.get(userResults.size() - 1);
                }
            } catch (Exception e) {
                // Graceful fallback
            }
        }

        if (result != null) {
            populateResultDetails(result);
        } else {
            // Default placeholder display
            view.getQuizTitleLabel().setText("General Practice Quiz");
            view.getPercentDisplayLabel().setText("82%");
            view.getCorrectRatioBadgeLabel().setText("8 / 10 Correct");
            view.getCorrectValLabel().setText("8");
            view.getIncorrectValLabel().setText("2");
            view.getAccuracyValLabel().setText("80%");
            view.getDurationValLabel().setText("12:34");

            populateSampleQuestionReview(8, 10);
        }
    }

    private void populateResultDetails(Result result) {
        int totalQ = result.getTotalQuestions() > 0 ? result.getTotalQuestions() : 10;
        int correctQ = result.getCorrectAnswers();
        int incorrectQ = Math.max(0, totalQ - correctQ);

        double pct = (totalQ > 0) ? ((double) correctQ / totalQ) * 100 : 0;
        String pctStr = String.format("%.0f%%", pct);

        view.getPercentDisplayLabel().setText(pctStr);
        view.getCorrectRatioBadgeLabel().setText(correctQ + " / " + totalQ + " Correct");
        view.getCorrectValLabel().setText(String.valueOf(correctQ));
        view.getIncorrectValLabel().setText(String.valueOf(incorrectQ));
        view.getAccuracyValLabel().setText(pctStr);

        // Calculate Duration
        if (result.getStartedAt() != null && result.getFinishedAt() != null) {
            Duration d = Duration.between(result.getStartedAt(), result.getFinishedAt());
            long mins = Math.max(0, d.toMinutes());
            long secs = Math.max(0, d.toSecondsPart());
            view.getDurationValLabel().setText(String.format("%02d:%02d", mins, secs));
        } else {
            view.getDurationValLabel().setText("12:34");
        }

        // Quiz Title
        try {
            Quiz quiz = quizService.getQuizById(result.getQuizId());
            if (quiz != null && quiz.getQuizName() != null) {
                view.getQuizTitleLabel().setText(quiz.getQuizName());
            }
        } catch (Exception e) {
            // Keep default
        }

        populateSampleQuestionReview(correctQ, totalQ);
    }

    private void populateSampleQuestionReview(int correctCount, int totalCount) {
        view.getQuestionReviewBox().getChildren().clear();

        for (int i = 1; i <= Math.min(6, totalCount); i++) {
            boolean isCorrect = (i <= correctCount);
            HBox itemRow = new HBox(14);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setPadding(new Insets(14, 20, 14, 20));
            itemRow.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 1px 0; -fx-cursor: hand;");

            Label statusIcon = new Label(isCorrect ? "✓" : "✕");
            statusIcon.setAlignment(Pos.CENTER);
            statusIcon.setPrefSize(24, 24);
            statusIcon.setStyle(isCorrect
                    ? "-fx-background-color: #d1fae5; -fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 12px;"
                    : "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 12px;");

            Label qTitleL = new Label("Question " + i);
            qTitleL.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label arrowL = new Label("›");
            arrowL.setStyle("-fx-font-size: 18px; -fx-text-fill: #94a3b8;");

            itemRow.getChildren().addAll(statusIcon, qTitleL, spacer, arrowL);
            view.getQuestionReviewBox().getChildren().add(itemRow);
        }
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
