package com.quizzy.dao;

import com.quizzy.model.Result;
import com.quizzy.util.DatabaseConnection;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ResultDAOImpl implements ResultDAO {

    private Result mapResult(ResultSet rs) throws SQLException {
        return new Result(
            rs.getInt("ResultID"),
            rs.getInt("UserID"),
            rs.getInt("QuizID"),
            rs.getBigDecimal("Score"),
            rs.getInt("TotalQuestions"),
            rs.getInt("CorrectAnswers"),
            rs.getTimestamp("StartedAt").toLocalDateTime(),
            rs.getTimestamp("FinishedAt").toLocalDateTime()
        );
    }

    @Override
    public Result findById(int resultId) {
        String sql = """
                     SELECT ResultID, UserID, QuizID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt
                     FROM Result
                     WHERE ResultID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resultId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResult(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("ResultDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Result> findByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        String sql = """
                     SELECT ResultID, UserID, QuizID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt
                     FROM Result
                     WHERE UserID=?
                     ORDER BY FinishedAt DESC, ResultID DESC
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResult(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("ResultDAO.findByUserId error: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Result> findByQuizId(int quizId) {
        List<Result> results = new ArrayList<>();
        String sql = """
                     SELECT ResultID, UserID, QuizID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt
                     FROM Result
                     WHERE QuizID=?
                     ORDER BY FinishedAt DESC, ResultID DESC
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResult(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("ResultDAO.findByQuizId error: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Result> findAll() {
        List<Result> results = new ArrayList<>();
        String sql = """
                     SELECT ResultID, UserID, QuizID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt
                     FROM Result
                     ORDER BY FinishedAt DESC, ResultID DESC
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(mapResult(rs));
            }

        } catch (SQLException e) {
            System.err.println("ResultDAO.findAll error: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Result> findTopResults(int limit) {
        List<Result> topResults = new ArrayList<>();
        String sql = """
                     SELECT TOP(?) ResultID, UserID, QuizID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt
                     FROM Result
                     ORDER BY Score DESC,
                              FinishedAt ASC
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topResults.add(mapResult(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return topResults;
    }

    @Override
    public boolean insert(Result result) {
        String sql = """
                     INSERT INTO Result (QuizID, UserID, Score, TotalQuestions, CorrectAnswers, StartedAt, FinishedAt)
                     VALUES (?,?,?,?,?,?,?)
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, result.getQuizId());
            ps.setInt(2, result.getUserId());
            ps.setBigDecimal(3, result.getScore());
            ps.setInt(4, result.getTotalQuestions());
            ps.setInt(5, result.getCorrectAnswers());
            ps.setTimestamp(6, Timestamp.valueOf(result.getStartedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(result.getFinishedAt()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        result.setResultId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int resultId) {
        String sql = """
                     DELETE FROM Result
                     WHERE ResultID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resultId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

}
