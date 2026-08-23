package com.quizzy.model;

import java.time.LocalDateTime;

public class Question {
    
    private int questionId;
    private int quizId;
    private String content;
    private String difficulty;
    private LocalDateTime createdAt;

    public Question() {
    }

    public Question(int questionId, int quizId, String content, String difficulty, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.content = content;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
    }

    public Question(int quizId, String content, String difficulty) {
        this.quizId = quizId;
        this.content = content;
        this.difficulty = difficulty;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getContent() {
        return content;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}
