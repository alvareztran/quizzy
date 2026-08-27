package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.ResultDetail;
import com.quizzy.model.User;
import com.quizzy.service.AnswerService;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultDetailService;
import com.quizzy.service.ResultService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.ResultView;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ResultController {

    private final ResultView view;
    private final ResultService resultService = ServiceFactory.getResultService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final ResultDetailService resultDetailService = ServiceFactory.getResultDetailService();
    private final AnswerService answerService = ServiceFactory.getAnswerService();

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
            view.getReviewAnswersBtn().setOnAction(e -> SceneManager.showHistoryDetail());

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
            view.getQuizTitleLabel().setText("No Quiz Completed Yet");
            view.getPercentDisplayLabel().setText("0%");
            view.getCorrectRatioBadgeLabel().setText("0 / 0 Correct");
            view.getCorrectValLabel().setText("0");
            view.getIncorrectValLabel().setText("0");
            view.getAccuracyValLabel().setText("0%");
            view.getDurationValLabel().setText("00:00");

            populateQuestionReview(null, 0, 0);
        }
    }

    private void populateResultDetails(Result result) {
        int totalQ = result.getTotalQuestions() > 0 ? result.getTotalQuestions() : 0;
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
            view.getDurationValLabel().setText("00:00");
        }

        try {
            Quiz quiz = quizService.getQuizById(result.getQuizId());
            if (quiz != null && quiz.getQuizName() != null) {
                view.getQuizTitleLabel().setText(quiz.getQuizName());
            } else {
                view.getQuizTitleLabel().setText("Quiz #" + result.getQuizId());
            }
        } catch (Exception ignored) {
            view.getQuizTitleLabel().setText("Quiz #" + result.getQuizId());
        }

        populateQuestionReview(result, correctQ, totalQ);
    }

    private void populateQuestionReview(Result result, int correctCount, int totalCount) {
        view.getQuestionReviewBox().getChildren().clear();

        if (totalCount <= 0) {
            HBox emptyBox = new HBox();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(16, 20, 16, 20));
            Label emptyLbl = new Label("No questions recorded for this attempt.");
            emptyLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 600;");
            emptyBox.getChildren().add(emptyLbl);
            view.getQuestionReviewBox().getChildren().add(emptyBox);
            return;
        }

        List<ResultDetail> details = null;
        if (result != null && result.getResultId() > 0) {
            try {
                details = resultDetailService.getResultDetailsByResultId(result.getResultId());
            } catch (Exception ignored) {
            }
        }

        Map<Integer, Boolean> questionCorrectnessMap = new HashMap<>();
        if (details != null && !details.isEmpty()) {
            for (ResultDetail rd : details) {
                try {
                    Answer a = answerService.getAnswerById(rd.getAnswerId());
                    questionCorrectnessMap.put(rd.getOrderOfQuestion(), a != null && a.isIsCorrect());
                } catch (Exception ignored) {
                }
            }
        }

        HBox itemsRow = new HBox(16);
        itemsRow.setAlignment(Pos.CENTER);
        itemsRow.setPadding(new Insets(14, 20, 16, 20));

        int displayCount = Math.max(1, Math.min(totalCount, 15));

        for (int i = 1; i <= displayCount; i++) {
            boolean isCorrect;
            if (questionCorrectnessMap.containsKey(i)) {
                isCorrect = questionCorrectnessMap.get(i);
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

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }
}
