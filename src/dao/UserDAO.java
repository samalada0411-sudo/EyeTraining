package dao;

import model.User;
import model.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Сохранить нового пользователя
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (name, age, work_hours, glasses, eye_disease, vision_acuity) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setInt(2, user.getAge());
            pstmt.setInt(3, user.getWorkHours());
            pstmt.setBoolean(4, user.isGlasses());
            pstmt.setString(5, user.getEyeDisease());
            pstmt.setString(6, user.getVisionAcuity());
            pstmt.executeUpdate();

            // Получаем сгенерированный ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        }
    }

    // Найти пользователя по ID
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    // Получить всех пользователей
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // Обновить данные пользователя
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, age=?, work_hours=?, glasses=?, eye_disease=?, vision_acuity=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setInt(2, user.getAge());
            pstmt.setInt(3, user.getWorkHours());
            pstmt.setBoolean(4, user.isGlasses());
            pstmt.setString(5, user.getEyeDisease());
            pstmt.setString(6, user.getVisionAcuity());
            pstmt.setInt(7, user.getId());
            pstmt.executeUpdate();
        }
    }

    // Удалить пользователя
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // превращает ResultSet в объект User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setAge(rs.getInt("age"));
        user.setWorkHours(rs.getInt("work_hours"));
        user.setGlasses(rs.getBoolean("glasses"));
        user.setEyeDisease(rs.getString("eye_disease"));
        user.setVisionAcuity(rs.getString("vision_acuity"));
        return user;
    }
}