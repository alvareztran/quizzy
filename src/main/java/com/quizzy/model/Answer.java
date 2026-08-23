package com.quizzy.model;

public class Answer {
    
    private int answerId;
    private int questionId;
    private String answerContent;
    private boolean isCorrect;

    public Answer() {
    }
    
    public Answer(int answerId, int questionId, String answerContent, boolean isCorrect) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answerContent = answerContent;
        this.isCorrect = isCorrect;
    }

    public Answer(int questionId, String answerContent, boolean isCorrect) {
        this.questionId = questionId;
        this.answerContent = answerContent;
        this.isCorrect = isCorrect;
    }
    
    public int getAnswerId() {
        return answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public boolean isIsCorrect() {
        return isCorrect;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    
}
