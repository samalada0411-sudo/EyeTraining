import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:eye_training.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // ========== СУЩЕСТВУЮЩИЕ ТАБЛИЦЫ (НЕ ТРОГАЕМ) ==========

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


            // ========== НОВЫЕ ТАБЛИЦЫ ДЛЯ ТЕСТА И ПРОГРАММ ==========

            // 1. Таблица вопросов для теста
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question_text TEXT NOT NULL,
                    category TEXT NOT NULL,
                    weight INTEGER DEFAULT 1,
                    options TEXT
                );""");

            // 2. Таблица ответов пользователей
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_answers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    question_id INTEGER NOT NULL,
                    answer_value INTEGER NOT NULL,
                    answer_date DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(question_id) REFERENCES questions(id)
                );""");

            // 3. Таблица сгенерированных программ
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_programs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    program_type TEXT NOT NULL,
                    generation_date DATE DEFAULT CURRENT_DATE,
                    is_custom BOOLEAN DEFAULT 0,
                    total_duration INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                );""");

            // 4. Связующая таблица (программа → упражнения)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS program_exercises (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    program_id INTEGER NOT NULL,
                    exercise_id INTEGER NOT NULL,
                    duration_sec INTEGER NOT NULL,
                    repeats INTEGER DEFAULT 1,
                    order_num INTEGER NOT NULL,
                    FOREIGN KEY(program_id) REFERENCES user_programs(id),
                    FOREIGN KEY(exercise_id) REFERENCES exercises(id)
                );""");


            // ========== ДОБАВЛЯЕМ НОВЫЕ ПОЛЯ В ТАБЛИЦУ users ==========
            // Проверяем и добавляем недостающие колонки (для совместимости со старой БД)
            addColumnIfNotExists(stmt, "users", "glasses", "BOOLEAN DEFAULT 0");
            addColumnIfNotExists(stmt, "users", "eye_disease", "TEXT");
            addColumnIfNotExists(stmt, "users", "vision_acuity", "TEXT");

            // ========== РАСШИРЯЕМ ТАБЛИЦУ exercises (новые поля) ==========
            addColumnIfNotExists(stmt, "exercises", "how_to_do", "TEXT DEFAULT ''");
            addColumnIfNotExists(stmt, "exercises", "type", "TEXT DEFAULT 'general'");
            addColumnIfNotExists(stmt, "exercises", "difficulty", "INTEGER DEFAULT 1");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM exercises");
            if (rs.next() && rs.getInt(1) < 15) {
                fillExtendedExercises(stmt);
            }

            // Заполняем вопросы для теста
            rs = stmt.executeQuery("SELECT COUNT(*) FROM questions");
            if (rs.next() && rs.getInt(1) == 0) {
                fillQuestions(stmt);
            }

            System.out.println("База данных готова к работе!\n");
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД:");
            e.printStackTrace();
        }
    }

    private static void addColumnIfNotExists(Statement stmt, String table, String columnName, String columnType) {
        try {
            stmt.execute(String.format("ALTER TABLE %s ADD COLUMN %s %s", table, columnName, columnType));
            System.out.println("   Добавлена колонка: " + table + "." + columnName);
        } catch (SQLException e) {
            // Колонка уже существует — игнорируем ошибку
        }
    }

    // Расширенное наполнение упражнениями (до 15+)
    private static void fillExtendedExercises(Statement stmt) throws SQLException {
        String[][] data = {
                // id 1-5 (старые, обновляем с новыми полями)
                {"Пальминг", "Расслабление глаз тёплыми ладонями", "60", "Бейтса",
                        "Потрите ладони друг о друга. Закройте глаза и накройте их ладонями. Полностью расслабьтесь.", "relaxation", "1"},
                {"Круговые движения", "Медленные круги глазами", "40", "Стандартная",
                        "Не поворачивая головы, медленно вращайте глазами по кругу: 10 раз в одну сторону, 10 в другую.", "movement", "1"},
                {"Смена фокуса", "Ближний → дальний объект", "30", "Аветисова",
                        "Смотрите на палец на расстоянии 15 см 5 сек, затем переведите взгляд вдаль на 5 сек.", "focus", "2"},
                {"Частое моргание", "Лёгкое моргание 1 минуту", "60", "Гигиена зрения",
                        "Быстро и легко моргайте в течение минуты, не сжимая веки сильно.", "relaxation", "1"},
                {"Движения по сторонам", "Вверх-вниз, влево-вправо", "45", "Стандартная",
                        "Медленно переводите взгляд вверх, затем вниз. Повторите 10 раз. Затем влево-вправо 10 раз.", "movement", "1"},

                // Новые упражнения 6-15
                {"Соляризация", "Мягкое привыкание глаз к свету", "30", "Бейтса",
                        "Закройте глаза и поверните лицо к солнцу (или лампе). Медленно поворачивайте голову из стороны в сторону.", "relaxation", "2"},
                {"Карандашная гимнастика", "Слежение за движущимся предметом", "35", "Аветисова",
                        "Возьмите карандаш. Медленно приближайте его к носу, следя глазами, затем отводите обратно. Повторите 10 раз.", "focus", "3"},
                {"Фиксация на буквах", "Тренировка чёткости зрения", "45", "Стандартная",
                        "Сфокусируйтесь на букве в книге, затем переводите взгляд на соседнюю, не двигая головой.", "focus", "2"},
                {"Часы", "Круговые движения глазами по циферблату", "40", "Стандартная",
                        "Представьте перед собой циферблат. Переводите взгляд на каждую цифру по часовой стрелке, затем против.", "focus", "2"},
                {"Массаж век", "Лёгкий массаж закрытых век", "30", "Гигиена зрения",
                        "Закройте глаза. Круговыми движениями мягко массируйте веки кончиками пальцев.", "massage", "1"},
                {"Рисование носом", "Расслабление глаз и шеи", "30", "Бейтса",
                        "Представьте, что кончик носа — это кисть. Рисуйте в воздухе круги, восьмёрки, буквы.", "movement", "2"},
                {"Взгляд вдаль", "Снятие напряжения после работы за ПК", "60", "Гигиена зрения",
                        "Подойдите к окну. Смотрите вдаль (на горизонт) 30 сек, затем на ближний предмет (10 сек). Повторяйте 2 минуты.", "relaxation", "1"},
                {"Медленное моргание", "Увлажнение глаз", "20", "Гигиена зрения",
                        "Медленно закройте глаза (счёт 2), задержите закрытыми на 2 секунды, медленно откройте. Повторите 10 раз.", "relaxation", "1"},
                {"Диагонали", "Движения глаз по диагонали", "30", "Стандартная",
                        "Переводите взгляд вверх-вправо, затем вниз-влево. Затем вверх-влево и вниз-вправо. Повторите по 10 раз.", "movement", "2"},
                {"Восьмёрки", "Трассировка горизонтальной восьмёрки", "40", "Стандартная",
                        "Представьте перед собой знак бесконечности (8). Медленно водите глазами по этой траектории.", "movement", "2"}
        };

        // Сначала удаляем старые записи (чтобы избежать дублей)
        stmt.execute("DELETE FROM exercises");

        for (String[] ex : data) {
            stmt.execute(String.format(
                    "INSERT INTO exercises (name, description, duration, method, how_to_do, type, difficulty) " +
                            "VALUES ('%s', '%s', %s, '%s', '%s', '%s', %s)",
                    ex[0], ex[1], ex[2], ex[3], ex[4], ex[5], ex[6]));
        }
        System.out.println("   Добавлено " + data.length + " упражнений");
    }

    // Заполнение вопросов для теста
    private static void fillQuestions(Statement stmt) throws SQLException {
        String[][] questions = {
                // Симптомы утомления (category: symptoms)
                {"Как часто вы чувствуете сухость или жжение в глазах?", "symptoms", "3", null},
                {"Бывают ли у вас головные боли после работы за компьютером?", "symptoms", "2", null},
                {"Замечаете ли вы «пелену» или размытость изображения к концу дня?", "symptoms", "3", null},
                {"Часто ли краснеют глаза к вечеру?", "symptoms", "2", null},
                {"Бывает ли двоение в глазах при взгляде вдаль?", "symptoms", "3", null},

                // Привычки работы (category: habits)
                {"Сколько часов в день вы проводите за компьютером?", "habits", "4", "1-2 часа,3-4 часа,5-6 часов,7-8 часов,более 8 часов"},
                {"Как часто вы делаете перерывы в работе за ПК?", "habits", "3", "каждый час,раз в 2-3 часа,раз в 4-5 часов,почти никогда"},
                {"Соблюдаете ли вы расстояние до монитора (50-70 см)?", "habits", "2", "всегда,чаще всего,иногда,нет"},
                {"Работаете ли вы при хорошем освещении?", "habits", "2", "всегда,чаще всего,иногда,нет"},
                {"Делаете ли вы гимнастику для глаз в течение рабочего дня?", "habits", "3", "ежедневно,пару раз в неделю,редко,никогда"},

                // Состояние здоровья (category: health)
                {"Носите ли вы очки или контактные линзы?", "health", "2", "нет,очки,линзы"},
                {"Есть ли у вас диагностированные заболевания глаз?", "health", "4", "нет,близорукость,дальнозоркость,астигматизм,другое"},
                {"Как вы оцениваете свою остроту зрения?", "health", "3", "отличная,хорошая,удовлетворительная,плохая"},
                {"Были ли у вас травмы глаз или операции?", "health", "4", "нет,да"},
                {"Страдаете ли вы от повышенного внутриглазного давления?", "health", "4", "нет,да,не знаю"}
        };

        for (String[] q : questions) {
            String options = q[3] != null ? "'" + q[3] + "'" : "NULL";
            stmt.execute(String.format(
                    "INSERT INTO questions (question_text, category, weight, options) " +
                            "VALUES ('%s', '%s', %s, %s)",
                    q[0], q[1], q[2], options));
        }
        System.out.println("   Добавлено " + questions.length + " вопросов для теста");
    }
}