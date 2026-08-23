package com.quizzy.dao;

import com.quizzy.model.Topic;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicDAOImpl implements TopicDAO {

    private Topic mapTopic(ResultSet rs) throws SQLException {
        return new Topic(
                rs.getInt("TopicID"),
                rs.getString("TopicName"),
                rs.getString("Description")
        );
    }

    @Override
    public Topic findById(int topicId) {
        String sql = """
                     SELECT * 
                     FROM Topic 
                     WHERE TopicID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, topicId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTopic(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("TopicDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Topic findByName(String topicName) {
        String sql = """
                     SELECT *
                     FROM Topic
                     WHERE TopicName=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, topicName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTopic(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("TopicDAO.findByName error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Topic> findAll() {
        List<Topic> topics = new ArrayList<>();
        String sql = """
                     SELECT *
                     FROM Topic
                     ORDER BY TopicID
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                topics.add(mapTopic(rs));
            }

        } catch (SQLException e) {
            System.err.println("TopicDAO.findAll error: " + e.getMessage());
        }
        return topics;
    }

    @Override
    public boolean insert(Topic topic) {
        String sql = """
                     INSERT INTO Topic (TopicName, Description)
                     VALUES (?,?)
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, topic.getTopicName());
            ps.setString(2, topic.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("TopicDAO.insert error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Topic topic) {
        String sql = """
                     UPDATE Topic 
                     SET TopicName=?, 
                         Description=?
                     WHERE TopicID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, topic.getTopicName());
            ps.setString(2, topic.getDescription());
            ps.setInt(3, topic.getTopicId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("TopicDAO.update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int topicId) {
        String sql = """
                     DELETE FROM Topic
                     WHERE TopicID=?
                     """;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, topicId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("TopicDAO.delete error: " + e.getMessage());
        }
        return false;
    }
}
