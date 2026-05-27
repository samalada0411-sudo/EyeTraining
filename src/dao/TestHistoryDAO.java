package dao;

import model.DatabaseManager;
import model.TestHistoryEntry;
import java.sql.*;
import java.util.*;

public class TestHistoryDAO {

    public void save(int userId, int totalScore, String loadLevel) throws SQLException {
        String sql = "INSERT INTO test_history (user_id, total_score, load_level) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, totalScore);
            pstmt.setString(3, loadLevel);
            pstmt.executeUpdate();
        }
    }

    public List<TestHistoryEntry> findByUserId(int userId) throws SQLException {
        List<TestHistoryEntry> history = new ArrayList<>();
        String sql = "SELECT * FROM test_history WHERE user_id = ? ORDER BY test_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(new TestHistoryEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("test_date"),
                        rs.getInt("total_score"),
                        rs.getString("load_level")
                ));
            }
        }
        return history;
    }
}