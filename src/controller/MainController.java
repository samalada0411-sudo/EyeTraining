package controller;

import model.TestHistoryEntry;
import model.User;
import model.Question;
import model.Program;
import service.TestService;
import service.ProgramGenerator;
import service.UserService;
import java.sql.SQLException;
import java.util.*;

public class MainController {

    private User currentUser;
    private TestService testService;
    private ProgramGenerator programGenerator;
    private UserService userService;

    public static class QuestionData {
        public final int id;
        public final String text;
        public final String category;
        public final String[] options;

        public QuestionData(int id, String text, String category, String[] options) {
            this.id = id;
            this.text = text;
            this.category = category;
            this.options = options;
        }
    }

    public static class ExerciseData {
        public final String name;
        public final String description;
        public final String howToDo;
        public final int duration;
        public final String type;
        public final int difficulty;

        public ExerciseData(String name, String description, String howToDo, int duration, String type, int difficulty) {
            this.name = name;
            this.description = description;
            this.howToDo = howToDo;
            this.duration = duration;
            this.type = type;
            this.difficulty = difficulty;
        }
    }

    public static class PresetExerciseInfo {
        public final String name;
        public final String instruction;

        public PresetExerciseInfo(String name, String instruction) {
            this.name = name;
            this.instruction = instruction;
        }
    }

    public interface ViewListener {
        void onQuestionsLoaded(List<QuestionData> questions);
        void onProgramDisplayed(List<ExerciseData> exercises, String loadLevel, int totalDuration, int totalScore);
        void onShowEmptyProgram();
        void onShowMessage(String title, String message, boolean isError);
        void onShowPresetProgramDetails(String programName, List<PresetExerciseInfo> exercises);
        void onShowTestHistory(List<TestHistoryEntry> history);
    }

    private ViewListener viewListener;

    public MainController() {
        this.testService = new TestService();
        this.programGenerator = new ProgramGenerator();
        this.userService = new UserService();
    }

    public void setViewListener(ViewListener listener) {
        this.viewListener = listener;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadUserData();
    }

    public List<User> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addUser(String name, int age) {
        try {
            userService.addUser(name, age);
            if (viewListener != null) {
                viewListener.onShowMessage("Успех", "Пользователь добавлен", false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось добавить пользователя", true);
            }
        }
    }

    private void loadUserData() {
        if (currentUser == null) return;

        try {
            TestService.TestResult result = testService.getUserTestResult(currentUser.getId());

            if (result != null && result.hasAnswers()) {
                Program program = programGenerator.getLatestProgram(currentUser.getId());

                if (program != null) {
                    List<ExerciseData> exercises = programGenerator.getProgramExercisesForDisplay(program.getId());
                    if (viewListener != null && exercises != null && !exercises.isEmpty()) {
                        viewListener.onProgramDisplayed(
                                exercises,
                                result.getLoadLevel(),
                                program.getTotalDuration(),
                                result.getTotalScore()
                        );
                    } else {
                        viewListener.onShowEmptyProgram();
                    }
                } else {
                    Program newProgram = programGenerator.generateProgram(currentUser.getId(), result);
                    List<ExerciseData> exercises = programGenerator.getProgramExercisesForDisplay(newProgram.getId());
                    if (viewListener != null && exercises != null) {
                        viewListener.onProgramDisplayed(
                                exercises,
                                result.getLoadLevel(),
                                newProgram.getTotalDuration(),
                                result.getTotalScore()
                        );
                    }
                }
            } else {
                if (viewListener != null) {
                    viewListener.onShowEmptyProgram();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowEmptyProgram();
            }
        }
    }

    public void loadQuestions() {
        try {
            List<Question> questions = testService.getAllQuestions();
            List<QuestionData> questionDataList = new ArrayList<>();
            for (Question q : questions) {
                questionDataList.add(new QuestionData(
                        q.getId(),
                        q.getQuestionText(),
                        q.getCategory(),
                        q.getOptionsArray()
                ));
            }
            if (viewListener != null) {
                viewListener.onQuestionsLoaded(questionDataList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitTest(List<Integer> answerValues) {
        if (currentUser == null) {
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Пользователь не выбран", true);
            }
            return;
        }

        try {
            testService.saveAnswers(currentUser.getId(), answerValues);
            TestService.TestResult result = testService.calculateTestResult(currentUser.getId(), answerValues);

            Program program = programGenerator.generateProgram(currentUser.getId(), result);
            List<ExerciseData> exercises = programGenerator.getProgramExercisesForDisplay(program.getId());

            if (viewListener != null && exercises != null) {
                viewListener.onProgramDisplayed(
                        exercises,
                        result.getLoadLevel(),
                        program.getTotalDuration(),
                        result.getTotalScore()
                );
                viewListener.onShowMessage("Успех", "Тест пройден! Программа сгенерирована", false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось сохранить результаты", true);
            }
        }
    }

    public void generateNewProgram() {
        if (currentUser == null) {
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Пользователь не выбран", true);
            }
            return;
        }

        try {
            TestService.TestResult result = testService.getUserTestResult(currentUser.getId());

            if (result == null || !result.hasAnswers()) {
                if (viewListener != null) {
                    viewListener.onShowMessage("Нет теста", "Сначала пройдите тест-опросник", true);
                }
                return;
            }

            Program program = programGenerator.generateProgram(currentUser.getId(), result);
            List<ExerciseData> exercises = programGenerator.getProgramExercisesForDisplay(program.getId());

            if (viewListener != null && exercises != null) {
                viewListener.onProgramDisplayed(
                        exercises,
                        result.getLoadLevel(),
                        program.getTotalDuration(),
                        result.getTotalScore()
                );
                viewListener.onShowMessage("Готово", "Новая программа сгенерирована", false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось сгенерировать программу", true);
            }
        }
    }

    public void selectPresetProgram(String programName) {
        if (currentUser == null) {
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Пользователь не выбран", true);
            }
            return;
        }

        try {
            Program program = programGenerator.generatePresetProgram(currentUser.getId(), programName);
            List<ExerciseData> exercises = programGenerator.getProgramExercisesForDisplay(program.getId());

            if (viewListener != null && exercises != null) {
                viewListener.onProgramDisplayed(
                        exercises,
                        "medium",
                        program.getTotalDuration(),
                        50
                );
                viewListener.onShowMessage("Готово", "Программа \"" + programName + "\" добавлена", false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось создать программу", true);
            }
        }
    }

    public void showPresetProgramDetails(String programName) {
        try {
            List<PresetExerciseInfo> exercises = programGenerator.getPresetProgramDetails(programName);
            if (viewListener != null) {
                viewListener.onShowPresetProgramDetails(programName, exercises);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось загрузить программу", true);
            }
        }
    }

    public void showTestHistory() {
        if (currentUser == null) {
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Пользователь не выбран", true);
            }
            return;
        }

        try {
            List<TestHistoryEntry> history = testService.getTestHistory(currentUser.getId());
            if (viewListener != null) {
                viewListener.onShowTestHistory(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (viewListener != null) {
                viewListener.onShowMessage("Ошибка", "Не удалось загрузить историю", true);
            }
        }
    }
}