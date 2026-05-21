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
     * Сохранить все ответы пользователя в БД
     */
    public void saveAllAnswers(int userId, List<Answer> answers) throws SQLException {
        for (Answer answer : answers) {
            answer.setUserId(userId);
            answerDAO.save(answer);
        }
    }

}