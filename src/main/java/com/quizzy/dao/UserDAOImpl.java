package com.quizzy.dao;

import com.quizzy.model.User;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private User mapUser(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedAt");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;
        return new User(
                rs.getInt("UserID"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("FullName"),
                rs.getString("Role"),
                createdAt
        );
    }

    @Override
    public User findById(int userId) {
        String sql = """
                     SELECT UserID, Username, Password, FullName, Role, CreatedAt
                     FROM Users 
                     WHERE UserID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("UserDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        String sql = """
                     SELECT UserID, Username, Password, FullName, Role, CreatedAt
                     FROM Users
                     WHERE Username=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("UserDAO.findByUsername error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = """
                     SELECT UserID, Username, Password, FullName, Role, CreatedAt
                     FROM Users
                     ORDER BY UserID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("UserDAO.findAll error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("UserDAO.insert error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("UserDAO.update error: " + e.getMessage());
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
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("UserDAO.delete error: " + e.getMessage());
        }
        return false;
    }
}
