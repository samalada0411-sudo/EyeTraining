import model.DatabaseManager;

import java.sql.*;
import java.util.Scanner;

public class EyeTrainingApp {

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();  // Инициализация при запуске

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ТРЕНИРОВКА ГЛАЗ ===\n");
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Показать всех пользователей");
            System.out.println("3. Показать все упражнения");
            System.out.println("4. Добавить новое упражнение");
            System.out.println("5. Записать выполненную тренировку");
            System.out.println("6. Показать историю тренировок пользователя");
            System.out.println("7. Удалить пользователя");
            System.out.println("8. Удалить упражнение");
            System.out.println("0. Выход");

            System.out.print("\nВыберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // очистка

            switch (choice) {
                case 1 -> addUser(scanner);
                case 2 -> showAllUsers();
                case 3 -> showAllExercises();
                case 4 -> addExercise(scanner);
                case 5 -> recordTraining(scanner);
                case 6 -> showUserHistory(scanner);
                case 7 -> deleteUser(scanner);
                case 8 -> deleteExercise(scanner);
                case 0 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void addUser(Scanner sc) {
        System.out.print("Имя пользователя: ");
        String name = sc.nextLine();
        System.out.print("Возраст: ");
        int age = sc.nextInt();
        System.out.print("Часов за компьютером в день: ");
        int hours = sc.nextInt();

        String sql = "INSERT INTO users (name, age, work_hours) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, hours);
            pstmt.executeUpdate();
            System.out.println("Пользователь добавлен!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showAllUsers() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            System.out.println("\nПользователи:");
            while (rs.next()) {
                System.out.printf("ID: %d | %s | %d лет | %d ч/день%n",
                        rs.getInt("id"), rs.getString("name"),
                        rs.getInt("age"), rs.getInt("work_hours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showAllExercises() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM exercises")) {

            System.out.println("\nУпражнения:");
            while (rs.next()) {
                System.out.printf("ID: %d | %s (%d сек) — %s%n",
                        rs.getInt("id"), rs.getString("name"),
                        rs.getInt("duration"), rs.getString("method"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addExercise(Scanner sc) {
        System.out.print("Название: ");
        String name = sc.nextLine();
        System.out.print("Описание: ");
        String desc = sc.nextLine();
        System.out.print("Длительность (сек): ");
        int duration = sc.nextInt();
        sc.nextLine();
        System.out.print("Методика: ");
        String method = sc.nextLine();

        String sql = "INSERT INTO exercises (name, description, duration, method) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, desc);
            pstmt.setInt(3, duration);
            pstmt.setString(4, method);
            pstmt.executeUpdate();
            System.out.println("Упражнение добавлено!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void recordTraining(Scanner sc) {
        System.out.print("ID пользователя: ");
        int userId = sc.nextInt();
        System.out.print("ID упражнения: ");
        int exId = sc.nextInt();

        String sql = "INSERT INTO user_trainings (user_id, exercise_id, completed) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, exId);
            pstmt.executeUpdate();
            System.out.println("Тренировка записана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showUserHistory(Scanner sc) {
        System.out.print("ID пользователя: ");
        int userId = sc.nextInt();

        String sql = """
            SELECT t.training_date, e.name, e.duration 
            FROM user_trainings t 
            JOIN exercises e ON t.exercise_id = e.id 
            WHERE t.user_id = ? AND t.completed = 1 
            ORDER BY t.training_date DESC""";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nИстория тренировок:");
            while (rs.next()) {
                System.out.printf("%s | %s (%d сек)%n",
                        rs.getString("training_date"),
                        rs.getString("name"),
                        rs.getInt("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteUser(Scanner sc) {
        System.out.print("Введите ID пользователя для удаления: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Пользователь с ID " + id + " успешно удалён!");
            } else {
                System.out.println("Пользователь с таким ID не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении пользователя!");
            e.printStackTrace();
        }
    }

    private static void deleteExercise(Scanner sc) {
        System.out.print("Введите ID упражнения для удаления: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM exercises WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Упражнение с ID " + id + " успешно удалено!");
            } else {
                System.out.println("Упражнение с таким ID не найдено.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении упражнения!");
            e.printStackTrace();
        }
    }
}