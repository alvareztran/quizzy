package com.quizzy.dao;

import com.quizzy.model.Quiz;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizDAOImpl implements QuizDAO {

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedAt");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;
        return new Quiz(
                rs.getInt("QuizID"),
                rs.getInt("TopicID"),
                rs.getString("QuizName"),
                rs.getInt("NumberOfQuestions"),
                rs.getInt("TimeLimit"),
                createdAt
        );
    }

    @Override
    public Quiz findById(int quizId) {
        String sql = """
                     SELECT * 
                     FROM Quiz 
                     WHERE QuizID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuiz(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("QuizDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Quiz> findByTopicId(int topicId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = """
                     SELECT * 
                     FROM Quiz
                     WHERE TopicID=?
                     ORDER BY QuizID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, topicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapQuiz(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("QuizDAO.findByTopicId error: " + e.getMessage());
        }
        return quizzes;
    }

    @Override
    public Quiz findByName(String quizName) {
        String sql = """
                     SELECT *
                     FROM Quiz
                     WHERE QuizName=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, quizName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuiz(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("QuizDAO.findByName error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Quiz> findAll() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = """
                     SELECT *
                     FROM Quiz
                     ORDER BY QuizID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                quizzes.add(mapQuiz(rs));
            }

        } catch (SQLException e) {
            System.err.println("QuizDAO.findAll error: " + e.getMessage());
        }
        return quizzes;
    }

    @Override
    public boolean insert(Quiz quiz) {
        String sql = """
                     INSERT INTO Quiz (TopicID, QuizName, NumberOfQuestions, TimeLimit)
                     VALUES (?,?,?,?)
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quiz.getTopicId());
            ps.setString(2, quiz.getQuizName());
            ps.setInt(3, quiz.getNumberOfQuestions());
            ps.setInt(4, quiz.getTimeLimit());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuizDAO.insert error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Quiz quiz) {
        String sql = """
                     UPDATE Quiz 
                     SET TopicID=?,
                         QuizName=?, 
                         NumberOfQuestions=?, 
                         TimeLimit=?
                     WHERE QuizID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quiz.getTopicId());
            ps.setString(2, quiz.getQuizName());
            ps.setInt(3, quiz.getNumberOfQuestions());
            ps.setInt(4, quiz.getTimeLimit());
            ps.setInt(5, quiz.getQuizId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuizDAO.update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int quizId) {
        String sql = """
                     DELETE FROM Quiz
                     WHERE QuizID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuizDAO.delete error: " + e.getMessage());
        }
        return false;
    }
}
