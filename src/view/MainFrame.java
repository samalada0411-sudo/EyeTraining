package view;

import controller.MainController;
import view.components.StyledButton;
import model.User;
import model.TestHistoryEntry;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame implements MainController.ViewListener {

    private JTabbedPane tabbedPane;
    private TestPanel testPanel;
    private ProgramPanel programPanel;
    private PresetProgramsPanel presetPanel;
    private MainController controller;

    private JComboBox<String> userCombo;
    private DefaultComboBoxModel<String> userComboModel;
    private List<User> users;

    public MainFrame() {
        initUI();

        this.controller = new MainController();
        this.controller.setViewListener(this);

        testPanel.setController(controller);
        programPanel.setController(controller);
        presetPanel.setController(controller);

        loadUsers();
        controller.loadQuestions();
    }

    private void initUI() {
        setTitle("Тренировка глаз - Персональный помощник");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Colors.BACKGROUND);

        testPanel = new TestPanel();
        programPanel = new ProgramPanel();
        presetPanel = new PresetProgramsPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Colors.NORMAL_FONT);
        tabbedPane.setBackground(Colors.PANEL_BACKGROUND);

        tabbedPane.addTab("Тест-опросник", testPanel);
        tabbedPane.addTab("Моя программа", programPanel);
        tabbedPane.addTab("Готовые программы", presetPanel);

        JPanel headerPanel = createHeaderPanel();
        JPanel footerPanel = createFooterPanel();

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colors.PRIMARY);
        header.setPreferredSize(new Dimension(950, 80));

        JLabel titleLabel = new JLabel("Тренировка глаз", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Colors.TEXT_LIGHT);

        JLabel subtitleLabel = new JLabel("Персонализированные упражнения для здоровья ваших глаз", SwingConstants.CENTER);
        subtitleLabel.setFont(Colors.SMALL_FONT);
        subtitleLabel.setForeground(Colors.TEXT_LIGHT);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Пользователь:");
        userLabel.setForeground(Colors.TEXT_LIGHT);
        userLabel.setFont(Colors.NORMAL_FONT);

        userComboModel = new DefaultComboBoxModel<>();
        userCombo = new JComboBox<>(userComboModel);
        userCombo.setFont(Colors.NORMAL_FONT);
        userCombo.setPreferredSize(new Dimension(180, 30));
        userCombo.addActionListener(e -> onUserSelected());

        StyledButton addUserBtn = new StyledButton("+ Новый", Colors.ACCENT, Colors.ACCENT.darker(), Colors.ACCENT.darker().darker());
        addUserBtn.setPreferredSize(new Dimension(100, 30));
        addUserBtn.addActionListener(e -> showAddUserDialog());

        userPanel.add(userLabel);
        userPanel.add(userCombo);
        userPanel.add(addUserBtn);

        header.add(userPanel, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return header;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Colors.SECONDARY);
        footer.setPreferredSize(new Dimension(950, 35));

        JLabel infoLabel = new JLabel("Совет: Делайте перерывы каждые 20 минут работы за компьютером");
        infoLabel.setFont(Colors.SMALL_FONT);
        infoLabel.setForeground(Colors.TEXT_SECONDARY);

        footer.add(infoLabel);
        return footer;
    }

    private void loadUsers() {
        users = controller.getAllUsers();
        userComboModel.removeAllElements();
        for (User u : users) {
            userComboModel.addElement(u.getName() + " (ID:" + u.getId() + ")");
        }
        if (!users.isEmpty()) {
            userCombo.setSelectedIndex(0);
            onUserSelected();
        }
    }

    private void onUserSelected() {
        if (userCombo.getSelectedIndex() >= 0 && users != null && userCombo.getSelectedIndex() < users.size()) {
            User selected = users.get(userCombo.getSelectedIndex());
            controller.setCurrentUser(selected);
            testPanel.clearAnswers();
        }
    }

    private void showAddUserDialog() {
        JTextField nameField = new JTextField(15);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        ageSpinner.setPreferredSize(new Dimension(80, 30));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Имя:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Возраст:"), gbc);
        gbc.gridx = 1;
        panel.add(ageSpinner, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Новый пользователь", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int age = (Integer) ageSpinner.getValue();
            controller.addUser(name, age);
            loadUsers();
        }
    }

    @Override
    public void onQuestionsLoaded(List<MainController.QuestionData> questions) {
        testPanel.setQuestions(questions);
    }

    @Override
    public void onProgramDisplayed(List<MainController.ExerciseData> exercises, String loadLevel, int totalDuration, int totalScore) {
        programPanel.displayProgram(exercises, loadLevel, totalDuration, totalScore);
        tabbedPane.setSelectedIndex(1);
    }

    @Override
    public void onShowEmptyProgram() {
        programPanel.showEmptyState();
    }

    @Override
    public void onShowMessage(String title, String message, boolean isError) {
        if (isError) {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
        testPanel.showStatus(message, isError ? Colors.DANGER : Colors.ACCENT);
    }

    @Override
    public void onShowPresetProgramDetails(String programName, List<MainController.PresetExerciseInfo> exercises) {
        presetPanel.showProgramDetails(programName, exercises);
    }

    @Override
    public void onShowTestHistory(List<TestHistoryEntry> history) {
        showTestHistoryDialog(history);
    }

    private void showTestHistoryDialog(List<TestHistoryEntry> history) {
        JDialog dialog = new JDialog(this, "История прохождения теста", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Colors.PANEL_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("История тестирований");
        titleLabel.setFont(Colors.HEADER_FONT);
        titleLabel.setForeground(Colors.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Дата", "Балл", "Уровень нагрузки"};
        Object[][] data = new Object[history.size()][3];
        for (int i = 0; i < history.size(); i++) {
            TestHistoryEntry entry = history.get(i);
            data[i][0] = entry.getTestDate();
            data[i][1] = entry.getTotalScore();
            String levelText = "";
            switch (entry.getLoadLevel()) {
                case "low":
                    levelText = "Низкий";
                    break;
                case "medium":
                    levelText = "Средний";
                    break;
                case "high":
                    levelText = "Высокий";
                    break;
            }
            data[i][2] = levelText;
        }

        JTable table = new JTable(data, columnNames);
        table.setFont(Colors.NORMAL_FONT);
        table.setRowHeight(30);

        table.getTableHeader().setFont(Colors.TITLE_FONT);
        table.getTableHeader().setBackground(Colors.PRIMARY);
        table.getTableHeader().setForeground(Color.BLACK);  // ЧЁРНЫЙ ЦВЕТ ТЕКСТА

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}