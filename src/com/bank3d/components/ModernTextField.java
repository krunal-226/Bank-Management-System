package com.bank3d.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernTextField extends JTextField {
    private String placeholder = "";
    private boolean focused = false;

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setFont(UITheme.FONT_REGULAR);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.ACCENT_CYAN);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String p) {
        this.placeholder = p;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int arc = 12;

        // Background
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        // Border glow
        if (focused) {
            g2.setColor(UITheme.ACCENT_CYAN);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(new Color(255, 255, 255, 30));
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        super.paintComponent(g);

        // Placeholder text
        if (getText().isEmpty() && !focused && placeholder != null && !placeholder.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(UITheme.TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            int py = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 14, py);
        }

        g2.dispose();
    }
}
