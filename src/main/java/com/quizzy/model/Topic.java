package com.quizzy.model;

public class Topic {
    
    private int topicId;
    private String topicName;
    private String description;

    public Topic() {
    }

    public Topic(int topicId, String topicName, String description) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.description = description;
    }

    public Topic(String topicName, String description) {
        this.topicName = topicName;
        this.description = description;
    }
    
    public int getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return topicName != null ? topicName : "";
    }
}
