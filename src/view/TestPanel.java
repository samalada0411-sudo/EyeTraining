package view;

import controller.MainController;
import view.components.RoundedPanel;
import view.components.StyledButton;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestPanel extends JPanel {

    private MainController controller;
    private List<QuestionPanel> questionPanels;
    private JButton submitButton;
    private JButton historyButton;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    private JPanel questionsContainer;

    public TestPanel() {
        questionPanels = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("Тест-опросник для оценки нагрузки на зрение");
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel descLabel = new JLabel("Ответьте на вопросы, чтобы получить персонализированную программу тренировок");
        descLabel.setFont(Colors.NORMAL_FONT);
        descLabel.setForeground(Colors.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(descLabel);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        questionsContainer = new JPanel();
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        questionsContainer.setBackground(Colors.PANEL_BACKGROUND);

        scrollPane = new JScrollPane(questionsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Colors.PANEL_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(850, 450));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        submitButton = new StyledButton("Получить персонализированную программу", Colors.ACCENT, Colors.ACCENT.darker(), Colors.ACCENT.darker().darker());
        submitButton.setPreferredSize(new Dimension(280, 45));
        submitButton.addActionListener(e -> onSubmit());
        submitButton.setEnabled(false);

        historyButton = new StyledButton("История тестов", Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
        historyButton.setPreferredSize(new Dimension(150, 45));
        historyButton.addActionListener(e -> {
            if (controller != null) {
                controller.showTestHistory();
            }
        });

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Colors.NORMAL_FONT);
        statusLabel.setForeground(Colors.TEXT_SECONDARY);

        footerPanel.add(submitButton);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(historyButton);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(statusLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setQuestions(List<MainController.QuestionData> questions) {
        questionsContainer.removeAll();
        questionPanels.clear();

        if (questions != null && !questions.isEmpty()) {
            for (MainController.QuestionData q : questions) {
                QuestionPanel qp = new QuestionPanel(q);
                questionPanels.add(qp);
                questionsContainer.add(qp);
                questionsContainer.add(Box.createVerticalStrut(10));
            }
            submitButton.setEnabled(true);
            statusLabel.setText("Готово. " + questions.size() + " вопросов загружено");
            statusLabel.setForeground(Colors.STATUS_LOW);
        } else {
            JLabel emptyLabel = new JLabel("Нет вопросов в базе данных");
            emptyLabel.setFont(Colors.NORMAL_FONT);
            emptyLabel.setForeground(Colors.DANGER);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsContainer.add(emptyLabel);
            statusLabel.setText("Ошибка загрузки вопросов");
            statusLabel.setForeground(Colors.DANGER);
        }

        questionsContainer.revalidate();
        questionsContainer.repaint();
    }

    public void clearAnswers() {
        for (QuestionPanel qp : questionPanels) {
            qp.resetAnswer();
        }
        statusLabel.setText(" ");
    }

    private void onSubmit() {
        if (controller != null && !questionPanels.isEmpty()) {
            List<Integer> answers = new ArrayList<>();
            for (QuestionPanel qp : questionPanels) {
                answers.add(qp.getSelectedAnswer());
            }
            controller.submitTest(answers);
        }
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    class QuestionPanel extends RoundedPanel {
        private MainController.QuestionData question;
        private JComboBox<String> answerCombo;

        public QuestionPanel(MainController.QuestionData question) {
            super(10, Colors.PANEL_BACKGROUND, Colors.BORDER, 1);
            this.question = question;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel questionLabel = new JLabel("<html><body style='width: 600px'>" + question.text + "</body></html>");
            questionLabel.setFont(Colors.NORMAL_FONT);
            questionLabel.setForeground(Colors.TEXT_PRIMARY);

            JLabel categoryLabel = new JLabel(getCategoryName(question.category));
            categoryLabel.setFont(Colors.SMALL_FONT);
            categoryLabel.setForeground(Colors.TEXT_LIGHT);
            categoryLabel.setBackground(getCategoryColor(question.category));
            categoryLabel.setOpaque(true);
            categoryLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

            String[] options = question.options;
            if (options == null || options.length == 0) {
                options = new String[]{"Никогда", "Редко", "Иногда", "Часто", "Постоянно"};
            }
            answerCombo = new JComboBox<>(options);
            answerCombo.setFont(Colors.NORMAL_FONT);
            answerCombo.setPreferredSize(new Dimension(250, 35));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            topPanel.add(questionLabel, BorderLayout.CENTER);
            topPanel.add(categoryLabel, BorderLayout.EAST);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            bottomPanel.add(new JLabel("Ответ: "));
            bottomPanel.add(answerCombo);

            add(topPanel, BorderLayout.NORTH);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        public int getSelectedAnswer() {
            return answerCombo.getSelectedIndex() + 1;
        }

        public void resetAnswer() {
            answerCombo.setSelectedIndex(0);
        }

        private String getCategoryName(String category) {
            switch (category) {
                case "symptoms": return "Симптомы";
                case "habits": return "Привычки";
                case "health": return "Здоровье";
                default: return category;
            }
        }

        private Color getCategoryColor(String category) {
            switch (category) {
                case "symptoms": return Colors.DANGER;
                case "habits": return Colors.WARNING;
                case "health": return Colors.PRIMARY;
                default: return Colors.TEXT_SECONDARY;
            }
        }
    }
}