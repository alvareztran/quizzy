package com.quizzy.service;

import com.quizzy.dao.ResultDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.Result;
import com.quizzy.model.User;
import com.quizzy.util.SessionManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class ResultService {

    private final ResultDAO resultDAO;

    public ResultService() {
        this(DAOFactory.getResultDAO());
    }

    public ResultService(ResultDAO resultDAO) {
        this.resultDAO = Objects.requireNonNull(resultDAO);
    }

    public Result getResultById(int resultId) {
        return resultDAO.findById(resultId);
    }

    public List<Result> getResultsByUserId(int userId) {
        if (userId <= 0) {
            return List.of();
        }
        User current = SessionManager.getCurrentUser();
        if (current != null && "Player".equalsIgnoreCase(current.getRole()) && current.getUserId() != userId) {
            throw new SecurityException("Access denied: You can only view your own results.");
        }
        return resultDAO.findByUserId(userId);
    }

    public List<Result> getResultsByQuizId(int quizId) {
        return resultDAO.findByQuizId(quizId);
    }

    public List<Result> getAllResults() {
        SessionManager.requireAdmin();
        return resultDAO.findAll();
    }

    public List<Result> getTopResults(int limit) {
        return resultDAO.findTopResults(limit);
    }

    public BigDecimal calculateScore(int correctAnswers, int totalQuestions) {
        if (totalQuestions <= 0) {
            return BigDecimal.ZERO;
        }
        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(correctAnswers)
                .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.TEN);
    }

    public boolean createResult(Result result) {
        if (result == null) {
            return false;
        }

        if (result.getQuizId() <= 0) {
            return false;
        }

        if (result.getUserId() <= 0) {
            return false;
        }

        if (result.getTotalQuestions() <= 0) {
            return false;
        }

        if (result.getCorrectAnswers() < 0 || result.getCorrectAnswers() > result.getTotalQuestions()) {
            return false;
        }

        if (result.getStartedAt() == null || result.getFinishedAt() == null) {
            return false;
        }

        if (!result.getFinishedAt().isAfter(result.getStartedAt())) {
            return false;
        }

        BigDecimal score = calculateScore(result.getCorrectAnswers(), result.getTotalQuestions());
        result.setScore(score);

        return resultDAO.insert(result);
    }

    public boolean deleteResult(int resultId) {
        SessionManager.requireAdmin();

        if (resultId <= 0) {
            return false;
        }

        Result existingResult = resultDAO.findById(resultId);
        if (existingResult == null) {
            return false;
        }

        return resultDAO.delete(resultId);
    }

}
