package view;

import controller.MainController;
import view.components.RoundedPanel;
import view.components.StyledButton;
import javax.swing.*;
import java.awt.*;
import java.util.List;

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

        JLabel descLabel = new JLabel("Выберите готовую программу, чтобы посмотреть её описание");
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
                controller.showPresetProgramDetails(title);
            }
        });

        buttonPanel.add(viewButton);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(specsPanel, BorderLayout.SOUTH);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void showProgramDetails(String programName, List<MainController.PresetExerciseInfo> exercises) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Программа: " + programName);
        dialog.setSize(550, 500);
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

        int order = 1;
        for (MainController.PresetExerciseInfo ex : exercises) {
            JPanel exercisePanel = new JPanel();
            exercisePanel.setLayout(new BoxLayout(exercisePanel, BoxLayout.Y_AXIS));
            exercisePanel.setBackground(Colors.SECONDARY);
            exercisePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Colors.BORDER, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            exercisePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(order + ". " + ex.name);
            nameLabel.setFont(Colors.TITLE_FONT);
            nameLabel.setForeground(Colors.TEXT_PRIMARY);

            JLabel instructionLabel = new JLabel("<html><body style='width: 480px'>" + ex.instruction + "</body></html>");
            instructionLabel.setFont(Colors.NORMAL_FONT);
            instructionLabel.setForeground(Colors.TEXT_SECONDARY);

            exercisePanel.add(nameLabel);
            exercisePanel.add(Box.createVerticalStrut(5));
            exercisePanel.add(instructionLabel);

            mainPanel.add(exercisePanel);
            mainPanel.add(Box.createVerticalStrut(10));
            order++;
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
}