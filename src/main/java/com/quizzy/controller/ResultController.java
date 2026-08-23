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
import javafx.scene.layout.StackPane;
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
        boolean isAdmin = SessionManager.isAdmin();
        view.setAdminMode(isAdmin);

        if (isAdmin) {
            view.getBackAdminBtn().setOnAction(e -> SceneManager.showAdminResult());
            view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showMain());
            view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showMain());
        } else {
            view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
            view.getNavHistoryBtn().setOnAction(e -> SceneManager.showHistory());

            view.getTryAgainBtn().setOnAction(e -> SceneManager.showSelectQuiz());
            view.getBackTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
            view.getReviewAnswersBtn().setOnAction(e -> SceneManager.showSelectQuiz());

            view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
            view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());
        }

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
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
            } catch (Exception ignored) {
            }
        }

        if (result != null) {
            populateResultDetails(result);
        } else {
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

        if (result.getStartedAt() != null && result.getFinishedAt() != null) {
            Duration d = Duration.between(result.getStartedAt(), result.getFinishedAt());
            long mins = Math.max(0, d.toMinutes());
            long secs = Math.max(0, d.toSecondsPart());
            view.getDurationValLabel().setText(String.format("%02d:%02d", mins, secs));
        } else {
            view.getDurationValLabel().setText("12:34");
        }

        try {
            Quiz quiz = quizService.getQuizById(result.getQuizId());
            if (quiz != null && quiz.getQuizName() != null) {
                view.getQuizTitleLabel().setText(quiz.getQuizName());
            }
        } catch (Exception ignored) {
        }

        populateSampleQuestionReview(correctQ, totalQ);
    }

    private void populateSampleQuestionReview(int correctCount, int totalCount) {
        view.getQuestionReviewBox().getChildren().clear();

        HBox itemsRow = new HBox(16);
        itemsRow.setAlignment(Pos.CENTER);
        itemsRow.setPadding(new Insets(14, 20, 16, 20));

        int displayCount = Math.max(1, Math.min(totalCount, 15));

        for (int i = 1; i <= displayCount; i++) {
            boolean isCorrect;
            if (displayCount == 10 && correctCount == 8) {
                isCorrect = (i != 2 && i != 9);
            } else {
                isCorrect = (i <= correctCount);
            }

            VBox qItem = new VBox(6);
            qItem.setAlignment(Pos.CENTER);

            StackPane badge = new StackPane();
            badge.setPrefSize(36, 36);
            badge.setMinSize(36, 36);
            badge.setMaxSize(36, 36);
            badge.setStyle(isCorrect
                    ? "-fx-background-color: #4f46e5; -fx-background-radius: 10px;"
                    : "-fx-background-color: #dc2626; -fx-background-radius: 10px;");

            Label symbol = new Label(isCorrect ? "✓" : "✕");
            symbol.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;");
            badge.getChildren().add(symbol);

            Label qNum = new Label(String.valueOf(i));
            qNum.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

            qItem.getChildren().addAll(badge, qNum);
            itemsRow.getChildren().add(qItem);
        }

        view.getQuestionReviewBox().getChildren().add(itemsRow);
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
