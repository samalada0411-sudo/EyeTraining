import model.DatabaseManager;
import view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Устанавливаем стиль интерфейса как системный
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Инициализируем базу данных
        DatabaseManager.initializeDatabase();

        // Запускаем GUI в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}