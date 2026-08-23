package com.quizzy.service;

import com.quizzy.dao.QuizDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.Quiz;
import com.quizzy.util.SessionManager;
import java.util.List;
import java.util.Objects;

public class QuizService {

    private final QuizDAO quizDAO;

    public QuizService() {
        this(DAOFactory.getQuizDAO());
    }

    public QuizService(QuizDAO quizDAO) {
        this.quizDAO = Objects.requireNonNull(quizDAO);
    }

    public Quiz getQuizById(int quizId) {
        if (quizId <= 0) {
            return null;
        }
        return quizDAO.findById(quizId);
    }

    public List<Quiz> getAllQuizzes() {
        return quizDAO.findAll();
    }

    public List<Quiz> getQuizzesByTopicId(int topicId) {
        if (topicId <= 0) {
            return List.of();
        }
        return quizDAO.findByTopicId(topicId);
    }

    public boolean createQuiz(Quiz quiz) {
        SessionManager.requireAdmin();

        if (quiz == null) {
            return false;
        }

        if (quiz.getTopicId() <= 0) {
            return false;
        }

        if (quiz.getQuizName() == null || quiz.getQuizName().isBlank()) {
            return false;
        }

        if (quiz.getNumberOfQuestions() < 0) {
            return false;
        }

        if (quiz.getTimeLimit() < 0) {
            return false;
        }

        return quizDAO.insert(quiz);
    }

    public boolean updateQuiz(Quiz quiz) {
        SessionManager.requireAdmin();

        if (quiz == null) {
            return false;
        }

        if (quiz.getTopicId() <= 0) {
            return false;
        }

        if (quiz.getQuizName() == null || quiz.getQuizName().isBlank()) {
            return false;
        }

        if (quiz.getNumberOfQuestions() < 0) {
            return false;
        }

        if (quiz.getTimeLimit() < 0) {
            return false;
        }

        Quiz existingQuiz = quizDAO.findById(quiz.getQuizId());

        if (existingQuiz == null) {
            return false;
        }

        return quizDAO.update(quiz);
    }

    public boolean deleteQuiz(int quizId) {
        SessionManager.requireAdmin();

        if (quizId <= 0) {
            return false;
        }

        Quiz existingQuiz = quizDAO.findById(quizId);
        if (existingQuiz == null) {
            return false;
        }

        return quizDAO.delete(quizId);
    }

}
