package com.quizzy.dao;

import com.quizzy.model.Answer;
import com.quizzy.util.DatabaseConnection;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class AnswerDAOImpl implements AnswerDAO {
    
    @Override
    public Answer findById(int answerId) {
        
        String sql = """
                     SELECT *
                     FROM Answer
                     WHERE AnswerID=?
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new Answer(
                        rs.getInt("AnswerID"),
                        rs.getInt("QuestionID"),
                        rs.getString("AnswerContent"),
                        rs.getBoolean("IsCorrect")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    Answer answer = new Answer(
                        rs.getInt("AnswerID"),
                        rs.getInt("QuestionID"),
                        rs.getString("AnswerContent"),
                        rs.getBoolean("IsCorrect")
                    );
                    answers.add(answer);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new Answer(
                        rs.getInt("AnswerID"),
                        rs.getInt("QuestionID"),
                        rs.getString("AnswerContent"),
                        rs.getBoolean("IsCorrect")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
                Answer answer = new Answer(
                        rs.getInt("AnswerID"),
                        rs.getInt("QuestionID"),
                        rs.getString("AnswerContent"),
                        rs.getBoolean("IsCorrect")
                    );
                answers.add(answer);
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerContent());
            ps.setBoolean(3, answer.isIsCorrect());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerContent());
            ps.setBoolean(3, answer.isIsCorrect());
            ps.setInt(4, answer.getAnswerId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, answerId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
        
    }
    
}
