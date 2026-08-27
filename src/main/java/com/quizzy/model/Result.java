package com.quizzy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Result {

    private int resultId;
    private int userId;
    private int quizId;
    private BigDecimal score;
    private int totalQuestions;
    private int correctAnswers;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public Result() {
    }

    public Result(int resultId, int userId, int quizId, BigDecimal score, int totalQuestions,
            int correctAnswer, LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.resultId = resultId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswer;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public Result(int userId, int quizId, BigDecimal score, int totalQuestions, int correctAnswers,
            LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public int getResultId() {
        return resultId;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswers = correctAnswer;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

}
