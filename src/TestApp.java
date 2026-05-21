import model.*;
import dao.*;
import service.*;
import java.sql.SQLException;
import java.util.*;

public class TestApp {
    public static void main(String[] args) {
        // Инициализируем БД
        model.DatabaseManager.initializeDatabase();

        try {
            // ========== ТЕСТ 1: Работа с пользователями ==========
            System.out.println("=== ТЕСТ 1: Пользователи ===");
            UserDAO userDAO = new UserDAO();

            // Создаём нового пользователя
            User newUser = new User("Тестовый Пользователь", 30, 10);
            newUser.setGlasses(false);
            newUser.setVisionAcuity("хорошая");
            userDAO.save(newUser);
            System.out.println("Создан пользователь: " + newUser.getName() + " (ID=" + newUser.getId() + ")");

            // Находим пользователя по ID
            User found = userDAO.findById(newUser.getId());
            System.out.println("Найден пользователь: " + found.getName() + ", " + found.getAge() + " лет");

            // Все пользователи
            List<User> allUsers = userDAO.findAll();
            System.out.println("Всего пользователей в БД: " + allUsers.size());


            // ========== ТЕСТ 2: Упражнения ==========
            System.out.println("\n=== ТЕСТ 2: Упражнения ===");
            ExerciseDAO exerciseDAO = new ExerciseDAO();
            List<Exercise> exercises = exerciseDAO.findAll();
            System.out.println("Всего упражнений: " + exercises.size());
            for (Exercise ex : exercises.subList(0, Math.min(5, exercises.size()))) {
                System.out.println("  - " + ex.getName() + " (" + ex.getDuration() + " сек) [" + ex.getType() + "]");
            }


            // ========== ТЕСТ 3: Вопросы ==========
            System.out.println("\n=== ТЕСТ 3: Вопросы ===");
            QuestionDAO questionDAO = new QuestionDAO();
            List<Question> questions = questionDAO.findAll();
            System.out.println("Всего вопросов: " + questions.size());
            for (Question q : questions.subList(0, Math.min(3, questions.size()))) {
                System.out.println("  - " + q.getQuestionText() + " (категория: " + q.getCategory() + ")");
            }


            // ========== ТЕСТ 4: Прохождение теста ==========
            System.out.println("\n=== ТЕСТ 4: Тестирование ===");
            TestService testService = new TestService();

            // Создаём ответы (симулируем прохождение теста)
            List<Answer> answers = new ArrayList<>();
            for (Question q : questions) {
                int answerValue = 0;
                // Имитируем ответы: для симптомов и привычек ставим высокие значения
                switch (q.getCategory()) {
                    case "symptoms":
                        answerValue = 4;  // частые симптомы
                        break;
                    case "habits":
                        answerValue = 4;  // плохие привычки
                        break;
                    case "health":
                        answerValue = 2;  // здоровье нормальное
                        break;
                    default:
                        answerValue = 3;
                }
                answers.add(new Answer(newUser.getId(), q.getId(), answerValue));
            }

            // Сохраняем ответы
            testService.saveAllAnswers(newUser.getId(), answers);
            System.out.println("Сохранено ответов: " + answers.size());

            // Рассчитываем нагрузку
            int totalLoad = testService.calculateTotalLoad(answers);
            String loadLevel = testService.getLoadLevel(totalLoad);
            System.out.println("Общая нагрузка: " + totalLoad + " баллов");
            System.out.println("Уровень нагрузки: " + loadLevel);
            System.out.println("Рекомендуемый тип упражнений: " + testService.getRecommendedExerciseType(loadLevel));


            // ========== ТЕСТ 5: Генерация программы ==========
            System.out.println("\n=== ТЕСТ 5: Генерация программы ===");
            ProgramGenerator generator = new ProgramGenerator();

            // Персонализированная программа
            Program personalized = generator.generatePersonalizedProgram(newUser.getId(), answers);
            System.out.println("Создана персонализированная программа (ID=" + personalized.getId() + ")");
            System.out.println("  Тип: " + personalized.getProgramType());
            System.out.println("  Длительность: " + personalized.getTotalDuration() + " сек");

            // Готовая программа
            Program preset = generator.generatePresetProgram(newUser.getId(), "Быстрая зарядка");
            System.out.println("Создана готовая программа (ID=" + preset.getId() + ")");
            System.out.println("  Тип: " + preset.getProgramType());
            System.out.println("  Длительность: " + preset.getTotalDuration() + " сек");


            // ========== ТЕСТ 6: Просмотр программы с упражнениями ==========
            System.out.println("\n=== ТЕСТ 6: Детали программы ===");
            ProgramGenerator.ProgramWithExercises details = generator.getProgramWithExercises(personalized.getId());
            if (details != null) {
                System.out.println("Программа: " + details.getProgram().getProgramType());
                System.out.println("Упражнения:");
                for (Exercise ex : details.getExercises()) {
                    System.out.println("  - " + ex.getName());
                    System.out.println("    Описание: " + ex.getDescription());
                    System.out.println("    Как делать: " + ex.getHowToDo());
                    System.out.println("    Длительность: " + ex.getDuration() + " сек");
                }
            }

            System.out.println("\n✅ ВСЕ ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!");

        } catch (SQLException e) {
            System.err.println("Ошибка при тестировании:");
            e.printStackTrace();
        }
    }
}