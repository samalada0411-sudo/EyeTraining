package service;

import model.*;
import dao.ExerciseDAO;
import dao.ProgramDAO;
import dao.ProgramExerciseDAO;
import java.sql.SQLException;
import java.util.*;

/**
 * Генерацию персонализированной программы тренировок
 * Подбор упражнений на основе нагрузки
 * Расчёт общей длительности программы
 */

public class ProgramGenerator {

    private ExerciseDAO exerciseDAO;
    private ProgramDAO programDAO;
    private ProgramExerciseDAO programExerciseDAO;
    private TestService testService;

    public ProgramGenerator() {
        this.exerciseDAO = new ExerciseDAO();
        this.programDAO = new ProgramDAO();
        this.programExerciseDAO = new ProgramExerciseDAO();
        this.testService = new TestService();
    }

    /**
     * Сгенерировать персонализированную программу на основе ответов пользователя
     */
    public Program generatePersonalizedProgram(int userId, List<Answer> answers) throws SQLException {
        // 1. Рассчитываем нагрузку
        int totalScore = testService.calculateTotalLoad(answers);
        String loadLevel = testService.getLoadLevel(totalScore);
        String recommendedType = testService.getRecommendedExerciseType(loadLevel);

        System.out.println("   DEBUG: loadLevel=" + loadLevel + ", recommendedType=" + recommendedType);

        // 2. Подбираем упражнения
        List<Exercise> selectedExercises = selectExercisesByLoad(recommendedType, loadLevel);

        System.out.println("   DEBUG: выбрано упражнений: " + selectedExercises.size());
        for (Exercise ex : selectedExercises) {
            System.out.println("      - " + (ex != null ? ex.getName() : "NULL!"));
        }

        // 3. Создаём программу
        int totalDuration = calculateTotalDuration(selectedExercises);
        Program program = new Program(userId, "personalized", true, totalDuration);
        programDAO.save(program);

        // 4. Сохраняем упражнения в программе
        int orderNum = 1;
        for (Exercise exercise : selectedExercises) {
            if (exercise != null) {
                ProgramExercise pe = new ProgramExercise(
                        program.getId(),
                        exercise.getId(),
                        exercise.getDuration(),
                        getRepeatsByLoad(loadLevel),
                        orderNum++
                );
                programExerciseDAO.save(pe);
            }
        }

        return program;
    }

    /**
     * Сгенерировать готовую программу по названию
     */
    public Program generatePresetProgram(int userId, String presetName) throws SQLException {
        List<Exercise> selectedExercises = getPresetExercises(presetName);

        int totalDuration = calculateTotalDuration(selectedExercises);
        Program program = new Program(userId, presetName, false, totalDuration);
        programDAO.save(program);

        int orderNum = 1;
        for (Exercise exercise : selectedExercises) {
            if (exercise != null) {
                ProgramExercise pe = new ProgramExercise(
                        program.getId(),
                        exercise.getId(),
                        exercise.getDuration(),
                        1,
                        orderNum++
                );
                programExerciseDAO.save(pe);
            }
        }

        return program;
    }

    /**
     * Подбор упражнений на основе уровня нагрузки
     */
    private List<Exercise> selectExercisesByLoad(String recommendedType, String loadLevel) throws SQLException {
        List<Exercise> selected = new ArrayList<>();

        // Базовые упражнения (всегда добавляем, с проверкой на существование)
        Exercise palm = exerciseDAO.findById(1);      // Пальминг
        Exercise blink = exerciseDAO.findById(4);     // Частое моргание

        if (palm != null) selected.add(palm);
        if (blink != null) selected.add(blink);

        // Добавляем упражнения по типу
        List<Exercise> typeExercises = exerciseDAO.findByType(recommendedType);
        System.out.println("   DEBUG: найдено упражнений типа " + recommendedType + ": " + typeExercises.size());

        if (!typeExercises.isEmpty()) {
            // Берём 2-3 упражнения из подходящего типа
            int count = "high".equals(loadLevel) ? 3 : 2;
            for (int i = 0; i < count && i < typeExercises.size(); i++) {
                Exercise ex = typeExercises.get(i);
                if (ex != null && !containsExercise(selected, ex.getId())) {
                    selected.add(ex);
                }
            }
        }

        // Если не нашли упражнений по типу, добавляем альтернативные
        if (selected.size() < 3) {
            addFallbackExercises(selected);
        }

        // При высокой нагрузке добавляем массаж
        if ("high".equals(loadLevel)) {
            List<Exercise> massage = exerciseDAO.findByType("massage");
            if (!massage.isEmpty()) {
                Exercise m = massage.get(0);
                if (m != null && !containsExercise(selected, m.getId())) {
                    selected.add(m);
                }
            }
        }

        return selected;
    }

    /**
     * Добавить альтернативные упражнения (если не нашли по типу)
     */
    private void addFallbackExercises(List<Exercise> selected) throws SQLException {
        int[] fallbackIds = {3, 5, 7}; // Смена фокуса, Движения по сторонам, Карандашная гимнастика
        for (int id : fallbackIds) {
            if (selected.size() >= 4) break;
            Exercise ex = exerciseDAO.findById(id);
            if (ex != null && !containsExercise(selected, ex.getId())) {
                selected.add(ex);
            }
        }
    }

    /**
     * Проверить, есть ли уже упражнение в списке
     */
    private boolean containsExercise(List<Exercise> list, int id) {
        for (Exercise ex : list) {
            if (ex != null && ex.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить упражнения для готовой программы
     */
    private List<Exercise> getPresetExercises(String presetName) throws SQLException {
        List<Exercise> exercises = new ArrayList<>();

        switch (presetName) {
            case "Быстрая зарядка":
                addIfExists(exercises, 4);  // Частое моргание
                addIfExists(exercises, 3);  // Смена фокуса
                addIfExists(exercises, 5);  // Движения по сторонам
                break;
            case "Полная релаксация":
                addIfExists(exercises, 1);  // Пальминг
                addIfExists(exercises, 6);  // Соляризация
                addIfExists(exercises, 8);  // Массаж век
                addIfExists(exercises, 9);  // Взгляд вдаль
                break;
            case "Для работы за ПК":
                addIfExists(exercises, 3);  // Смена фокуса
                addIfExists(exercises, 5);  // Движения по сторонам
                addIfExists(exercises, 7);  // Карандашная гимнастика
                addIfExists(exercises, 10); // Диагонали
                break;
            default:
                addIfExists(exercises, 1);
                addIfExists(exercises, 3);
                addIfExists(exercises, 4);
        }

        return exercises;
    }

    /**
     * Добавить упражнение в список, если оно существует в БД
     */
    private void addIfExists(List<Exercise> list, int id) throws SQLException {
        Exercise ex = exerciseDAO.findById(id);
        if (ex != null) {
            list.add(ex);
        } else {
            System.out.println("   WARNING: Упражнение с ID=" + id + " не найдено в БД!");
        }
    }

    /**
     * Количество повторов в зависимости от нагрузки
     */
    private int getRepeatsByLoad(String loadLevel) {
        switch (loadLevel) {
            case "low": return 1;
            case "medium": return 2;
            case "high": return 3;
            default: return 1;
        }
    }

    /**
     * Рассчитать общую длительность программы
     */
    private int calculateTotalDuration(List<Exercise> exercises) {
        int total = 0;
        for (Exercise ex : exercises) {
            if (ex != null) {
                total += ex.getDuration();
            }
        }
        return total;
    }

    /**
     * Получить программу с её упражнениями (для отображения)
     */
    public ProgramWithExercises getProgramWithExercises(int programId) throws SQLException {
        Program program = programDAO.findById(programId);
        if (program == null) return null;

        List<ProgramExercise> programExercises = programExerciseDAO.findByProgramId(programId);
        List<Exercise> exercises = new ArrayList<>();

        for (ProgramExercise pe : programExercises) {
            Exercise ex = exerciseDAO.findById(pe.getExerciseId());
            if (ex != null) {
                exercises.add(ex);
            }
        }

        return new ProgramWithExercises(program, exercises, programExercises);
    }

    /**
     * Вспомогательный класс для возврата программы с упражнениями
     */
    public static class ProgramWithExercises {
        private Program program;
        private List<Exercise> exercises;
        private List<ProgramExercise> programExercises;

        public ProgramWithExercises(Program program, List<Exercise> exercises, List<ProgramExercise> programExercises) {
            this.program = program;
            this.exercises = exercises;
            this.programExercises = programExercises;
        }

        public Program getProgram() { return program; }
        public List<Exercise> getExercises() { return exercises; }
        public List<ProgramExercise> getProgramExercises() { return programExercises; }
    }
}