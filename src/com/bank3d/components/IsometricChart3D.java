package com.bank3d.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class IsometricChart3D extends JPanel {
    public static class BarData {
        public String label;
        public double amount;
        public boolean isIncome;

        public BarData(String label, double amount, boolean isIncome) {
            this.label = label;
            this.amount = amount;
            this.isIncome = isIncome;
        }
    }

    private final List<BarData> data = new ArrayList<>();
    private double animProgress = 0.0;
    private Timer animTimer;
    private int hoveredIndex = -1;

    public IsometricChart3D() {
        setPreferredSize(new Dimension(480, 260));
        setOpaque(false);

        // Sample initial cash flow bars
        data.add(new BarData("Jan", 3500, true));
        data.add(new BarData("Feb", 1800, false));
        data.add(new BarData("Mar", 4200, true));
        data.add(new BarData("Apr", 2100, false));
        data.add(new BarData("May", 5000, true));
        data.add(new BarData("Jun", 1500, false));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Approximate hit testing for columns
                int w = getWidth();
                int colW = w / Math.max(1, data.size());
                int idx = e.getX() / colW;
                if (idx != hoveredIndex && idx >= 0 && idx < data.size()) {
                    hoveredIndex = idx;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }
        });

        animTimer = new Timer(16, e -> {
            if (animProgress < 1.0) {
                animProgress += (1.0 - animProgress) * 0.10 + 0.01;
                if (animProgress >= 0.99) {
                    animProgress = 1.0;
                    animTimer.stop();
                }
                repaint();
            }
        });
        animTimer.start();
    }

    public void updateData(double depositSum, double withdrawSum) {
        data.clear();
        data.add(new BarData("Prev Bal", 5000, true));
        data.add(new BarData("Deposits", Math.max(500, depositSum), true));
        data.add(new BarData("Withdrawals", Math.max(300, withdrawSum), false));
        data.add(new BarData("Net Inflow", Math.max(500, depositSum - withdrawSum), depositSum >= withdrawSum));
        animProgress = 0.0;
        animTimer.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int baseY = h - 45;

        // Header Title
        g2.setFont(UITheme.FONT_SUBTITLE);
        g2.setColor(UITheme.TEXT_PRIMARY);
        g2.drawString("3D Isometric Cash Flow Analysis", 15, 24);

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        g2.drawString("Real-time 3D transaction pillars", 15, 40);

        // Ground Grid Lines
        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(15, baseY, w - 15, baseY);

        if (data.isEmpty()) {
            g2.dispose();
            return;
        }

        double maxVal = 6000.0;
        for (BarData b : data) {
            if (b.amount > maxVal) maxVal = b.amount;
        }

        int count = data.size();
        int slotW = (w - 40) / count;
        int barW = Math.min(36, slotW - 12);
        int barD = 16; // 3D depth

        for (int i = 0; i < count; i++) {
            BarData d = data.get(i);
            int cx = 25 + i * slotW + (slotW / 2);
            double targetH = (d.amount / maxVal) * (baseY - 70);
            int barH = (int) (targetH * animProgress);

            boolean isHovered = (i == hoveredIndex);
            if (isHovered) barH += 6;

            paint3DPillar(g2, cx, baseY, barW, barH, barD, d.isIncome, isHovered);

            // Label
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(isHovered ? Color.WHITE : UITheme.TEXT_SECONDARY);
            FontMetrics fm = g2.getFontMetrics();
            int lx = cx - fm.stringWidth(d.label) / 2;
            g2.drawString(d.label, lx, baseY + 18);

            // Amount label on hover
            if (isHovered) {
                String amtStr = String.format("Rs. %,.0f", d.amount);
                int ax = cx - fm.stringWidth(amtStr) / 2;
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.drawString(amtStr, ax, baseY - barH - barD - 8);
            }
        }

        g2.dispose();
    }

    private void paint3DPillar(Graphics2D g2, int x, int baseY, int w, int h, int d, boolean isIncome, boolean highlight) {
        int left = x - w / 2;
        int right = x + w / 2;
        int topY = baseY - h;

        Color topColor, leftColor, rightColor;
        if (isIncome) {
            topColor = highlight ? new Color(52, 211, 153) : UITheme.ACCENT_GREEN;
            leftColor = new Color(5, 150, 105);
            rightColor = new Color(4, 120, 87);
        } else {
            topColor = highlight ? new Color(248, 113, 113) : UITheme.ACCENT_RED;
            leftColor = new Color(220, 38, 38);
            rightColor = new Color(185, 28, 28);
        }

        // 1. Front-Left Face
        Path2D front = new Path2D.Double();
        front.moveTo(left, baseY);
        front.lineTo(left, topY);
        front.lineTo(right, topY);
        front.lineTo(right, baseY);
        front.closePath();
        g2.setColor(leftColor);
        g2.fill(front);

        // 2. Right 3D Extrusion Face
        Path2D side = new Path2D.Double();
        side.moveTo(right, baseY);
        side.lineTo(right, topY);
        side.lineTo(right + d, topY - d / 2);
        side.lineTo(right + d, baseY - d / 2);
        side.closePath();
        g2.setColor(rightColor);
        g2.fill(side);

        // 3. Top Isometric Rhombus Face
        Path2D top = new Path2D.Double();
        top.moveTo(left, topY);
        top.lineTo(left + d, topY - d / 2);
        top.lineTo(right + d, topY - d / 2);
        top.lineTo(right, topY);
        top.closePath();
        g2.setColor(topColor);
        g2.fill(top);

        // Top Face Highlight Outline
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(top);
    }
}
