package controller;

import model.*;
import dao.*;
import service.*;
import view.Colors;
import view.MainFrame;
import view.TestPanel;
import javax.swing.*;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private MainFrame view;
    private User currentUser;
    private UserDAO userDAO;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private ProgramDAO programDAO;
    private ProgramExerciseDAO programExerciseDAO;
    private TestService testService;
    private ProgramGenerator programGenerator;

    // Храним последнюю сгенерированную программу и уровень нагрузки для текущего пользователя
    private Program lastProgram;
    private String lastLoadLevel;
    private int lastTotalLoad;

    public MainController(MainFrame view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.questionDAO = new QuestionDAO();
        this.answerDAO = new AnswerDAO();
        this.programDAO = new ProgramDAO();
        this.programExerciseDAO = new ProgramExerciseDAO();
        this.testService = new TestService();
        this.programGenerator = new ProgramGenerator();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Выбран пользователь: " + user.getName() + " (ID=" + user.getId() + ")");

        // Загружаем данные пользователя из БД
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser == null) return;

        try {
            // 1. Загружаем последние ответы пользователя
            List<Answer> answers = answerDAO.findByUserId(currentUser.getId());

            if (answers != null && !answers.isEmpty()) {
                // Есть ответы - рассчитываем нагрузку
                lastTotalLoad = testService.calculateTotalLoad(answers);
                lastLoadLevel = testService.getLoadLevel(lastTotalLoad);
                System.out.println("  Загружены ответы, нагрузка: " + lastTotalLoad + " (" + lastLoadLevel + ")");
            } else {
                lastTotalLoad = 0;
                lastLoadLevel = null;
                System.out.println("  Нет сохранённых ответов");
            }

            // 2. Загружаем последнюю программу пользователя
            lastProgram = programDAO.findLatestByUserId(currentUser.getId());

            if (lastProgram != null) {
                // Загружаем упражнения программы
                ProgramGenerator.ProgramWithExercises programWithEx =
                        programGenerator.getProgramWithExercises(lastProgram.getId());

                if (programWithEx != null && lastLoadLevel != null) {
                    // Отображаем программу
                    view.getProgramPanel().displayProgram(
                            programWithEx.getExercises(),
                            lastLoadLevel,
                            lastProgram.getTotalDuration()
                    );
                    System.out.println("  Загружена последняя программа (ID=" + lastProgram.getId() + ")");
                } else {
                    view.getProgramPanel().showEmptyState();
                }
            } else {
                view.getProgramPanel().showEmptyState();
                System.out.println("  Нет сохранённых программ");
            }

            // 3. Обновляем отображение уровня нагрузки на панели программы
            if (lastLoadLevel != null) {
                view.getProgramPanel().updateLoadLevel(lastLoadLevel, lastTotalLoad);
            } else {
                view.getProgramPanel().resetLoadLevel();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getQuestions() {
        try {
            return questionDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void submitTest(List<Integer> answerValues) {
        if (currentUser == null) {
            showMessage("Ошибка", "Пользователь не выбран", "error");
            return;
        }

        try {
            List<Question> questions = questionDAO.findAll();
            List<Answer> answers = new ArrayList<>();

            for (int i = 0; i < questions.size() && i < answerValues.size(); i++) {
                Answer answer = new Answer(currentUser.getId(), questions.get(i).getId(), answerValues.get(i));
                answers.add(answer);
            }

            // Удаляем старые ответы пользователя и сохраняем новые
            // (для простоты - удалим старые, потом сохраним новые)
            // В реальном проекте лучше обновлять

            testService.saveAllAnswers(currentUser.getId(), answers);

            lastTotalLoad = testService.calculateTotalLoad(answers);
            lastLoadLevel = testService.getLoadLevel(lastTotalLoad);

            // Генерируем и сохраняем программу
            lastProgram = programGenerator.generatePersonalizedProgram(currentUser.getId(), answers);

            // Отображаем программу
            ProgramGenerator.ProgramWithExercises programWithEx =
                    programGenerator.getProgramWithExercises(lastProgram.getId());

            if (programWithEx != null) {
                view.getProgramPanel().displayProgram(
                        programWithEx.getExercises(),
                        lastLoadLevel,
                        lastProgram.getTotalDuration()
                );
            }

            showMessage("Успех!",
                    "Тест пройден!\nУровень нагрузки: " + getLoadLevelText(lastLoadLevel) +
                            "\nСгенерирована программа тренировок", "success");

            // Переключаемся на вкладку программы
            JTabbedPane tabbedPane = (JTabbedPane) view.getContentPane().getComponent(1);
            tabbedPane.setSelectedIndex(1);

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Ошибка", "Не удалось сохранить результаты теста: " + e.getMessage(), "error");
        }
    }

    public void generateNewProgram() {
        if (currentUser == null) {
            showMessage("Ошибка", "Пользователь не выбран", "error");
            return;
        }

        try {
            List<Answer> answers = answerDAO.findByUserId(currentUser.getId());

            if (answers.isEmpty()) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Вы ещё не проходили тест.\nСгенерировать программу на основе стандартных параметров?",
                        "Нет теста",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
                generateDefaultProgram();
                return;
            }

            // Используем сохранённые ответы
            lastTotalLoad = testService.calculateTotalLoad(answers);
            lastLoadLevel = testService.getLoadLevel(lastTotalLoad);

            lastProgram = programGenerator.generatePersonalizedProgram(currentUser.getId(), answers);

            ProgramGenerator.ProgramWithExercises programWithEx =
                    programGenerator.getProgramWithExercises(lastProgram.getId());

            if (programWithEx != null) {
                view.getProgramPanel().displayProgram(
                        programWithEx.getExercises(),
                        lastLoadLevel,
                        lastProgram.getTotalDuration()
                );
            }

            showMessage("Готово", "Сгенерирована новая программа тренировок", "success");

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Ошибка", "Не удалось сгенерировать программу", "error");
        }
    }

    private void generateDefaultProgram() {
        try {
            selectPresetProgram("Быстрая зарядка");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectPresetProgram(String presetName) {
        if (currentUser == null) {
            showMessage("Ошибка", "Пользователь не выбран", "error");
            return;
        }

        try {
            lastProgram = programGenerator.generatePresetProgram(currentUser.getId(), presetName);

            // Загружаем упражнения программы для отображения
            ProgramGenerator.ProgramWithExercises programWithEx =
                    programGenerator.getProgramWithExercises(lastProgram.getId());

            if (programWithEx != null) {
                view.getProgramPanel().displayProgram(
                        programWithEx.getExercises(),
                        "medium",
                        lastProgram.getTotalDuration()
                );
                // Сохраняем уровень нагрузки как средний для готовой программы
                lastLoadLevel = "medium";
                lastTotalLoad = 50;
            }

            showMessage("Готово", "Программа \"" + presetName + "\" добавлена в раздел \"Моя программа\"", "success");

            // Переключаемся на вкладку программы
            JTabbedPane tabbedPane = (JTabbedPane) view.getContentPane().getComponent(1);
            tabbedPane.setSelectedIndex(1);

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Ошибка", "Не удалось создать программу", "error");
        }
    }

    private String getLoadLevelText(String loadLevel) {
        switch (loadLevel) {
            case "low": return "Низкий (рекомендуются расслабляющие упражнения)";
            case "medium": return "Средний (рекомендуются упражнения на фокусировку)";
            case "high": return "Высокий (рекомендуются активные движения и массаж)";
            default: return loadLevel;
        }
    }

    private void showMessage(String title, String message, String type) {
        TestPanel testPanel = view.getTestPanel();
        Color color = type.equals("success") ? Colors.ACCENT : Colors.DANGER;
        testPanel.showStatus(message, color);
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}