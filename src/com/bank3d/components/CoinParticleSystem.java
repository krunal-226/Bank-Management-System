package com.bank3d.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CoinParticleSystem extends JPanel {
    private static class Coin3D {
        double x, y, z;
        double vx, vy, vz;
        double yaw, pitch;
        double vYaw, vPitch;
        double radius;
        double life;
        double maxLife;
        boolean isDeposit;

        Coin3D(double x, double y, double vx, double vy, boolean isDeposit) {
            this.x = x;
            this.y = y;
            this.z = 0;
            this.vx = vx;
            this.vy = vy;
            this.vz = (Math.random() - 0.5) * 2.0;
            this.yaw = Math.random() * Math.PI * 2;
            this.pitch = Math.random() * Math.PI * 2;
            this.vYaw = (Math.random() - 0.5) * 0.35 + 0.15;
            this.vPitch = (Math.random() - 0.5) * 0.25;
            this.radius = 16.0 + Math.random() * 8.0;
            this.maxLife = 120 + Math.random() * 60;
            this.life = maxLife;
            this.isDeposit = isDeposit;
        }

        void update(int floorY) {
            x += vx;
            y += vy;
            z += vz;

            // Gravity
            vy += 0.45;

            // Air drag
            vx *= 0.985;
            vz *= 0.985;

            // 3D rotations
            yaw += vYaw;
            pitch += vPitch;

            // Ground bounce
            if (y > floorY) {
                y = floorY;
                vy = -vy * 0.52; // energy loss
                vx *= 0.75;
            }

            life--;
        }
    }

    private final List<Coin3D> coins = new ArrayList<>();
    private final Random rng = new Random();
    private Timer physicsTimer;

    public CoinParticleSystem() {
        setOpaque(false);

        physicsTimer = new Timer(16, e -> {
            int floor = getHeight() - 20;
            synchronized (coins) {
                Iterator<Coin3D> it = coins.iterator();
                while (it.hasNext()) {
                    Coin3D c = it.next();
                    c.update(floor);
                    if (c.life <= 0) {
                        it.remove();
                    }
                }
            }
            if (!coins.isEmpty()) {
                repaint();
            }
        });
        physicsTimer.start();
    }

    public void burstDeposit(int count) {
        synchronized (coins) {
            int w = getWidth() > 0 ? getWidth() : 800;
            for (int i = 0; i < count; i++) {
                double sx = w * 0.25 + rng.nextDouble() * (w * 0.5);
                double sy = -20 - rng.nextDouble() * 60;
                double vx = (rng.nextDouble() - 0.5) * 4.0;
                double vy = 2.0 + rng.nextDouble() * 5.0;
                coins.add(new Coin3D(sx, sy, vx, vy, true));
            }
        }
        repaint();
    }

    public void burstWithdraw(int count) {
        synchronized (coins) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            for (int i = 0; i < count; i++) {
                double angle = rng.nextDouble() * Math.PI * 2;
                double speed = 5.0 + rng.nextDouble() * 9.0;
                double vx = Math.cos(angle) * speed;
                double vy = Math.sin(angle) * speed - 4.0;
                coins.add(new Coin3D(cx, cy, vx, vy, false));
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (coins.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        synchronized (coins) {
            for (Coin3D c : coins) {
                paint3DCoin(g2, c);
            }
        }

        g2.dispose();
    }

    private void paint3DCoin(Graphics2D g2, Coin3D c) {
        float alpha = (float) Math.max(0.0, Math.min(1.0, c.life / c.maxLife));
        if (alpha <= 0.01f) return;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // 3D perspective scales
        double cosY = Math.cos(c.yaw);
        double cosX = Math.cos(c.pitch);
        double rX = Math.abs(cosY) * c.radius;
        double rY = Math.abs(cosX) * c.radius;
        if (rX < 1.0) rX = 1.0;
        if (rY < 1.0) rY = 1.0;

        AffineTransform oldTx = g2.getTransform();
        g2.translate(c.x, c.y);

        // 3D Extruded Rim (Edge thickness)
        double rimThickness = 4.0 * Math.abs(Math.sin(c.yaw));
        if (rimThickness > 1.0) {
            g2.setColor(new Color(180, 83, 9)); // Dark bronze rim
            g2.fill(new Ellipse2D.Double(-rX - rimThickness / 2, -rY, (rX * 2) + rimThickness, rY * 2));
        }

        // Coin Face Gradient (Rich Radiant Gold)
        GradientPaint goldGrad = new GradientPaint(
            (float) -rX, (float) -rY, new Color(254, 240, 138),
            (float) rX, (float) rY, new Color(217, 119, 6)
        );
        g2.setPaint(goldGrad);
        g2.fill(new Ellipse2D.Double(-rX, -rY, rX * 2, rY * 2));

        // Coin Rim Ring
        g2.setColor(new Color(251, 191, 36));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new Ellipse2D.Double(-rX, -rY, rX * 2, rY * 2));

        // Currency Symbol inscribed on face
        if (rX > 8 && rY > 8) {
            g2.setColor(new Color(120, 53, 15));
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(c.radius * 0.95)));
            FontMetrics fm = g2.getFontMetrics();
            String sym = "Rs";
            int tx = -fm.stringWidth(sym) / 2;
            int ty = fm.getAscent() / 2 - 2;
            g2.drawString(sym, tx, ty);
        }

        g2.setTransform(oldTx);
    }
}
