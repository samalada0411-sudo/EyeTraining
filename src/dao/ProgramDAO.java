package dao;

import model.Program;
import model.DatabaseManager;
import java.sql.*;

public class ProgramDAO {

    // Сохранить программу
    public void save(Program program) throws SQLException {
        String sql = "INSERT INTO user_programs (user_id, program_type, generation_date, is_custom, total_duration) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, program.getUserId());
            pstmt.setString(2, program.getProgramType());
            pstmt.setDate(3, Date.valueOf(program.getGenerationDate()));
            pstmt.setBoolean(4, program.isCustom());
            pstmt.setInt(5, program.getTotalDuration());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                program.setId(rs.getInt(1));
            }
        }
    }

    // Найти последнюю программу пользователя
    public Program findLatestByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM user_programs WHERE user_id = ? ORDER BY generation_date DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProgram(rs);
            }
        }
        return null;
    }

    private Program mapResultSetToProgram(ResultSet rs) throws SQLException {
        Program program = new Program();
        program.setId(rs.getInt("id"));
        program.setUserId(rs.getInt("user_id"));
        program.setProgramType(rs.getString("program_type"));
        program.setGenerationDate(rs.getDate("generation_date").toLocalDate());
        program.setCustom(rs.getBoolean("is_custom"));
        program.setTotalDuration(rs.getInt("total_duration"));
        return program;
    }
    public Program findById(int id) throws SQLException {
        String sql = "SELECT * FROM user_programs WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProgram(rs);
            }
        }
        return null;
    }
}