package com.quizzy.dao;

import com.quizzy.model.Topic;
import java.util.List;

public interface TopicDAO {
    
    Topic findById(int topicId);
    Topic findByName(String topicName);
    List<Topic> findAll();
    boolean insert(Topic topic);
    boolean update(Topic topic);
    boolean delete(int topicId);
    
}
