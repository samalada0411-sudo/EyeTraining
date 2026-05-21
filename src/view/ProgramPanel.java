package view;

import controller.MainController;
import view.components.RoundedPanel;
import view.components.StyledButton;
import model.Exercise;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProgramPanel extends JPanel {

    private MainController controller;
    private JButton generateButton;
    private JPanel programContentPanel;
    private JLabel loadLevelLabel;
    private JLabel totalDurationLabel;
    private JLabel scoreLabel;

    public ProgramPanel() {
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Моя персональная программа тренировок");
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.setOpaque(false);

        loadLevelLabel = new JLabel("Уровень нагрузки: не определён");
        loadLevelLabel.setFont(Colors.NORMAL_FONT);
        loadLevelLabel.setForeground(Colors.TEXT_SECONDARY);

        totalDurationLabel = new JLabel("Общая длительность: 0 сек");
        totalDurationLabel.setFont(Colors.NORMAL_FONT);
        totalDurationLabel.setForeground(Colors.TEXT_SECONDARY);

        scoreLabel = new JLabel(" ");
        scoreLabel.setFont(Colors.SMALL_FONT);
        scoreLabel.setForeground(Colors.TEXT_SECONDARY);

        infoPanel.add(loadLevelLabel);
        infoPanel.add(totalDurationLabel);
        infoPanel.add(scoreLabel);

        topPanel.add(titleLabel);
        topPanel.add(infoPanel);

        programContentPanel = new JPanel();
        programContentPanel.setLayout(new BoxLayout(programContentPanel, BoxLayout.Y_AXIS));
        programContentPanel.setBackground(Colors.PANEL_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(programContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Colors.PANEL_BACKGROUND);

        showEmptyState();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        generateButton = new StyledButton("Сгенерировать новую программу", Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
        generateButton.setPreferredSize(new Dimension(250, 45));
        generateButton.addActionListener(e -> {
            if (controller != null) {
                controller.generateNewProgram();
            }
        });

        bottomPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void showEmptyState() {
        programContentPanel.removeAll();

        JPanel emptyPanel = new RoundedPanel(15, Colors.SECONDARY);
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel textLabel = new JLabel("Пройдите тест-опросник, чтобы получить персональную программу");
        textLabel.setFont(Colors.NORMAL_FONT);
        textLabel.setForeground(Colors.TEXT_SECONDARY);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hintLabel = new JLabel("Или выберите готовую программу на вкладке \"Готовые программы\"");
        hintLabel.setFont(Colors.SMALL_FONT);
        hintLabel.setForeground(Colors.TEXT_SECONDARY);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyPanel.add(textLabel);
        emptyPanel.add(Box.createVerticalStrut(5));
        emptyPanel.add(hintLabel);

        programContentPanel.add(emptyPanel);

        loadLevelLabel.setText("Уровень нагрузки: не определён");
        loadLevelLabel.setForeground(Colors.TEXT_SECONDARY);
        totalDurationLabel.setText("Общая длительность: 0 сек");
        scoreLabel.setText(" ");

        programContentPanel.revalidate();
        programContentPanel.repaint();
    }

    public void resetLoadLevel() {
        loadLevelLabel.setText("Уровень нагрузки: не определён");
        loadLevelLabel.setForeground(Colors.TEXT_SECONDARY);
        totalDurationLabel.setText("Общая длительность: 0 сек");
        scoreLabel.setText(" ");
    }

    public void updateLoadLevel(String loadLevel, int totalScore) {
        String text;
        Color color;
        switch (loadLevel) {
            case "low":
                text = "Уровень нагрузки: НИЗКИЙ";
                color = Colors.STATUS_LOW;
                break;
            case "medium":
                text = "Уровень нагрузки: СРЕДНИЙ";
                color = Colors.STATUS_MEDIUM;
                break;
            default:
                text = "Уровень нагрузки: ВЫСОКИЙ";
                color = Colors.STATUS_HIGH;
        }
        loadLevelLabel.setText(text);
        loadLevelLabel.setForeground(color);
        scoreLabel.setText("Общий балл: " + totalScore);
    }

    public void displayProgram(List<Exercise> exercises, String loadLevel, int totalDuration) {
        programContentPanel.removeAll();

        updateLoadLevel(loadLevel, 0);
        totalDurationLabel.setText("Общая длительность: " + totalDuration + " сек (" + (totalDuration / 60) + " мин)");

        JLabel exercisesTitle = new JLabel("Упражнения:");
        exercisesTitle.setFont(Colors.TITLE_FONT);
        exercisesTitle.setForeground(Colors.TEXT_PRIMARY);
        exercisesTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        programContentPanel.add(exercisesTitle);

        int order = 1;
        for (Exercise ex : exercises) {
            programContentPanel.add(createExerciseCard(ex, order++));
            programContentPanel.add(Box.createVerticalStrut(10));
        }

        programContentPanel.revalidate();
        programContentPanel.repaint();
    }

    private JPanel createExerciseCard(Exercise ex, int order) {
        RoundedPanel card = new RoundedPanel(10, Colors.PANEL_BACKGROUND, Colors.BORDER, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JLabel numberLabel = new JLabel(String.valueOf(order));
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        numberLabel.setForeground(Colors.PRIMARY);
        numberLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(ex.getName());
        nameLabel.setFont(Colors.TITLE_FONT);
        nameLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel typeLabel = new JLabel(getTypeName(ex.getType()) + " | " + ex.getDuration() + " секунд");
        typeLabel.setFont(Colors.SMALL_FONT);
        typeLabel.setForeground(Colors.TEXT_SECONDARY);

        titlePanel.add(nameLabel);
        titlePanel.add(typeLabel);

        leftPanel.add(numberLabel, BorderLayout.WEST);
        leftPanel.add(titlePanel, BorderLayout.CENTER);

        JLabel difficultyLabel = new JLabel("Сложность: " + getDifficultyStars(ex.getDifficulty()));
        difficultyLabel.setFont(Colors.SMALL_FONT);
        difficultyLabel.setForeground(getDifficultyColor(ex.getDifficulty()));

        card.add(leftPanel, BorderLayout.WEST);
        card.add(difficultyLabel, BorderLayout.EAST);

        JPanel descriptionPanel = new JPanel(new GridLayout(2, 1));
        descriptionPanel.setOpaque(false);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0));

        JLabel descLabel = new JLabel(ex.getDescription());
        descLabel.setFont(Colors.SMALL_FONT);
        descLabel.setForeground(Colors.TEXT_SECONDARY);

        JLabel howLabel = new JLabel(ex.getHowToDo());
        howLabel.setFont(Colors.SMALL_FONT);
        howLabel.setForeground(Colors.TEXT_SECONDARY);

        descriptionPanel.add(descLabel);
        descriptionPanel.add(howLabel);

        card.add(descriptionPanel, BorderLayout.SOUTH);

        return card;
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

    private String getDifficultyStars(int difficulty) {
        if (difficulty == 1) return "*";
        if (difficulty == 2) return "**";
        if (difficulty == 3) return "***";
        return String.valueOf(difficulty);
    }

    private Color getDifficultyColor(int difficulty) {
        switch (difficulty) {
            case 1: return Colors.STATUS_LOW;
            case 2: return Colors.WARNING;
            case 3: return Colors.STATUS_HIGH;
            default: return Colors.TEXT_SECONDARY;
        }
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}