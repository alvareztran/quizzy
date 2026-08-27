package com.quizzy.dao;

import com.quizzy.model.Answer;
import java.util.List;

public interface AnswerDAO {

    Answer findById(int answerId);
    List<Answer> findByQuestionId(int questionId);
    Answer findCorrectAnswer(int questionId);
    List<Answer> findAll();
    boolean insert(Answer answer);
    boolean update(Answer answer);
    boolean delete(int answerId);

}
