package view.components;

import view.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;

    public StyledButton(String text) {
        this(text, Colors.PRIMARY, Colors.PRIMARY.darker(), Colors.PRIMARY.darker().darker());
    }

    public StyledButton(String text, Color normal, Color hover, Color pressed) {
        super(text);
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;

        setFont(Colors.BUTTON_FONT);
        setForeground(Colors.TEXT_LIGHT);
        setBackground(normalColor);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
        g2.dispose();
    }
}