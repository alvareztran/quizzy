package com.quizzy.dao;

import com.quizzy.model.Question;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAOImpl implements QuestionDAO {

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedAt");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;
        return new Question(
                rs.getInt("QuestionID"),
                rs.getInt("QuizID"),
                rs.getString("Content"),
                rs.getString("Difficulty"),
                createdAt
        );
    }

    @Override
    public Question findById(int questionId) {
        String sql = """
                     SELECT QuestionID, QuizID, Content, Difficulty, CreatedAt
                     FROM Question
                     WHERE QuestionID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("QuestionDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        String sql = """
                     SELECT QuestionID, QuizID, Content, Difficulty, CreatedAt
                     FROM Question
                     ORDER BY QuestionID
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                questions.add(mapQuestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("QuestionDAO.findAll error: " + e.getMessage());
        }
        return questions;
    }

    @Override
    public List<Question> findByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                     SELECT QuestionID, QuizID, Content, Difficulty, CreatedAt
                     FROM Question
                     WHERE QuizID=?
                     ORDER BY QuestionID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapQuestion(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("QuestionDAO.findByQuizId error: " + e.getMessage());
        }
        return questions;
    }

    @Override
    public int countByQuizId(int quizId) {
        String sql = """
                     SELECT COUNT(*)
                     FROM Question
                     WHERE QuizID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("QuestionDAO.countByQuizId error: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Question> findRandomByQuizId(int quizId, int numberOfQuestions) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                     SELECT TOP(?) QuestionID, QuizID, Content, Difficulty, CreatedAt
                     FROM Question
                     WHERE QuizID=?
                     ORDER BY NEWID()
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, numberOfQuestions);
            ps.setInt(2, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapQuestion(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("QuestionDAO.findRandomByQuizId error: " + e.getMessage());
        }
        return questions;
    }

    @Override
    public boolean insert(Question question) {
        String sql = """
                     INSERT INTO Question (QuizID, Content, Difficulty)
                     VALUES (?,?,?)
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, question.getQuizId());
            ps.setString(2, question.getContent());
            ps.setString(3, question.getDifficulty());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuestionDAO.insert error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Question question) {
        String sql = """
                     UPDATE Question
                     SET QuizID=?,
                         Content=?,
                         Difficulty=?
                     WHERE QuestionID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, question.getQuizId());
            ps.setString(2, question.getContent());
            ps.setString(3, question.getDifficulty());
            ps.setInt(4, question.getQuestionId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuestionDAO.update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int questionId) {
        String sql = """
                     DELETE FROM Question
                     WHERE QuestionID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("QuestionDAO.delete error: " + e.getMessage());
        }
        return false;
    }
}
