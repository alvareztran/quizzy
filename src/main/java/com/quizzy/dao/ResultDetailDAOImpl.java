package com.quizzy.dao;

import com.quizzy.model.ResultDetail;
import com.quizzy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailDAOImpl implements ResultDetailDAO {

    private ResultDetail mapResultDetail(ResultSet rs) throws SQLException {
        return new ResultDetail(
                rs.getInt("ResultDetailID"),
                rs.getInt("OrderOfQuestion"),
                rs.getInt("QuestionID"),
                rs.getInt("AnswerID"),
                rs.getInt("ResultID")
        );
    }

    @Override
    public ResultDetail findById(int resultDetailId) {
        String sql = """
                     SELECT ResultDetailID, OrderOfQuestion, QuestionID, AnswerID, ResultID
                     FROM ResultDetail
                     WHERE ResultDetailID = ?
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resultDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultDetail(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("ResultDetailDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ResultDetail> findByResultId(int resultId) {
        List<ResultDetail> list = new ArrayList<>();
        String sql = """
                     SELECT ResultDetailID, OrderOfQuestion, QuestionID, AnswerID, ResultID
                     FROM ResultDetail
                     WHERE ResultID = ?
                     ORDER BY OrderOfQuestion ASC
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resultId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultDetail(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ResultDetailDAO.findByResultId error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean insert(ResultDetail resultDetail) {
        String sql = """
                     INSERT INTO ResultDetail (OrderOfQuestion, QuestionID, AnswerID, ResultID)
                     VALUES (?, ?, ?, ?)
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, resultDetail.getOrderOfQuestion());
            ps.setInt(2, resultDetail.getQuestionId());
            ps.setInt(3, resultDetail.getAnswerId());
            ps.setInt(4, resultDetail.getResultId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        resultDetail.setResultDetailId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ResultDetailDAO.insert error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertBatch(List<ResultDetail> resultDetails) {
        if (resultDetails == null || resultDetails.isEmpty()) {
            return true;
        }

        String sql = """
                     INSERT INTO ResultDetail (OrderOfQuestion, QuestionID, AnswerID, ResultID)
                     VALUES (?, ?, ?, ?)
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            for (ResultDetail rd : resultDetails) {
                ps.setInt(1, rd.getOrderOfQuestion());
                ps.setInt(2, rd.getQuestionId());
                ps.setInt(3, rd.getAnswerId());
                ps.setInt(4, rd.getResultId());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            return results.length == resultDetails.size();
        } catch (SQLException e) {
            System.err.println("ResultDetailDAO.insertBatch error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteByResultId(int resultId) {
        String sql = """
                     DELETE FROM ResultDetail
                     WHERE ResultID = ?
                     """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resultId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ResultDetailDAO.deleteByResultId error: " + e.getMessage());
        }
        return false;
    }
}
