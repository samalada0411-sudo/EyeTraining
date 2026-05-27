package service;

import model.*;
import dao.*;
import service.TestService.TestResult;
import controller.MainController;
import java.sql.SQLException;
import java.util.*;

public class ProgramGenerator {

    private ExerciseDAO exerciseDAO;
    private ProgramDAO programDAO;
    private ProgramExerciseDAO programExerciseDAO;

    public ProgramGenerator() {
        this.exerciseDAO = new ExerciseDAO();
        this.programDAO = new ProgramDAO();
        this.programExerciseDAO = new ProgramExerciseDAO();
    }

    public Program generateProgram(int userId, TestResult testResult) throws SQLException {
        String recommendedType = getRecommendedType(testResult.getLoadLevel());
        List<Exercise> selectedExercises = selectExercisesByLoad(recommendedType, testResult.getLoadLevel());

        int totalDuration = calculateTotalDuration(selectedExercises);
        Program program = new Program(userId, "personalized", true, totalDuration);
        programDAO.save(program);

        int orderNum = 1;
        for (Exercise ex : selectedExercises) {
            ProgramExercise pe = new ProgramExercise(
                    program.getId(), ex.getId(), ex.getDuration(),
                    getRepeatsByLoad(testResult.getLoadLevel()), orderNum++
            );
            programExerciseDAO.save(pe);
        }

        return program;
    }

    public Program generatePresetProgram(int userId, String presetName) throws SQLException {
        List<Exercise> selectedExercises = getPresetExercises(presetName);
        int totalDuration = calculateTotalDuration(selectedExercises);

        Program program = new Program(userId, presetName, false, totalDuration);
        programDAO.save(program);

        int orderNum = 1;
        for (Exercise ex : selectedExercises) {
            ProgramExercise pe = new ProgramExercise(program.getId(), ex.getId(), ex.getDuration(), 1, orderNum++);
            programExerciseDAO.save(pe);
        }

        return program;
    }

    public Program getLatestProgram(int userId) throws SQLException {
        return programDAO.findLatestByUserId(userId);
    }

    public List<MainController.ExerciseData> getProgramExercisesForDisplay(int programId) throws SQLException {
        List<ProgramExercise> programExercises = programExerciseDAO.findByProgramId(programId);
        List<MainController.ExerciseData> exerciseData = new ArrayList<>();

        for (ProgramExercise pe : programExercises) {
            Exercise ex = exerciseDAO.findById(pe.getExerciseId());
            if (ex != null) {
                exerciseData.add(new MainController.ExerciseData(
                        ex.getName(),
                        ex.getDescription(),
                        ex.getHowToDo(),
                        ex.getDuration(),
                        getTypeName(ex.getType()),
                        ex.getDifficulty()
                ));
            }
        }

        return exerciseData;
    }

    public List<MainController.PresetExerciseInfo> getPresetProgramDetails(String presetName) throws SQLException {
        List<MainController.PresetExerciseInfo> result = new ArrayList<>();

        int[] exerciseIds;
        String[] instructions;

        switch (presetName) {
            case "Быстрая зарядка":
                exerciseIds = new int[]{4, 3, 5};
                instructions = new String[]{
                        "Быстро и легко моргайте в течение минуты",
                        "Смотрите на палец (15 см) 5 сек, затем вдаль 5 сек",
                        "Переводите взгляд вверх-вниз 10 раз, затем влево-вправо 10 раз"
                };
                break;
            case "Полная релаксация":
                exerciseIds = new int[]{1, 6, 10, 12};
                instructions = new String[]{
                        "Потрите ладони, закройте глаза и накройте их ладонями",
                        "Закройте глаза, поверните лицо к солнцу, медленно поворачивайте голову",
                        "Круговыми движениями массируйте закрытые веки",
                        "Смотрите вдаль 30 сек, затем на ближний предмет 10 сек"
                };
                break;
            case "Для работы за ПК":
                exerciseIds = new int[]{3, 5, 7, 14};
                instructions = new String[]{
                        "Смотрите на палец (15 см) 5 сек, затем вдаль 5 сек",
                        "Переводите взгляд вверх-вниз 10 раз, затем влево-вправо 10 раз",
                        "Приближайте карандаш к носу, следя глазами, затем отводите",
                        "Переводите взгляд вверх-вправо -> вниз-влево, затем вверх-влево -> вниз-вправо"
                };
                break;
            default:
                return result;
        }

        for (int i = 0; i < exerciseIds.length; i++) {
            Exercise ex = exerciseDAO.findById(exerciseIds[i]);
            if (ex != null) {
                result.add(new MainController.PresetExerciseInfo(ex.getName(), instructions[i]));
            }
        }

        return result;
    }

    private String getRecommendedType(String loadLevel) {
        switch (loadLevel) {
            case "low": return "relaxation";
            case "medium": return "focus";
            case "high": return "movement";
            default: return "relaxation";
        }
    }

    private List<Exercise> selectExercisesByLoad(String type, String loadLevel) throws SQLException {
        List<Exercise> selected = new ArrayList<>();

        addIfExists(selected, 1);
        addIfExists(selected, 4);

        List<Exercise> typeExercises = exerciseDAO.findByType(type);
        int count = "high".equals(loadLevel) ? 3 : 2;
        for (int i = 0; i < count && i < typeExercises.size(); i++) {
            if (!containsId(selected, typeExercises.get(i).getId())) {
                selected.add(typeExercises.get(i));
            }
        }

        if ("high".equals(loadLevel)) {
            addIfExists(selected, 10);
        }

        return selected;
    }

    private List<Exercise> getPresetExercises(String presetName) throws SQLException {
        List<Exercise> exercises = new ArrayList<>();

        switch (presetName) {
            case "Быстрая зарядка":
                addIfExists(exercises, 4);
                addIfExists(exercises, 3);
                addIfExists(exercises, 5);
                break;
            case "Полная релаксация":
                addIfExists(exercises, 1);
                addIfExists(exercises, 6);
                addIfExists(exercises, 10);
                addIfExists(exercises, 12);
                break;
            case "Для работы за ПК":
                addIfExists(exercises, 3);
                addIfExists(exercises, 5);
                addIfExists(exercises, 7);
                addIfExists(exercises, 14);
                break;
        }

        return exercises;
    }

    private void addIfExists(List<Exercise> list, int id) throws SQLException {
        Exercise ex = exerciseDAO.findById(id);
        if (ex != null) list.add(ex);
    }

    private boolean containsId(List<Exercise> list, int id) {
        for (Exercise ex : list) {
            if (ex.getId() == id) return true;
        }
        return false;
    }

    private int getRepeatsByLoad(String loadLevel) {
        switch (loadLevel) {
            case "low": return 1;
            case "medium": return 2;
            case "high": return 3;
            default: return 1;
        }
    }

    private int calculateTotalDuration(List<Exercise> exercises) {
        int total = 0;
        for (Exercise ex : exercises) {
            total += ex.getDuration();
        }
        return total;
    }

    private String getTypeName(String type) {
        switch (type) {
            case "relaxation": return "Расслабляющее";
            case "focus": return "Фокусировка";
            case "movement": return "Движение";
            case "massage": return "Массаж";
            default: return type;
        }
    }
}