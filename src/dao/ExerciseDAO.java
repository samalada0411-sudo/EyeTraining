package dao;

import model.Exercise;
import model.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDAO {

    // Найти упражнение по ID
    public Exercise findById(int id) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExercise(rs);
            }
        }
        return null;
    }

    // Найти упражнения по типу (relaxation, focus, movement, massage)
    public List<Exercise> findByType(String type) throws SQLException {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT * FROM exercises WHERE type = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        }
        return exercises;
    }

    // Преобразование ResultSet в Exercise
    private Exercise mapResultSetToExercise(ResultSet rs) throws SQLException {
        Exercise exercise = new Exercise();
        exercise.setId(rs.getInt("id"));
        exercise.setName(rs.getString("name"));
        exercise.setDescription(rs.getString("description"));
        exercise.setDuration(rs.getInt("duration"));
        exercise.setMethod(rs.getString("method"));
        exercise.setHowToDo(rs.getString("how_to_do"));
        exercise.setType(rs.getString("type"));
        exercise.setDifficulty(rs.getInt("difficulty"));
        return exercise;
    }
}