package com.quizzy.dao;

import com.quizzy.model.Quiz;
import java.util.List;

public interface QuizDAO {

    Quiz findById(int quizId);
    List<Quiz> findByTopicId(int topicId);
    Quiz findByName(String quizName);
    List<Quiz> findAll();
    boolean insert(Quiz quiz);
    boolean update(Quiz quiz);
    boolean delete(int quizId);

}
