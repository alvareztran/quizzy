package com.quizzy.service;

import com.quizzy.dao.AnswerDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.Answer;
import com.quizzy.util.SessionManager;
import java.util.List;
import java.util.Objects;

public class AnswerService {

    private final AnswerDAO answerDAO;

    public AnswerService() {
        this(DAOFactory.getAnswerDAO());
    }

    public AnswerService(AnswerDAO answerDAO) {
        this.answerDAO = Objects.requireNonNull(answerDAO);
    }

    public Answer getAnswerId(int answerId) {
        if (answerId <= 0) {
            return null;
        }
        return answerDAO.findById(answerId);
    }

    public Answer getAnswerById(int answerId) {
        return getAnswerId(answerId);
    }

    public List<Answer> getAnswersByQuestionId(int questionId) {
        if (questionId <= 0) {
            return null;
        }
        return answerDAO.findByQuestionId(questionId);
    }

    public Answer getCorrectAnswerByQuestionId(int questionId) {
        if (questionId <= 0) {
            return null;
        }
        return answerDAO.findCorrectAnswer(questionId);
    }

    public boolean createAnswer(Answer answer) {
        SessionManager.requireAdmin();

        if (answer == null) {
            return false;
        }

        if (answer.getQuestionId() <= 0) {
            return false;
        }

        if (answer.getAnswerContent() == null || answer.getAnswerContent().isBlank()) {
            return false;
        }

        return answerDAO.insert(answer);
    }

    public boolean updateAnswer(Answer answer) {
        SessionManager.requireAdmin();

        if (answer == null) {
            return false;
        }

        if (answer.getQuestionId() <= 0) {
            return false;
        }

        if (answer.getAnswerContent() == null || answer.getAnswerContent().isBlank()) {
            return false;
        }

        Answer existingAnswer = answerDAO.findById(answer.getAnswerId());

        if (existingAnswer == null) {
            return false;
        }

        return answerDAO.update(answer);
    }

    public boolean deleteAnswer(int answerId) {
        SessionManager.requireAdmin();

        if (answerId <= 0) {
            return false;
        }

        Answer existingAnswer = answerDAO.findById(answerId);
        if (existingAnswer == null) {
            return false;
        }

        return answerDAO.delete(answerId);
    }

}
