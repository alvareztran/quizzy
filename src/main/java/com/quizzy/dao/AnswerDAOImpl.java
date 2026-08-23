package com.quizzy.dao;

import com.quizzy.model.Answer;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAOImpl implements AnswerDAO {

    private Answer mapAnswer(ResultSet rs) throws SQLException {
        return new Answer(
                rs.getInt("AnswerID"),
                rs.getInt("QuestionID"),
                rs.getString("AnswerContent"),
                rs.getBoolean("IsCorrect")
        );
    }

    @Override
    public Answer findById(int answerId) {
        String sql = """
                     SELECT *
                     FROM Answer
                     WHERE AnswerID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAnswer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("AnswerDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Answer> findByQuestionId(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = """
                     SELECT * 
                     FROM Answer
                     WHERE QuestionID=?
                     ORDER BY AnswerID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.add(mapAnswer(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("AnswerDAO.findByQuestionId error: " + e.getMessage());
        }
        return answers;
    }

    @Override
    public Answer findCorrectAnswer(int questionId) {
        String sql = """
                     SELECT *
                     FROM Answer
                     WHERE QuestionID=? AND IsCorrect=1
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAnswer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("AnswerDAO.findCorrectAnswer error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Answer> findAll() {
        List<Answer> answers = new ArrayList<>();
        String sql = """
                     SELECT *
                     FROM Answer
                     ORDER BY AnswerID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                answers.add(mapAnswer(rs));
            }

        } catch (SQLException e) {
            System.err.println("AnswerDAO.findAll error: " + e.getMessage());
        }
        return answers;
    }

    @Override
    public boolean insert(Answer answer) {
        String sql = """
                     INSERT INTO Answer (QuestionID, AnswerContent, IsCorrect)
                     VALUES (?,?,?)
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerContent());
            ps.setBoolean(3, answer.isIsCorrect());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("AnswerDAO.insert error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Answer answer) {
        String sql = """
                     UPDATE Answer
                     SET QuestionID=?,
                         AnswerContent=?,
                         IsCorrect=?
                     WHERE AnswerID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerContent());
            ps.setBoolean(3, answer.isIsCorrect());
            ps.setInt(4, answer.getAnswerId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("AnswerDAO.update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int answerId) {
        String sql = """
                     DELETE FROM Answer
                     WHERE AnswerID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, answerId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("AnswerDAO.delete error: " + e.getMessage());
        }
        return false;
    }
}
