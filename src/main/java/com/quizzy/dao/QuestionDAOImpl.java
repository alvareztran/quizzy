package com.quizzy.dao;

import com.quizzy.model.Question;
import com.quizzy.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class QuestionDAOImpl implements QuestionDAO {
    
    @Override
    public Question findById(int questionId) {
        
        String sql = """
                     SELECT * 
                     FROM Question
                     WHERE QuestionID=?
                     """;
                
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new Question(
                        rs.getInt("QuestionID"),
                        rs.getInt("QuizID"),
                        rs.getString("Content"),
                        rs.getString("Difficulty"),
                        toLocalDateTime(rs.getTimestamp("CreatedAt"))
                    );
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
        
    }

    @Override
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        String sql = """
                     SELECT * 
                     FROM Question
                     ORDER BY QuestionID
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                Question question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("Content"),
                    rs.getString("Difficulty"),
                    toLocalDateTime(rs.getTimestamp("CreatedAt"))
                );
                questions.add(question);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return questions;
    }
    
    @Override
    public List<Question> findByQuizId(int quizId) {
        
        List<Question> questions = new ArrayList<>();
        
        String sql = """
                     SELECT * 
                     FROM Question
                     WHERE QuizID=?
                     ORDER BY QuestionID
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    Question question = new Question(
                        rs.getInt("QuestionID"),
                        rs.getInt("QuizID"),
                        rs.getString("Content"),
                        rs.getString("Difficulty"),
                        toLocalDateTime(rs.getTimestamp("CreatedAt"))
                    );
                    questions.add(question);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return questions;

    }
    
    @Override
    public List<Question> findRandomByQuizId(int quizId, int numberOfQuestions) {
        
        List<Question> questions = new ArrayList<>();
        
        String sql = """
                     SELECT TOP(?) *
                     FROM Question
                     WHERE QuizID=?
                     ORDER BY NEWID()
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, numberOfQuestions);
            ps.setInt(2, quizId);
            
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    Question question = new Question(
                        rs.getInt("QuestionID"),
                        rs.getInt("QuizID"),
                        rs.getString("Content"),
                        rs.getString("Difficulty"),
                        toLocalDateTime(rs.getTimestamp("CreatedAt"))
                    );
                    questions.add(question);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, question.getQuizId());
            ps.setString(2, question.getContent());
            ps.setString(3, question.getDifficulty());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
        
    }
    
    @Override
    public boolean update (Question question) {
        
        String sql = """
                     UPDATE Question 
                     SET QuizID=?, 
                         Content=?,
                         Difficulty=?
                     WHERE QuestionID=?
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, question.getQuizId());
            ps.setString(2, question.getContent());
            ps.setString(3, question.getDifficulty());
            ps.setInt(4, question.getQuestionId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
        
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

}
