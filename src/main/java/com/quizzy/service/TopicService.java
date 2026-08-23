package com.quizzy.service;

import com.quizzy.dao.TopicDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.Topic;
import com.quizzy.util.SessionManager;
import java.util.List;
import java.util.Objects;

public class TopicService {

    private final TopicDAO topicDAO;

    public TopicService() {
        this(DAOFactory.getTopicDAO());
    }

    public TopicService(TopicDAO topicDAO) {
        this.topicDAO = Objects.requireNonNull(topicDAO);
    }

    public Topic getTopicById(int topicId) {
        if (topicId <= 0) {
            return null;
        }
        return topicDAO.findById(topicId);
    }

    public List<Topic> getAllTopics() {
        return topicDAO.findAll();
    }

    public boolean createTopic(Topic topic) {
        SessionManager.requireAdmin();

        if (topic == null) {
            return false;
        }

        if (topic.getTopicName() == null || topic.getTopicName().isBlank()) {
            return false;
        }

        Topic existingTopic = topicDAO.findByName(topic.getTopicName());
        if (existingTopic != null) {
            return false;
        }

        return topicDAO.insert(topic);
    }

    public boolean updateTopic(Topic topic) {
        SessionManager.requireAdmin();

        if (topic == null) {
            return false;
        }

        if (topic.getTopicName() == null || topic.getTopicName().isBlank()) {
            return false;
        }

        Topic existingTopic = topicDAO.findById(topic.getTopicId());
        if (existingTopic == null) {
            return false;
        }

        Topic topicWithName = topicDAO.findByName(topic.getTopicName());
        if (topicWithName != null && topicWithName.getTopicId() != topic.getTopicId()) {
            return false;
        }

        return topicDAO.update(topic);
    }

    public boolean deleteTopic(int topicId) {
        SessionManager.requireAdmin();

        if (topicId <= 0) {
            return false;
        }

        Topic existingTopic = topicDAO.findById(topicId);
        if (existingTopic == null) {
            return false;
        }

        return topicDAO.delete(topicId);
    }

}
