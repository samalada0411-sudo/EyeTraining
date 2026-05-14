import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:eye_training.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    work_hours INTEGER NOT NULL
                );""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS exercises (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    method TEXT NOT NULL
                );""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_trainings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    exercise_id INTEGER,
                    training_date DATE DEFAULT CURRENT_DATE,
                    completed BOOLEAN DEFAULT 0,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(exercise_id) REFERENCES exercises(id)
                );""");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (name, age, work_hours) VALUES ('Иван Иванов', 22, 8)");
                System.out.println("Создан тестовый пользователь: Иван Иванов (ID = 1)");
            }

            // Заполняем упражнения
            rs = stmt.executeQuery("SELECT COUNT(*) FROM exercises");
            if (rs.next() && rs.getInt(1) == 0) {
                fillInitialExercises(stmt);
            }

            System.out.println("База данных готова к работе!\n");

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД:");
            e.printStackTrace();
        }
    }

    private static void fillInitialExercises(Statement stmt) throws SQLException {
        String[][] data = {
                {"Пальминг", "Расслабление глаз тёплыми ладонями", "60", "Бейтса"},
                {"Круговые движения", "Медленные круги глазами", "40", "Стандартная"},
                {"Смена фокуса", "Ближний → дальний объект", "30", "Аветисова"},
                {"Частое моргание", "Лёгкое моргание 1 минуту", "60", "Гигиена зрения"},
                {"Движения по сторонам", "Вверх-вниз, влево-вправо", "45", "Стандартная"}
        };

        for (String[] ex : data) {
            stmt.execute(String.format(
                    "INSERT INTO exercises (name, description, duration, method) " +
                            "VALUES ('%s', '%s', %s, '%s')", ex[0], ex[1], ex[2], ex[3]));
        }
        System.out.println("   Добавлено 5 упражнений.");
    }
}