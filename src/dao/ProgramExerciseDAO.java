package dao;

import model.ProgramExercise;
import model.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramExerciseDAO {

    // Сохранить упражнение в программе
    public void save(ProgramExercise programExercise) throws SQLException {
        String sql = "INSERT INTO program_exercises (program_id, exercise_id, duration_sec, repeats, order_num) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, programExercise.getProgramId());
            pstmt.setInt(2, programExercise.getExerciseId());
            pstmt.setInt(3, programExercise.getDurationSec());
            pstmt.setInt(4, programExercise.getRepeats());
            pstmt.setInt(5, programExercise.getOrderNum());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                programExercise.setId(rs.getInt(1));
            }
        }
    }

    // Найти все упражнения в программе
    public List<ProgramExercise> findByProgramId(int programId) throws SQLException {
        List<ProgramExercise> programExercises = new ArrayList<>();
        String sql = "SELECT * FROM program_exercises WHERE program_id = ? ORDER BY order_num";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, programId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                programExercises.add(mapResultSetToProgramExercise(rs));
            }
        }
        return programExercises;
    }

    private ProgramExercise mapResultSetToProgramExercise(ResultSet rs) throws SQLException {
        ProgramExercise pe = new ProgramExercise();
        pe.setId(rs.getInt("id"));
        pe.setProgramId(rs.getInt("program_id"));
        pe.setExerciseId(rs.getInt("exercise_id"));
        pe.setDurationSec(rs.getInt("duration_sec"));
        pe.setRepeats(rs.getInt("repeats"));
        pe.setOrderNum(rs.getInt("order_num"));
        return pe;
    }
}