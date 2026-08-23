package com.quizzy.service;

import com.quizzy.dao.QuestionDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.Question;
import com.quizzy.util.SessionManager;
import java.util.List;
import java.util.Objects;

public class QuestionService {

    private final QuestionDAO questionDAO;

    public QuestionService() {
        this(DAOFactory.getQuestionDAO());
    }

    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = Objects.requireNonNull(questionDAO);
    }

    public Question getQuestionById(int questionId) {
        if (questionId <= 0) {
            return null;
        }
        return questionDAO.findById(questionId);
    }

    public List<Question> getAllQuestions() {
        return questionDAO.findAll();
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        if (quizId <= 0) {
            return List.of();
        }
        return questionDAO.findByQuizId(quizId);
    }

    public List<Question> getRandomQuestionsByQuizId(int quizId, int numberOfQuestions) {
        if (quizId <= 0 || numberOfQuestions <= 0) {
            return List.of();
        }
        return questionDAO.findRandomByQuizId(quizId, numberOfQuestions);
    }

    public boolean createQuestion(Question question) {
        SessionManager.requireAdmin();

        if (question == null) {
            return false;
        }

        if (question.getQuizId() <= 0) {
            return false;
        }

        if (question.getContent() == null || question.getContent().isBlank()) {
            return false;
        }

        if (question.getDifficulty() == null || question.getDifficulty().isBlank()) {
            return false;
        }

        if (!question.getDifficulty().equals("Easy")
                && !question.getDifficulty().equals("Medium")
                && !question.getDifficulty().equals("Hard")) {
            return false;
        }

        return questionDAO.insert(question);
    }

    public boolean updateQuestion(Question question) {
        SessionManager.requireAdmin();

        if (question == null) {
            return false;
        }

        if (question.getQuizId() <= 0) {
            return false;
        }

        if (question.getContent() == null || question.getContent().isBlank()) {
            return false;
        }

        if (question.getDifficulty() == null || question.getDifficulty().isBlank()) {
            return false;
        }

        if (!question.getDifficulty().equals("Easy")
                && !question.getDifficulty().equals("Medium")
                && !question.getDifficulty().equals("Hard")) {
            return false;
        }

        Question existingQuestion = questionDAO.findById(question.getQuestionId());

        if (existingQuestion == null) {
            return false;
        }

        return questionDAO.update(question);
    }

    public boolean deleteQuestion(int questionId) {
        SessionManager.requireAdmin();

        if (questionId <= 0) {
            return false;
        }

        Question existingQuestion = questionDAO.findById(questionId);
        if (existingQuestion == null) {
            return false;
        }

        return questionDAO.delete(questionId);
    }

}
