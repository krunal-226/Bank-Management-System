package com.bank3d.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    public enum Style { PRIMARY, SECONDARY, SUCCESS, DANGER, GOLD }

    private final Style style;
    private boolean hovered = false;
    private boolean pressed = false;
    private float hoverAlpha = 0.0f;
    private Timer animTimer;

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(UITheme.FONT_BOLD);
        setForeground(UITheme.TEXT_PRIMARY);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        animTimer = new Timer(15, e -> {
            float target = hovered ? 1.0f : 0.0f;
            hoverAlpha += (target - hoverAlpha) * 0.25f;
            if (Math.abs(target - hoverAlpha) < 0.02f) {
                hoverAlpha = target;
                animTimer.stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                animTimer.start();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                animTimer.start();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int arc = 14;

        Color c1, c2, border;
        switch (style) {
            case PRIMARY:
                c1 = new Color(0, 180, 230);
                c2 = new Color(99, 102, 241);
                border = UITheme.ACCENT_CYAN;
                break;
            case SUCCESS:
                c1 = new Color(16, 185, 129);
                c2 = new Color(5, 150, 105);
                border = new Color(52, 211, 153);
                break;
            case DANGER:
                c1 = new Color(239, 68, 68);
                c2 = new Color(185, 28, 28);
                border = new Color(248, 113, 113);
                break;
            case GOLD:
                c1 = new Color(245, 158, 11);
                c2 = new Color(217, 119, 6);
                border = new Color(251, 191, 36);
                break;
            case SECONDARY:
            default:
                c1 = new Color(30, 41, 59, 210);
                c2 = new Color(15, 23, 42, 220);
                border = new Color(255, 255, 255, 40);
                break;
        }

        // Slight shift down on press
        int yOffset = pressed ? 1 : 0;

        // Button background
        GradientPaint gp = new GradientPaint(0, yOffset, c1, 0, h + yOffset, c2);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, h - 2, arc, arc));

        // Hover glowing sheen
        if (hoverAlpha > 0.01f) {
            g2.setColor(new Color(255, 255, 255, (int)(45 * hoverAlpha)));
            g2.fill(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, (h - 2) / 2.0f, arc, arc));

            // Glowing border
            g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(), (int)(180 * hoverAlpha)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f + yOffset, w - 3, h - 3, arc, arc));
        } else {
            g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(), 60));
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, h - 2, arc, arc));
        }

        // Text
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
        g2.setColor(Color.WHITE);
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }
}
