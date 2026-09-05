package com.quizzy.dao;

import com.quizzy.model.Question;
import java.util.List;

public interface QuestionDAO {

    Question findById(int questionId);
    List<Question> findAll();
    List<Question> findByQuizId(int quizId);
    int countByQuizId(int quizId);
    List<Question> findRandomByQuizId(int quizId, int numberOfQuestions);
    boolean insert(Question question);
    boolean update (Question question);
    boolean delete(int questionId);

}
