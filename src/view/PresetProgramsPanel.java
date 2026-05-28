package view;

import controller.MainController;
import service.WorkoutPlayer;
import view.components.RoundedPanel;
import view.components.StyledButton;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PresetProgramsPanel extends JPanel {

    private MainController controller;
    private JPanel cardsContainer;
    private WorkoutPlayer workoutPlayer;
    private JDialog currentDialog;
    private Timer workoutTimer;
    private JLabel timerLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton closeButton;
    private JPanel exercisesPanel;
    private JPanel buttonPanel;

    public PresetProgramsPanel() {
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("Готовые программы тренировок");
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel descLabel = new JLabel("Выберите готовую программу, чтобы посмотреть её описание и выполнить тренировку");
        descLabel.setFont(Colors.NORMAL_FONT);
        descLabel.setForeground(Colors.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel);
        headerPanel.add(descLabel);

        cardsContainer = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsContainer.setOpaque(false);

        cardsContainer.add(createProgramCard("Быстрая зарядка", "Короткая программа для быстрого снятия напряжения", 3, 90));
        cardsContainer.add(createProgramCard("Полная релаксация", "Глубокая релаксация для глаз после долгого дня", 4, 120));
        cardsContainer.add(createProgramCard("Для работы за ПК", "Комплекс для профилактики компьютерного синдрома", 4, 120));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(850, 400));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createProgramCard(String title, String description, int exerciseCount, int durationSec) {
        RoundedPanel card = new RoundedPanel(15, Colors.PANEL_BACKGROUND, Colors.BORDER, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Colors.TITLE_FONT);
        titleLabel.setForeground(Colors.PRIMARY);

        JLabel descLabel = new JLabel("<html><body style='width: 200px'>" + description + "</body></html>");
        descLabel.setFont(Colors.NORMAL_FONT);
        descLabel.setForeground(Colors.TEXT_SECONDARY);

        JPanel specsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        specsPanel.setOpaque(false);
        specsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel countLabel = new JLabel("Упражнений: " + exerciseCount);
        countLabel.setFont(Colors.SMALL_FONT);

        JLabel durationLabel = new JLabel("Длительность: ~" + durationSec + " сек");
        durationLabel.setFont(Colors.SMALL_FONT);

        specsPanel.add(countLabel);
        specsPanel.add(durationLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        StyledButton viewButton = new StyledButton("Посмотреть программу", Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
        viewButton.addActionListener(e -> {
            if (controller != null) {
                showProgramWithWorkout(title);
            }
        });

        buttonPanel.add(viewButton);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(specsPanel, BorderLayout.SOUTH);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void showProgramWithWorkout(String programName) {
        // Создаём диалоговое окно
        currentDialog = new JDialog();
        currentDialog.setTitle("Программа: " + programName);
        currentDialog.setSize(600, 550);
        currentDialog.setLocationRelativeTo(this);
        currentDialog.setModal(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Colors.PANEL_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Заголовок
        JLabel titleLabel = new JLabel(programName);
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель с упражнениями
        exercisesPanel = new JPanel();
        exercisesPanel.setLayout(new BoxLayout(exercisesPanel, BoxLayout.Y_AXIS));
        exercisesPanel.setBackground(Colors.PANEL_BACKGROUND);

        // Получаем упражнения для программы
        List<MainController.PresetExerciseInfo> exercises = controller.getPresetProgramDetails(programName);

        for (int i = 0; i < exercises.size(); i++) {
            MainController.PresetExerciseInfo ex = exercises.get(i);
            JPanel exercisePanel = createExerciseDetailPanel(i + 1, ex.name, ex.instruction);
            exercisesPanel.add(exercisePanel);
            exercisesPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(exercisesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Colors.PANEL_BACKGROUND);

        // Нижняя панель с таймером и кнопками
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        timerLabel = new JLabel("Готов к тренировке");
        timerLabel.setFont(Colors.NORMAL_FONT);
        timerLabel.setForeground(Colors.PRIMARY);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        startButton = new StyledButton("Начать тренировку", Colors.ACCENT, Colors.ACCENT.darker(), Colors.ACCENT.darker().darker());
        startButton.setPreferredSize(new Dimension(160, 40));
        startButton.addActionListener(e -> startWorkoutInDialog(programName));

        stopButton = new StyledButton("Остановить", Colors.WARNING, Colors.WARNING.darker(), Colors.WARNING.darker().darker());
        stopButton.setPreferredSize(new Dimension(140, 40));
        stopButton.addActionListener(e -> stopWorkoutInDialog());
        stopButton.setEnabled(false);

        closeButton = new StyledButton("Закрыть", Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
        closeButton.addActionListener(e -> {
            stopWorkoutInDialog();
            currentDialog.dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(closeButton);

        bottomPanel.add(timerLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        currentDialog.add(mainPanel);
        currentDialog.setVisible(true);
    }

    private void startWorkoutInDialog(String programName) {
        List<MainController.PresetExerciseInfo> exercises = controller.getPresetProgramDetails(programName);

        if (exercises == null || exercises.isEmpty()) return;

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        // Конвертируем в формат для WorkoutPlayer
        List<MainController.ExerciseData> workoutExercises = new java.util.ArrayList<>();
        for (MainController.PresetExerciseInfo ex : exercises) {
            workoutExercises.add(new MainController.ExerciseData(
                    ex.name,
                    "",
                    ex.instruction,
                    30,
                    "preset",
                    1
            ));
        }

        workoutPlayer = new WorkoutPlayer();

        workoutPlayer.startWorkout(workoutExercises, new WorkoutPlayer.WorkoutListener() {
            @Override
            public void onExerciseStart(String name, int order, int total) {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Упражнение " + order + "/" + total + ": " + name);
                });
            }

            @Override
            public void onExerciseProgress(int secondsLeft, int totalSeconds) {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Осталось: " + secondsLeft + " сек");
                });
            }

            @Override
            public void onExerciseComplete(String name) {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Выполнено: " + name);
                });
            }

            @Override
            public void onWorkoutComplete() {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Тренировка завершена! Отлично!");
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    if (workoutPlayer != null) {
                        workoutPlayer.close();
                    }
                });
            }
        });
    }

    private void stopWorkoutInDialog() {
        if (workoutPlayer != null) {
            workoutPlayer.stopWorkout();
            workoutPlayer.close();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            timerLabel.setText("Тренировка остановлена");
        }
    }

    private JPanel createExerciseDetailPanel(int order, String name, String instruction) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colors.SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(order + ". " + name);
        nameLabel.setFont(Colors.TITLE_FONT);
        nameLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel instructionLabel = new JLabel("<html><body style='width: 520px'>" + instruction + "</body></html>");
        instructionLabel.setFont(Colors.NORMAL_FONT);
        instructionLabel.setForeground(Colors.TEXT_SECONDARY);

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(instructionLabel);

        return panel;
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}