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
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultDetailService;
import com.quizzy.service.ResultService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.HistoryDetailView;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryDetailController {

    private final HistoryDetailView view;
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final QuestionService questionService = ServiceFactory.getQuestionService();
    private final AnswerService answerService = ServiceFactory.getAnswerService();
    private final ResultService resultService = ServiceFactory.getResultService();
    private final ResultDetailService resultDetailService = ServiceFactory.getResultDetailService();

    private final List<VBox> questionCardNodes = new ArrayList<>();
    private final List<HBox> navIndexNodes = new ArrayList<>();
    private int currentSelectedQuestionIndex = 0;

    public HistoryDetailController() {
        this.view = new HistoryDetailView();
        initEventHandlers();
        loadHistoryDetail();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public HistoryDetailView getHistoryDetailView() {
        return view;
    }

    private void initEventHandlers() {
        view.getBackToHistoryBtn().setOnAction(e -> SceneManager.showHistory());
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showHistory());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> {
            SessionManager.clear();
            SceneManager.showLogin();
        });
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void loadHistoryDetail() {
        Result result = SessionManager.getLastResult();
        if (result == null) {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                try {
                    List<Result> userResults = resultService.getResultsByUserId(currentUser.getUserId());
                    if (userResults != null && !userResults.isEmpty()) {
                        result = userResults.get(userResults.size() - 1);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (result == null) {
            renderEmptyHistoryDetail();
            return;
        }

        try {
            Quiz quiz = quizService.getQuizById(result.getQuizId());
            String quizName = (quiz != null && quiz.getQuizName() != null) ? quiz.getQuizName() : ("Quiz #" + result.getQuizId());
            view.getQuizTitleInfoLabel().setText(quizName);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy  •  HH:mm");
            LocalDateTime finishTime = result.getFinishedAt() != null ? result.getFinishedAt() : result.getStartedAt();
            String dateStr = (finishTime != null) ? finishTime.format(dtf) : "-";
            view.getQuizDateInfoLabel().setText(dateStr);

            int totalQuestions = result.getTotalQuestions() > 0 ? result.getTotalQuestions() : 0;
            int correctCount = result.getCorrectAnswers();
            int incorrectCount = Math.max(0, totalQuestions - correctCount);

            view.getQuizQuestionsInfoLabel().setText(totalQuestions + " Questions");

            double scorePct = (totalQuestions > 0)
                    ? ((result.getScore() != null) ? (result.getScore().doubleValue() / 10.0) * 100 : ((double) correctCount / totalQuestions) * 100)
                    : 0;
            view.getPercentDisplayLabel().setText(String.format("%.0f%%", scorePct));
            view.getAccuracyPercentLabel().setText(String.format("%.0f%%", scorePct));

            if (scorePct >= 90) {
                view.getPraiseTitleLabel().setText("Outstanding! 🏆");
            } else if (scorePct >= 70) {
                view.getPraiseTitleLabel().setText("Great Job! 🎉");
            } else if (scorePct >= 50) {
                view.getPraiseTitleLabel().setText("Good Effort! 👍");
            } else {
                view.getPraiseTitleLabel().setText("Keep Practicing! 💪");
            }

            view.getPraiseSubtitleLabel().setText(String.format("You scored %d out of %d", correctCount, totalQuestions));
            view.getCorrectCountLabel().setText(String.valueOf(correctCount));
            view.getIncorrectCountLabel().setText(String.valueOf(incorrectCount));

            if (result.getStartedAt() != null && result.getFinishedAt() != null) {
                Duration dur = Duration.between(result.getStartedAt(), result.getFinishedAt());
                long minutes = Math.max(0, dur.toMinutes());
                long seconds = Math.max(0, dur.toSecondsPart());
                view.getTimeTakenLabel().setText(String.format("%02d:%02d", minutes, seconds));
            } else {
                view.getTimeTakenLabel().setText("00:00");
            }

            List<Question> questions = questionService.getQuestionsByQuizId(result.getQuizId());
            if (questions == null || questions.isEmpty()) {
                renderEmptyQuestionsList();
                return;
            }

            List<ResultDetail> details = resultDetailService.getResultDetailsByResultId(result.getResultId());
            Map<Integer, Integer> chosenAnswerMap = new HashMap<>();
            if (details != null) {
                for (ResultDetail rd : details) {
                    chosenAnswerMap.put(rd.getQuestionId(), rd.getAnswerId());
                }
            }

            renderQuestions(questions, chosenAnswerMap, correctCount);

        } catch (Exception e) {
            renderEmptyHistoryDetail();
        }
    }

    private void renderEmptyHistoryDetail() {
        view.getQuizTitleInfoLabel().setText("No Quiz Selected");
        view.getQuizDateInfoLabel().setText("-");
        view.getQuizQuestionsInfoLabel().setText("0 Questions");
        view.getPercentDisplayLabel().setText("0%");
        view.getPraiseTitleLabel().setText("No Result Available");
        view.getPraiseSubtitleLabel().setText("Please complete a quiz first.");
        view.getCorrectCountLabel().setText("0");
        view.getIncorrectCountLabel().setText("0");
        view.getAccuracyPercentLabel().setText("0%");
        view.getTimeTakenLabel().setText("00:00");

        renderEmptyQuestionsList();
    }

    private void renderEmptyQuestionsList() {
        view.getQuestionsIndexContainer().getChildren().clear();
        view.getQuestionCardsContainer().getChildren().clear();
        questionCardNodes.clear();
        navIndexNodes.clear();

        Label emptyLabel = new Label("No questions recorded for this quiz.");
        emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-weight: 600; -fx-padding: 24;");
        view.getQuestionCardsContainer().getChildren().add(emptyLabel);
    }

    private void renderQuestions(List<Question> questions, Map<Integer, Integer> chosenAnswerMap, int fallbackCorrectCount) {
        view.getQuestionsIndexContainer().getChildren().clear();
        view.getQuestionCardsContainer().getChildren().clear();
        questionCardNodes.clear();
        navIndexNodes.clear();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            final int qIndex = i;

            List<Answer> answers = null;
            try {
                answers = answerService.getAnswersByQuestionId(q.getQuestionId());
            } catch (Exception ignored) {
            }

            Integer chosenAnswerId = chosenAnswerMap.get(q.getQuestionId());
            boolean isQuestionCorrect = false;

            if (answers != null && !answers.isEmpty()) {
                if (chosenAnswerId != null) {
                    for (Answer a : answers) {
                        if (a.getAnswerId() == chosenAnswerId && a.isIsCorrect()) {
                            isQuestionCorrect = true;
                            break;
                        }
                    }
                } else if (chosenAnswerMap.isEmpty()) {
                    isQuestionCorrect = (i < fallbackCorrectCount);
                }
            }

            HBox navItem = createSidebarNavItem(i + 1, isQuestionCorrect, i == 0);
            navItem.setOnMouseClicked(e -> selectQuestion(qIndex));
            navIndexNodes.add(navItem);
            view.getQuestionsIndexContainer().getChildren().add(navItem);

            VBox card = createQuestionCard(i + 1, q.getContent(), answers, chosenAnswerId, isQuestionCorrect);
            questionCardNodes.add(card);
            view.getQuestionCardsContainer().getChildren().add(card);
        }
    }

    private HBox createSidebarNavItem(int number, boolean isCorrect, boolean isSelected) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));

        Label numLbl = new Label(String.valueOf(number));
        numLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusIcon = new Label(isCorrect ? "✓" : "✕");
        statusIcon.setAlignment(Pos.CENTER);
        statusIcon.setPrefSize(20, 20);
        statusIcon.setMinSize(20, 20);
        statusIcon.setMaxSize(20, 20);

        if (isCorrect) {
            statusIcon.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-font-size: 11px; -fx-font-weight: 900; -fx-background-radius: 10px;");
        } else {
            statusIcon.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 11px; -fx-font-weight: 900; -fx-background-radius: 10px;");
        }

        row.getChildren().addAll(numLbl, spacer, statusIcon);

        if (isSelected) {
            row.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #c7d2fe; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        } else {
            row.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        }

        return row;
    }

    private void selectQuestion(int index) {
        if (index < 0 || index >= questionCardNodes.size()) {
            return;
        }

        for (int i = 0; i < navIndexNodes.size(); i++) {
            HBox item = navIndexNodes.get(i);
            if (i == index) {
                item.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #c7d2fe; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
            } else {
                item.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
            }
        }

        currentSelectedQuestionIndex = index;

        double totalCards = questionCardNodes.size();
        if (totalCards > 1) {
            double vVal = (double) index / (totalCards - 1);
            view.getScrollPane().setVvalue(vVal);
        }
    }

    private VBox createDetailedSampleCard(int num, String questionText, boolean isCorrect, String[] answers, int correctIdx, int chosenIdx, String explanation) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.03), 8, 0, 0, 2);");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label qTitle = new Label(num + ". " + questionText);
        qTitle.setWrapText(true);
        qTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        HBox.setHgrow(qTitle, Priority.ALWAYS);

        Label badge = new Label(isCorrect ? "✓ Correct" : "✕ Incorrect");
        badge.setAlignment(Pos.CENTER);
        if (isCorrect) {
            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 5 12; -fx-background-radius: 12px;");
        } else {
            badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 5 12; -fx-background-radius: 12px;");
        }

        topRow.getChildren().addAll(qTitle, badge);

        VBox optionsBox = new VBox(8);
        for (int i = 0; i < answers.length; i++) {
            HBox optRow = new HBox(10);
            optRow.setAlignment(Pos.CENTER_LEFT);
            optRow.setPadding(new Insets(10, 14, 10, 14));

            boolean isThisCorrect = (i == correctIdx);
            boolean isThisChosen = (i == chosenIdx);

            Label radioCircle = new Label(isThisChosen ? "◉" : "○");
            Label optText = new Label(answers[i]);
            HBox.setHgrow(optText, Priority.ALWAYS);

            optRow.getChildren().addAll(radioCircle, optText);

            if (isThisCorrect) {

                radioCircle.setStyle("-fx-font-size: 14px; -fx-text-fill: #16a34a;");
                optText.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #15803d;");
                optRow.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-border-radius: 8px; -fx-background-radius: 8px;");
                Label checkL = new Label("✓");
                checkL.setStyle("-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #16a34a;");
                optRow.getChildren().add(checkL);
            } else if (isThisChosen) {

                radioCircle.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626;");
                optText.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #b91c1c;");
                optRow.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #fca5a5; -fx-border-radius: 8px; -fx-background-radius: 8px;");
                Label crossL = new Label("✕");
                crossL.setStyle("-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #dc2626;");
                optRow.getChildren().add(crossL);
            } else {

                radioCircle.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");
                optText.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #334155;");
                optRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #f1f5f9; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            }

            optionsBox.getChildren().add(optRow);
        }

        VBox expBox = new VBox(4);
        expBox.setPadding(new Insets(10, 14, 10, 14));
        if (isCorrect) {
            expBox.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #dcfce7; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        } else {
            expBox.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #fee2e2; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        }

        Label expLabel = new Label("Explanation: " + explanation);
        expLabel.setWrapText(true);
        expLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + (isCorrect ? "#166534" : "#991b1b") + ";");
        expBox.getChildren().add(expLabel);

        card.getChildren().addAll(topRow, optionsBox, expBox);
        return card;
    }

    private VBox createQuestionCard(int num, String questionText, List<Answer> answers, Integer chosenAnswerId, boolean isCorrect) {
        String[] opts;
        int correctIdx = 0;
        int chosenIdx = -1;

        if (answers != null && !answers.isEmpty()) {
            opts = new String[answers.size()];
            char prefix = 'A';
            for (int i = 0; i < answers.size(); i++) {
                Answer a = answers.get(i);
                opts[i] = (char) (prefix + i) + ". " + a.getAnswerContent();
                if (a.isIsCorrect()) {
                    correctIdx = i;
                }
                if (chosenAnswerId != null && a.getAnswerId() == chosenAnswerId) {
                    chosenIdx = i;
                }
            }
        } else {
            opts = new String[]{"A. Option A", "B. Option B", "C. Option C", "D. Option D"};
        }

        if (chosenIdx == -1 && chosenAnswerId == null) {
            chosenIdx = isCorrect ? correctIdx : (correctIdx == 0 ? 1 : 0);
        }

        String explanation = isCorrect
                ? "Correct choice based on standard course specifications."
                : "The correct answer is " + opts[correctIdx] + ". Please review this topic.";

        return createDetailedSampleCard(num, questionText, isCorrect, opts, correctIdx, chosenIdx, explanation);
    }
}
