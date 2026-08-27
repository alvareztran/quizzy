package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Question;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.ResultDetail;
import com.quizzy.model.User;
import com.quizzy.service.AnswerService;
import com.quizzy.service.QuestionService;
import com.quizzy.service.ResultDetailService;
import com.quizzy.service.ResultService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.TakeQuizView;
import com.quizzy.view.component.SubmitQuizModal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class TakeQuizController {

    private final TakeQuizView view;
    private final QuestionService questionService = ServiceFactory.getQuestionService();
    private final AnswerService answerService = ServiceFactory.getAnswerService();
    private final ResultService resultService = ServiceFactory.getResultService();
    private final ResultDetailService resultDetailService = ServiceFactory.getResultDetailService();
    private final Map<Integer, Answer> selectedAnswerMap = new HashMap<>();

    private Quiz selectedQuiz;
    private List<Question> questionList;
    private int currentQuestionIndex;
    private ToggleGroup answerToggleGroup;

    private Timeline countdownTimeline;
    private int remainingSeconds;
    private boolean isSubmitted = false;

    public TakeQuizController() {
        this.view = new TakeQuizView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public TakeQuizView getTakeQuizView() {
        return view;
    }

    private void initEventHandlers() {
        view.getBackBtn().setOnAction(e -> confirmAndExitQuiz());
        view.getPreviousButton().setOnAction(e -> previousQuestion());
        view.getNextButton().setOnAction(e -> nextQuestion());
    }

    private void initializeData() {
        selectedQuiz = SessionManager.getSelectedQuiz();

        if (selectedQuiz == null) {
            showError("Please select a quiz first.");
            Platform.runLater(() -> SceneManager.showSelectQuiz());
            return;
        }

        view.getQuizNameLabel().setText(selectedQuiz.getQuizName());
        questionList = questionService.getRandomQuestionsByQuizId(
                selectedQuiz.getQuizId(),
                selectedQuiz.getNumberOfQuestions()
        );

        if (questionList == null || questionList.isEmpty()) {
            showError("No questions found for this quiz.");
            Platform.runLater(() -> SceneManager.showSelectQuiz());
            return;
        }

        SessionManager.setQuizStartTime(LocalDateTime.now());
        currentQuestionIndex = 0;
        showQuestion();

        startTimer();
    }

    private void startTimer() {
        int timeLimitMinutes = selectedQuiz.getTimeLimit();
        if (timeLimitMinutes <= 0) {
            timeLimitMinutes = 15;
        }

        remainingSeconds = timeLimitMinutes * 60;
        updateTimerDisplay();

        countdownTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> {
            remainingSeconds--;
            updateTimerDisplay();

            if (remainingSeconds <= 0) {
                stopTimer();
                handleTimeout();
            }
        }));

        countdownTimeline.setCycleCount(Animation.INDEFINITE);
        countdownTimeline.play();
    }

    private void stopTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    private void updateTimerDisplay() {
        int minutes = Math.max(0, remainingSeconds / 60);
        int seconds = Math.max(0, remainingSeconds % 60);
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        view.getTimerLabel().setText("⏱ " + formattedTime);

        if (remainingSeconds <= 60) {
            view.getTimerLabel().setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 20px;");
        } else {
            view.getTimerLabel().setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 20px;");
        }
    }

    private void handleTimeout() {
        if (isSubmitted) {
            return;
        }

        saveSelectedAnswer();
        finishQuiz();
    }

    private void previousQuestion() {
        saveSelectedAnswer();

        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion();
        }
    }

    private void nextQuestion() {
        saveSelectedAnswer();

        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            showQuestion();
            return;
        }

        confirmAndFinishQuiz();
    }

    private void confirmAndFinishQuiz() {
        saveSelectedAnswer();

        int answeredCount = 0;
        int totalQuestions = questionList.size();
        Map<Integer, Boolean> questionAnsweredMap = new HashMap<>();

        for (int i = 0; i < totalQuestions; i++) {
            Question q = questionList.get(i);
            boolean isAns = selectedAnswerMap.containsKey(q.getQuestionId());
            if (isAns) {
                answeredCount++;
            }
            questionAnsweredMap.put(i + 1, isAns);
        }

        boolean confirmed = SubmitQuizModal.showConfirmation(answeredCount, totalQuestions, questionAnsweredMap);
        if (confirmed) {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        if (isSubmitted) {
            return;
        }
        isSubmitted = true;
        stopTimer();

        int correctAnswers = 0;
        int totalQuestions = questionList.size();

        for (Question question : questionList) {
            Answer selectedAnswer = selectedAnswerMap.get(question.getQuestionId());
            if (selectedAnswer != null && selectedAnswer.isIsCorrect()) {
                correctAnswers++;
            }
        }

        LocalDateTime startedAt = SessionManager.getQuizStartTime();
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        LocalDateTime finishedAt = LocalDateTime.now();

        BigDecimal score = resultService.calculateScore(correctAnswers, totalQuestions);

        User currentUser = SessionManager.getCurrentUser();
        int userId = currentUser != null ? currentUser.getUserId() : 1;

        Result result = new Result(
                userId,
                selectedQuiz.getQuizId(),
                score,
                totalQuestions,
                correctAnswers,
                startedAt,
                finishedAt
        );

        if (currentUser != null) {
            try {
                boolean created = resultService.createResult(result);
                if (created && result.getResultId() > 0) {
                    List<ResultDetail> details = new ArrayList<>();
                    for (int i = 0; i < questionList.size(); i++) {
                        Question q = questionList.get(i);
                        Answer chosenAnswer = selectedAnswerMap.get(q.getQuestionId());
                        if (chosenAnswer != null) {
                            details.add(new ResultDetail(i + 1, q.getQuestionId(), chosenAnswer.getAnswerId(), result.getResultId()));
                        }
                    }
                    if (!details.isEmpty()) {
                        resultDetailService.saveResultDetails(details);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error saving quiz result or details: " + e.getMessage());
            }
        }

        SessionManager.setLastResult(result);
        SceneManager.showResult();
    }

    private void confirmAndExitQuiz() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Quiz");
        alert.setHeaderText("Cancel Quiz Attempt?");
        alert.setContentText("Are you sure you want to exit? Your current quiz progress will be cancelled and will NOT be saved.");

        ButtonType exitBtn = new ButtonType("Exit & Discard", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Continue Quiz", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(exitBtn, cancelBtn);

        Optional<ButtonType> opt = alert.showAndWait();
        if (opt.isPresent() && opt.get() == exitBtn) {
            stopTimer();
            SessionManager.setQuizStartTime(null);
            SceneManager.showSelectQuiz();
        }
    }

    private void showQuestion() {
        Question question = questionList.get(currentQuestionIndex);
        List<Answer> answers = answerService.getAnswersByQuestionId(question.getQuestionId());

        view.getQuestionNumberLabel().setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
        view.getQuestionContentLabel().setText(question.getContent());

        view.getAnswerBox().getChildren().clear();
        answerToggleGroup = new ToggleGroup();

        if (answers == null || answers.isEmpty()) {
            Label noAnswerLabel = new Label("No answers found for this question.");
            noAnswerLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            view.getAnswerBox().getChildren().add(noAnswerLabel);
        } else {
            Answer selectedAnswer = selectedAnswerMap.get(question.getQuestionId());

            for (Answer answer : answers) {
                RadioButton answerBtn = new RadioButton(answer.getAnswerContent());
                answerBtn.setWrapText(true);
                answerBtn.setMaxWidth(Double.MAX_VALUE);
                answerBtn.setToggleGroup(answerToggleGroup);
                answerBtn.setUserData(answer);
                answerBtn.setPadding(new Insets(14, 18, 14, 18));
                answerBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 15px; -fx-text-fill: #334155; -fx-cursor: hand;");

                if (selectedAnswer != null && selectedAnswer.getAnswerId() == answer.getAnswerId()) {
                    answerBtn.setSelected(true);
                    answerBtn.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #6366f1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-cursor: hand;");
                }

                answerBtn.selectedProperty().addListener((obs, oldV, isSel) -> {
                    if (isSel) {
                        answerBtn.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #6366f1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-cursor: hand;");
                    } else {
                        answerBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 15px; -fx-text-fill: #334155; -fx-cursor: hand;");
                    }
                });

                view.getAnswerBox().getChildren().add(answerBtn);
            }
        }

        view.getPreviousButton().setDisable(currentQuestionIndex == 0);
        view.getNextButton().setText(currentQuestionIndex == questionList.size() - 1 ? "Finish & Submit →" : "Next →");

        renderStepper();
    }

    private void renderStepper() {
        view.getStepperBox().getChildren().clear();
        int total = questionList.size();

        for (int i = 0; i < total; i++) {
            int stepIndex = i;
            Question q = questionList.get(i);
            boolean isCurrent = (i == currentQuestionIndex);
            boolean isAnswered = selectedAnswerMap.containsKey(q.getQuestionId());

            Label stepDot = new Label(String.valueOf(i + 1));
            stepDot.setAlignment(Pos.CENTER);
            stepDot.setPrefSize(28, 28);

            if (isCurrent) {
                stepDot.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 14px; -fx-cursor: hand;");
            } else if (isAnswered) {
                stepDot.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 14px; -fx-cursor: hand;");
            } else {
                stepDot.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-size: 12px; -fx-background-radius: 14px; -fx-cursor: hand;");
            }

            stepDot.setOnMouseClicked(e -> {
                saveSelectedAnswer();
                currentQuestionIndex = stepIndex;
                showQuestion();
            });

            view.getStepperBox().getChildren().add(stepDot);
        }
    }

    private void saveSelectedAnswer() {
        if (answerToggleGroup == null) {
            return;
        }

        Toggle selectedToggle = answerToggleGroup.getSelectedToggle();

        if (selectedToggle == null) {
            return;
        }

        Question question = questionList.get(currentQuestionIndex);
        Answer answer = (Answer) selectedToggle.getUserData();
        selectedAnswerMap.put(question.getQuestionId(), answer);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Take Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
