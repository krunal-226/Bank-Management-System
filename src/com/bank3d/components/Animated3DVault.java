package com.bank3d.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Animated3DVault extends JPanel {
    private double dialAngle = 0.0;
    private double targetDialAngle = 0.0;
    private double boltProgress = 1.0;
    private double targetBoltProgress = 1.0;
    private double doorOpenAngle = 0.0;
    private double targetDoorOpenAngle = 0.0;

    private boolean isUnlocked = false;
    private Timer animTimer;
    private Runnable unlockCallback = null;

    public Animated3DVault() {
        setPreferredSize(new Dimension(360, 360));
        setOpaque(false);

        animTimer = new Timer(16, e -> {
            boolean changed = false;

            double dialDiff = targetDialAngle - dialAngle;
            if (Math.abs(dialDiff) > 0.01) {
                dialAngle += dialDiff * 0.12;
                changed = true;
            } else {
                dialAngle = targetDialAngle;
            }

            double boltDiff = targetBoltProgress - boltProgress;
            if (Math.abs(boltDiff) > 0.01) {
                boltProgress += boltDiff * 0.14;
                changed = true;
            } else {
                boltProgress = targetBoltProgress;
            }

            double doorDiff = targetDoorOpenAngle - doorOpenAngle;
            if (Math.abs(doorDiff) > 0.01) {
                doorOpenAngle += doorDiff * 0.10;
                changed = true;
            } else {
                doorOpenAngle = targetDoorOpenAngle;
            }

            if (changed) {
                repaint();
            }
        });
        animTimer.start();
    }

    public void triggerUnlockSequence(Runnable onUnlocked) {
        this.unlockCallback = onUnlocked;
        this.isUnlocked = true;

        // Stage 1: Spin dial
        targetDialAngle += Math.PI * 4.5;

        // Stage 2: Retract bolts after 300ms
        Timer t1 = new Timer(300, e -> {
            targetBoltProgress = 0.0;
            ((Timer) e.getSource()).stop();
        });
        t1.setRepeats(false);
        t1.start();

        // Stage 3: Swing open door after 600ms
        Timer t2 = new Timer(600, e -> {
            targetDoorOpenAngle = Math.PI / 2.5;
            ((Timer) e.getSource()).stop();
        });
        t2.setRepeats(false);
        t2.start();

        // Stage 4: Trigger dashboard transition after door is open (1200ms)
        Timer t3 = new Timer(1200, e -> {
            if (unlockCallback != null) {
                Runnable cb = unlockCallback;
                unlockCallback = null;
                cb.run();
            }
            ((Timer) e.getSource()).stop();
        });
        t3.setRepeats(false);
        t3.start();
    }

    public void triggerLockSequence(Runnable onLocked) {
        this.isUnlocked = false;
        targetDoorOpenAngle = 0.0;

        Timer t1 = new Timer(350, e -> {
            targetBoltProgress = 1.0;
            targetDialAngle = 0.0;
            if (onLocked != null) onLocked.run();
            ((Timer) e.getSource()).stop();
        });
        t1.setRepeats(false);
        t1.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.min(cx, cy) - 25;

        paintVaultFrame(g2, cx, cy, radius);

        if (doorOpenAngle > 0.01) {
            paintVaultInterior(g2, cx, cy, radius);
        }

        paintVaultDoor3D(g2, cx, cy, radius);

        g2.dispose();
    }

    private void paintVaultFrame(Graphics2D g2, int cx, int cy, int r) {
        g2.setColor(new Color(15, 20, 32));
        g2.fillOval(cx - r - 16, cy - r - 16, (r + 16) * 2, (r + 16) * 2);

        GradientPaint frameGrad = new GradientPaint(
            cx - r, cy - r, new Color(51, 65, 85),
            cx + r, cy + r, new Color(15, 23, 42)
        );
        g2.setPaint(frameGrad);
        g2.setStroke(new BasicStroke(16.0f));
        g2.drawOval(cx - r - 4, cy - r - 4, (r + 4) * 2, (r + 4) * 2);

        int rivetCount = 16;
        for (int i = 0; i < rivetCount; i++) {
            double angle = (i * 2.0 * Math.PI) / rivetCount;
            int rx = (int) (cx + (r + 4) * Math.cos(angle));
            int ry = (int) (cy + (r + 4) * Math.sin(angle));

            g2.setColor(new Color(30, 41, 59));
            g2.fillOval(rx - 5, ry - 5, 10, 10);
            g2.setColor(new Color(148, 163, 184));
            g2.fillOval(rx - 3, ry - 3, 5, 5);
        }
    }

    private void paintVaultInterior(Graphics2D g2, int cx, int cy, int r) {
        Shape oldClip = g2.getClip();
        g2.setClip(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));

        RadialGradientPaint rgp = new RadialGradientPaint(
            new Point2D.Float(cx, cy), r,
            new float[]{ 0.0f, 0.4f, 1.0f },
            new Color[]{
                new Color(253, 224, 71, 230),
                new Color(217, 119, 6, 180),
                new Color(30, 20, 10, 240)
            }
        );
        g2.setPaint(rgp);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        paintGoldBars(g2, cx, cy);

        g2.setColor(new Color(0, 235, 255, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx - r, cy - 20, cx + r, cy - 20);
        g2.drawLine(cx - r, cy + 20, cx + r, cy + 20);

        g2.setClip(oldClip);
    }

    private void paintGoldBars(Graphics2D g2, int cx, int cy) {
        int bw = 80, bh = 22;
        int bx = cx - bw / 2, by = cy + 10;

        for (int layer = 0; layer < 2; layer++) {
            int yOff = by - (layer * 22);
            GradientPaint barGrad = new GradientPaint(
                bx, yOff, new Color(254, 240, 138),
                bx, yOff + bh, new Color(180, 83, 9)
            );
            g2.setPaint(barGrad);
            g2.fillRoundRect(bx, yOff, bw, bh, 6, 6);

            g2.setColor(new Color(113, 63, 18));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(bx, yOff, bw, bh, 6, 6);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.setColor(new Color(69, 26, 3));
            g2.drawString("999.9 FINE GOLD", bx + 8, yOff + 14);
        }
    }

    private void paintVaultDoor3D(Graphics2D g2, int cx, int cy, int r) {
        AffineTransform oldTx = g2.getTransform();

        double hingeX = cx - r;
        double cosOpen = Math.cos(doorOpenAngle);
        double sinOpen = Math.sin(doorOpenAngle);

        AffineTransform at = new AffineTransform();
        at.translate(hingeX, cy);
        at.scale(cosOpen, 1.0);
        at.shear(0, sinOpen * 0.15);
        at.translate(-(cx - hingeX), 0);

        g2.transform(at);

        GradientPaint doorGrad = new GradientPaint(
            cx - r, cy - r, new Color(51, 65, 85),
            cx + r, cy + r, new Color(15, 23, 42)
        );
        g2.setPaint(doorGrad);
        g2.fillOval(cx - r + 4, cy - r + 4, (r - 4) * 2, (r - 4) * 2);

        g2.setColor(new Color(100, 116, 139));
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawOval(cx - r + 10, cy - r + 10, (r - 10) * 2, (r - 10) * 2);

        int boltCount = 6;
        int boltLen = (int) (14 + boltProgress * 18);
        for (int i = 0; i < boltCount; i++) {
            double angle = (i * 2.0 * Math.PI) / boltCount;
            int bStartX = (int) (cx + (r - 16) * Math.cos(angle));
            int bStartY = (int) (cy + (r - 16) * Math.sin(angle));
            int bEndX = (int) (cx + (r - 16 + boltLen) * Math.cos(angle));
            int bEndY = (int) (cy + (r - 16 + boltLen) * Math.sin(angle));

            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(bStartX, bStartY, bEndX, bEndY);
        }

        paintCombinationDial(g2, cx, cy, (int)(r * 0.52));

        g2.setTransform(oldTx);
    }

    private void paintCombinationDial(Graphics2D g2, int cx, int cy, int dr) {
        GradientPaint dialBase = new GradientPaint(
            cx - dr, cy - dr, new Color(30, 41, 59),
            cx + dr, cy + dr, new Color(15, 23, 42)
        );
        g2.setPaint(dialBase);
        g2.fillOval(cx - dr, cy - dr, dr * 2, dr * 2);

        g2.setColor(UITheme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(cx - dr, cy - dr, dr * 2, dr * 2);

        int ticks = 36;
        for (int i = 0; i < ticks; i++) {
            double a = dialAngle + (i * 2.0 * Math.PI) / ticks;
            int t1 = dr - 8;
            int t2 = (i % 3 == 0) ? dr - 18 : dr - 12;
            int x1 = (int) (cx + t1 * Math.cos(a));
            int y1 = (int) (cy + t1 * Math.sin(a));
            int x2 = (int) (cx + t2 * Math.cos(a));
            int y2 = (int) (cy + t2 * Math.sin(a));

            g2.setColor((i % 3 == 0) ? Color.WHITE : new Color(148, 163, 184));
            g2.setStroke(new BasicStroke((i % 3 == 0) ? 2.0f : 1.0f));
            g2.drawLine(x1, y1, x2, y2);
        }

        int spokeCount = 3;
        int spokeLen = dr - 14;
        for (int i = 0; i < spokeCount; i++) {
            double sa = dialAngle + (i * 2.0 * Math.PI) / spokeCount;
            int sx = (int) (cx + spokeLen * Math.cos(sa));
            int sy = (int) (cy + spokeLen * Math.sin(sa));

            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(7.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx, cy, sx, sy);

            g2.setColor(UITheme.ACCENT_CYAN);
            g2.fillOval(sx - 7, sy - 7, 14, 14);
        }

        g2.setColor(new Color(30, 41, 59));
        g2.fillOval(cx - 18, cy - 18, 36, 36);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(cx - 18, cy - 18, 36, 36);

        g2.setColor(isUnlocked ? UITheme.ACCENT_GREEN : UITheme.ACCENT_CYAN);
        g2.fillOval(cx - 6, cy - 6, 12, 12);
    }
}
