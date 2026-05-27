package service;

import model.*;
import dao.*;
import java.sql.SQLException;
import java.util.*;

public class TestService {

    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private TestHistoryDAO testHistoryDAO;

    public TestService() {
        this.questionDAO = new QuestionDAO();
        this.answerDAO = new AnswerDAO();
        this.testHistoryDAO = new TestHistoryDAO();
    }

    public List<Question> getAllQuestions() throws SQLException {
        return questionDAO.findAll();
    }

    public void saveAnswers(int userId, List<Integer> answerValues) throws SQLException {
        List<Question> questions = questionDAO.findAll();

        // Удаляем старые ответы пользователя
        answerDAO.deleteByUserId(userId);

        for (int i = 0; i < questions.size() && i < answerValues.size(); i++) {
            Answer answer = new Answer(userId, questions.get(i).getId(), answerValues.get(i));
            answerDAO.save(answer);
        }
    }

    public TestResult calculateTestResult(int userId, List<Integer> answerValues) throws SQLException {
        List<Question> questions = questionDAO.findAll();
        int totalScore = 0;

        for (int i = 0; i < questions.size() && i < answerValues.size(); i++) {
            Question q = questions.get(i);
            int answerValue = answerValues.get(i);
            totalScore += answerValue * q.getWeight();
        }

        String loadLevel;
        if (totalScore <= 50) loadLevel = "low";
        else if (totalScore <= 100) loadLevel = "medium";
        else loadLevel = "high";

        // Сохраняем результат в историю
        testHistoryDAO.save(userId, totalScore, loadLevel);

        return new TestResult(totalScore, loadLevel, true);
    }

    public TestResult getUserTestResult(int userId) throws SQLException {
        List<Answer> answers = answerDAO.findByUserId(userId);

        if (answers == null || answers.isEmpty()) {
            return new TestResult(0, null, false);
        }

        int totalScore = 0;
        for (Answer answer : answers) {
            Question question = questionDAO.findById(answer.getQuestionId());
            if (question != null) {
                totalScore += answer.getAnswerValue() * question.getWeight();
            }
        }

        String loadLevel;
        if (totalScore <= 50) loadLevel = "low";
        else if (totalScore <= 100) loadLevel = "medium";
        else loadLevel = "high";

        return new TestResult(totalScore, loadLevel, true);
    }

    public List<TestHistoryEntry> getTestHistory(int userId) throws SQLException {
        return testHistoryDAO.findByUserId(userId);
    }

    public static class TestResult {
        private final int totalScore;
        private final String loadLevel;
        private final boolean hasAnswers;

        public TestResult(int totalScore, String loadLevel, boolean hasAnswers) {
            this.totalScore = totalScore;
            this.loadLevel = loadLevel;
            this.hasAnswers = hasAnswers;
        }

        public int getTotalScore() { return totalScore; }
        public String getLoadLevel() { return loadLevel; }
        public boolean hasAnswers() { return hasAnswers; }
    }
}