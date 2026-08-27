package com.quizzy.model;

public class ResultDetail {

    private int resultDetailId;
    private int orderOfQuestion;
    private int questionId;
    private int answerId;
    private int resultId;

    public ResultDetail() {
    }

    public ResultDetail(int resultDetailId, int orderOfQuestion, int questionId, int answerId, int resultId) {
        this.resultDetailId = resultDetailId;
        this.orderOfQuestion = orderOfQuestion;
        this.questionId = questionId;
        this.answerId = answerId;
        this.resultId = resultId;
    }

    public ResultDetail(int orderOfQuestion, int questionId, int answerId, int resultId) {
        this.orderOfQuestion = orderOfQuestion;
        this.questionId = questionId;
        this.answerId = answerId;
        this.resultId = resultId;
    }

    public int getResultDetailId() {
        return resultDetailId;
    }

    public void setResultDetailId(int resultDetailId) {
        this.resultDetailId = resultDetailId;
    }

    public int getOrderOfQuestion() {
        return orderOfQuestion;
    }

    public void setOrderOfQuestion(int orderOfQuestion) {
        this.orderOfQuestion = orderOfQuestion;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    @Override
    public String toString() {
        return "ResultDetail{" +
                "resultDetailId=" + resultDetailId +
                ", orderOfQuestion=" + orderOfQuestion +
                ", questionId=" + questionId +
                ", answerId=" + answerId +
                ", resultId=" + resultId +
                '}';
    }
}
