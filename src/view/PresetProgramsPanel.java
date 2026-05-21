package view;

import controller.MainController;
import view.components.RoundedPanel;
import view.components.StyledButton;
import javax.swing.*;
import java.awt.*;

public class PresetProgramsPanel extends JPanel {

    private MainController controller;
    private JPanel cardsContainer;

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

        JLabel descLabel = new JLabel("Выберите готовую программу, чтобы посмотреть её описание и начать тренировку");
        descLabel.setFont(Colors.NORMAL_FONT);
        descLabel.setForeground(Colors.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel);
        headerPanel.add(descLabel);

        cardsContainer = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsContainer.setOpaque(false);

        cardsContainer.add(createProgramCard(
                "Быстрая зарядка",
                "Короткая программа для быстрого снятия напряжения",
                new String[]{"Частое моргание", "Смена фокуса", "Движения по сторонам"},
                new String[]{"Быстро и легко моргайте в течение минуты",
                        "Смотрите на палец (15 см) 5 сек, затем вдаль 5 сек",
                        "Переводите взгляд вверх-вниз 10 раз, затем влево-вправо 10 раз"}
        ));

        cardsContainer.add(createProgramCard(
                "Полная релаксация",
                "Глубокая релаксация для глаз после долгого дня",
                new String[]{"Пальминг", "Соляризация", "Массаж век", "Взгляд вдаль"},
                new String[]{"Потрите ладони, закройте глаза и накройте их ладонями",
                        "Закройте глаза, поверните лицо к солнцу, медленно поворачивайте голову",
                        "Круговыми движениями массируйте закрытые веки",
                        "Смотрите вдаль 30 сек, затем на ближний предмет 10 сек"}
        ));

        cardsContainer.add(createProgramCard(
                "Для работы за ПК",
                "Комплекс для профилактики компьютерного синдрома",
                new String[]{"Смена фокуса", "Движения по сторонам", "Карандашная гимнастика", "Диагонали"},
                new String[]{"Смотрите на палец (15 см) 5 сек, затем вдаль 5 сек",
                        "Переводите взгляд вверх-вниз 10 раз, затем влево-вправо 10 раз",
                        "Приближайте карандаш к носу, следя глазами, затем отводите",
                        "Переводите взгляд вверх-вправо -> вниз-влево, затем вверх-влево -> вниз-вправо"}
        ));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(850, 450));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createProgramCard(String title, String description, String[] exerciseNames, String[] instructions) {
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

        JLabel countLabel = new JLabel("Упражнений: " + exerciseNames.length);
        countLabel.setFont(Colors.SMALL_FONT);

        int totalTime = exerciseNames.length * 30;
        JLabel durationLabel = new JLabel("Длительность: ~" + totalTime + " сек");
        durationLabel.setFont(Colors.SMALL_FONT);

        specsPanel.add(countLabel);
        specsPanel.add(durationLabel);

        StyledButton viewButton = new StyledButton("Посмотреть", Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
        viewButton.addActionListener(e -> showProgramDetailsDialog(title, exerciseNames, instructions));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(specsPanel, BorderLayout.SOUTH);
        card.add(viewButton, BorderLayout.EAST);

        return card;
    }

    private void showProgramDetailsDialog(String programName, String[] exerciseNames, String[] instructions) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Программа: " + programName);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Colors.PANEL_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(programName);
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < exerciseNames.length; i++) {
            JPanel exercisePanel = new JPanel();
            exercisePanel.setLayout(new BoxLayout(exercisePanel, BoxLayout.Y_AXIS));
            exercisePanel.setBackground(Colors.SECONDARY);
            exercisePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Colors.BORDER, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            exercisePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel((i+1) + ". " + exerciseNames[i]);
            nameLabel.setFont(Colors.TITLE_FONT);
            nameLabel.setForeground(Colors.TEXT_PRIMARY);

            JLabel instructionLabel = new JLabel("<html><body style='width: 420px'>" + instructions[i] + "</body></html>");
            instructionLabel.setFont(Colors.NORMAL_FONT);
            instructionLabel.setForeground(Colors.TEXT_SECONDARY);

            exercisePanel.add(nameLabel);
            exercisePanel.add(Box.createVerticalStrut(5));
            exercisePanel.add(instructionLabel);

            mainPanel.add(exercisePanel);
            mainPanel.add(Box.createVerticalStrut(10));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton closeButton = new StyledButton("Закрыть", Colors.TEXT_SECONDARY, Colors.TEXT_SECONDARY.darker(), Colors.TEXT_SECONDARY.darker().darker());
        closeButton.addActionListener(e -> dialog.dispose());

        JButton startButton = new StyledButton("Начать тренировку", Colors.ACCENT, Colors.ACCENT.darker(), Colors.ACCENT.darker().darker());
        startButton.addActionListener(e -> {
            dialog.dispose();
            if (controller != null) {
                controller.selectPresetProgram(programName);
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Colors.PANEL_BACKGROUND);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}