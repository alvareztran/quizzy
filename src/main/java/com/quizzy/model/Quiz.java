package com.quizzy.model;

import java.time.LocalDateTime;

public class Quiz {
    
    private int quizId;
    private int topicId;
    private String quizName;
    private int numberOfQuestions;
    private int timeLimit;
    private LocalDateTime createdAt;

    public Quiz() {
    }

    public Quiz(int quizId, int topicId, String quizName, int numberOfQuestions, int timeLimit, LocalDateTime createdAt) {
        this.quizId = quizId;
        this.topicId = topicId;
        this.quizName = quizName;
        this.numberOfQuestions = numberOfQuestions;
        this.timeLimit = timeLimit;
        this.createdAt = createdAt;
    }

    public Quiz(int topicId, String quizName, int numberOfQuestions, int timeLimit) {
        this.topicId = topicId;
        this.quizName = quizName;
        this.numberOfQuestions = numberOfQuestions;
        this.timeLimit = timeLimit;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return quizName != null ? quizName : "";
    }
}
