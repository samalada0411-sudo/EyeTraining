package service;

import model.Answer;
import model.Question;
import dao.QuestionDAO;
import dao.AnswerDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * Расчёт нагрузки на зрение на основе ответов пользователя
 * Определение уровня утомления (лёгкая, средняя, высокая нагрузка)
 * Подсчёт общего балла по категориям
 */

public class TestService {

    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;

    public TestService() {
        this.questionDAO = new QuestionDAO();
        this.answerDAO = new AnswerDAO();
    }

    /**
     * Рассчитать общий балл нагрузки по ответам пользователя
     * @param answers список ответов пользователя
     * @return суммарный балл нагрузки (чем выше, тем больше нагрузка)
     */
    public int calculateTotalLoad(List<Answer> answers) throws SQLException {
        int totalScore = 0;

        for (Answer answer : answers) {
            Question question = questionDAO.findById(answer.getQuestionId());
            if (question != null) {
                // Умножаем значение ответа на вес вопроса
                totalScore += answer.getAnswerValue() * question.getWeight();
            }
        }
        return totalScore;
    }

    /**
     * Рассчитать нагрузку по отдельной категории
     * @param answers список ответов пользователя
     * @param category категория (symptoms, habits, health)
     * @return балл по категории
     */
    public int calculateCategoryLoad(List<Answer> answers, String category) throws SQLException {
        int categoryScore = 0;

        for (Answer answer : answers) {
            Question question = questionDAO.findById(answer.getQuestionId());
            if (question != null && question.getCategory().equals(category)) {
                categoryScore += answer.getAnswerValue() * question.getWeight();
            }
        }
        return categoryScore;
    }

    /**
     * Определить уровень нагрузки
     * @param totalScore общий балл
     * @return уровень нагрузки: low, medium, high
     */
    public String getLoadLevel(int totalScore) {
        if (totalScore <= 50) {
            return "low";      // низкая нагрузка
        } else if (totalScore <= 100) {
            return "medium";   // средняя нагрузка
        } else {
            return "high";     // высокая нагрузка
        }
    }

    /**
     * Получить рекомендацию по типу упражнений на основе уровня нагрузки
     * @param loadLevel уровень нагрузки
     * @return рекомендуемый тип упражнений
     */
    public String getRecommendedExerciseType(String loadLevel) {
        switch (loadLevel) {
            case "low":
                return "relaxation";  // в основном расслабляющие
            case "medium":
                return "focus";       // фокусировка + расслабление
            case "high":
                return "movement";    // активные движения + массаж
            default:
                return "relaxation";
        }
    }

    /**
     * Проверить, нужно ли срочное вмешательство
     * @param answers список ответов
     * @return true если есть тревожные симптомы
     */
    public boolean needsUrgentAttention(List<Answer> answers) throws SQLException {
        for (Answer answer : answers) {
            Question question = questionDAO.findById(answer.getQuestionId());
            // Если вопрос о заболеваниях глаз и ответ высокий
            if (question != null &&
                    "health".equals(question.getCategory()) &&
                    answer.getAnswerValue() >= 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Сохранить все ответы пользователя в БД
     */
    public void saveAllAnswers(int userId, List<Answer> answers) throws SQLException {
        for (Answer answer : answers) {
            answer.setUserId(userId);
            answerDAO.save(answer);
        }
    }

    /**
     * Проверить, проходил ли пользователь тест сегодня
     */
    public boolean hasTestToday(int userId) throws SQLException {
        return answerDAO.hasTestToday(userId);
    }

    /**
     * Получить ответы пользователя
     */
    public List<Answer> getUserAnswers(int userId) throws SQLException {
        return answerDAO.findByUserId(userId);
    }
}