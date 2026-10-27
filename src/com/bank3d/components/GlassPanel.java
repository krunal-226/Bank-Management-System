package com.bank3d.components;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JPanel {
    private int cornerRadius = 18;
    private boolean highlighted = false;

    public GlassPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public GlassPanel() {
        this(18);
    }

    public void setHighlighted(boolean h) {
        this.highlighted = h;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);
        UITheme.drawGlassPanel(g2, 0, 0, getWidth(), getHeight(), cornerRadius, highlighted);
        g2.dispose();
        super.paintComponent(g);
    }
}
