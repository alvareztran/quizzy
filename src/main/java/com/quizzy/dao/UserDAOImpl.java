package com.quizzy.dao;

import com.quizzy.model.User;
import com.quizzy.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class UserDAOImpl implements UserDAO {
    
    @Override
    public User findById(int userId) {
        
        String sql = """
                     SELECT * 
                     FROM Users 
                     WHERE UserID=?
                     """;
                
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("FullName"),
                        rs.getString("Role"),
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
    public User findByUsername(String username) {
        
        String sql = """
                     SELECT *
                     FROM Users
                     WHERE Username=?
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("FullName"),
                        rs.getString("Role"),
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
    public List<User> findAll() {
        
        List<User> users = new ArrayList<>();
        
        String sql = """
                     SELECT *
                     FROM Users
                     ORDER BY UserID
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("FullName"),
                    rs.getString("Role"),
                    toLocalDateTime(rs.getTimestamp("CreatedAt"))
                );
                users.add(user);
            }
           
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return users;
        
    }
    
    @Override
    public boolean insert(User user) {
        
        String sql = """
                     INSERT INTO Users (Username, Password, FullName, Role)
                     VALUES (?,?,?,?)
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
        
    }
    
    @Override
    public boolean update(User user) {
        
        String sql = """
                     UPDATE Users 
                     SET Username=?, 
                         Password=?, 
                         FullName=?, 
                         Role=?
                     WHERE UserID=?
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getUserId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
        
    }
    
    @Override
    public boolean delete(int userId) {
        
        String sql = """
                     DELETE FROM Users
                     WHERE UserID=?
                     """;
        
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);) {
            
            ps.setInt(1, userId);
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
