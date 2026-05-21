package view;

import controller.MainController;
import view.components.StyledButton;
import model.User;
import dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private TestPanel testPanel;
    private ProgramPanel programPanel;
    private PresetProgramsPanel presetPanel;
    private MainController controller;

    private JComboBox<String> userCombo;
    private DefaultComboBoxModel<String> userComboModel;
    private java.util.List<User> users;

    public MainFrame() {
        initUI();
        controller = new MainController(this);
        testPanel.setController(controller);  // ЭТО ВАЖНО!
        programPanel.setController(controller);
        presetPanel.setController(controller);
        loadUsers();
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
        userCombo.setPreferredSize(new Dimension(150, 30));
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
        try {
            UserDAO userDAO = new UserDAO();
            users = userDAO.findAll();
            userComboModel.removeAllElements();
            for (User u : users) {
                userComboModel.addElement(u.getName() + " (ID:" + u.getId() + ")");
            }
            if (!users.isEmpty()) {
                userCombo.setSelectedIndex(0);
                onUserSelected();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onUserSelected() {
        if (userCombo.getSelectedIndex() >= 0 && users != null && userCombo.getSelectedIndex() < users.size()) {
            User selected = users.get(userCombo.getSelectedIndex());
            if (controller != null) {
                controller.setCurrentUser(selected);
                // Очищаем ответы на тест
                testPanel.clearAnswers();
                // Показываем пустое состояние в программе
                programPanel.showEmptyState();
            }
        }
    }

    private void showAddUserDialog() {
        JTextField nameField = new JTextField(15);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        ageSpinner.setPreferredSize(new Dimension(80, 30));
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 24, 1));
        hoursSpinner.setPreferredSize(new Dimension(80, 30));

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

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Часов за ПК в день:"), gbc);
        gbc.gridx = 1;
        panel.add(hoursSpinner, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Новый пользователь", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int age = (Integer) ageSpinner.getValue();
                int hours = (Integer) hoursSpinner.getValue();

                User newUser = new User(name, age, hours);
                UserDAO userDAO = new UserDAO();
                userDAO.save(newUser);

                loadUsers();
                JOptionPane.showMessageDialog(this, "Пользователь добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка базы данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void setSelectedUser(int userId, String userName) {
        for (int i = 0; i < userComboModel.getSize(); i++) {
            if (userComboModel.getElementAt(i).contains("ID:" + userId)) {
                userCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public TestPanel getTestPanel() { return testPanel; }
    public ProgramPanel getProgramPanel() { return programPanel; }
    public PresetProgramsPanel getPresetPanel() { return presetPanel; }
}