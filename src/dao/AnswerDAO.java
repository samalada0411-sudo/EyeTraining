package dao;

import model.Answer;
import model.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {

    public void save(Answer answer) throws SQLException {
        String sql = "INSERT INTO user_answers (user_id, question_id, answer_value, answer_date) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, answer.getUserId());
            pstmt.setInt(2, answer.getQuestionId());
            pstmt.setInt(3, answer.getAnswerValue());
            pstmt.setDate(4, Date.valueOf(answer.getAnswerDate()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                answer.setId(rs.getInt(1));
            }
        }
    }

    public List<Answer> findByUserId(int userId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT * FROM user_answers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
        }
        return answers;
    }

    public void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM user_answers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public boolean hasTestToday(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_answers WHERE user_id = ? AND answer_date = CURRENT_DATE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Answer mapResultSetToAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("id"));
        answer.setUserId(rs.getInt("user_id"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setAnswerValue(rs.getInt("answer_value"));
        answer.setAnswerDate(rs.getDate("answer_date").toLocalDate());
        return answer;
    }
}