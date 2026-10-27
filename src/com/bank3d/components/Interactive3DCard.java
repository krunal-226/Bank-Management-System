package com.bank3d.components;

import com.bank3d.models.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Interactive3DCard extends JPanel {
    private Account account;

    private static final int CARD_W = 340;
    private static final int CARD_H = 210;

    private double currentPitch = 0.0;
    private double currentYaw = 0.0;
    private double targetPitch = 0.0;
    private double targetYaw = 0.0;

    private boolean isFlipped = false;
    private double flipProgress = 0.0;
    private double targetFlipProgress = 0.0;

    private Timer renderTimer;
    private BufferedImage frontCache;
    private BufferedImage backCache;

    public Interactive3DCard(Account account) {
        this.account = account;
        setPreferredSize(new Dimension(420, 290));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        regenerateCardCaches();

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                double dx = (e.getX() - cx) / (double) cx;
                double dy = (e.getY() - cy) / (double) cy;

                dx = Math.max(-1.0, Math.min(1.0, dx));
                dy = Math.max(-1.0, Math.min(1.0, dy));

                targetYaw = dx * 0.42;
                targetPitch = -dy * 0.32;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                targetYaw = 0.0;
                targetPitch = 0.0;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                toggleFlip();
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        renderTimer = new Timer(16, e -> {
            currentPitch += (targetPitch - currentPitch) * 0.14;
            currentYaw += (targetYaw - currentYaw) * 0.14;

            double diff = targetFlipProgress - flipProgress;
            if (Math.abs(diff) > 0.005) {
                flipProgress += diff * 0.15;
            } else {
                flipProgress = targetFlipProgress;
            }

            repaint();
        });
        renderTimer.start();
    }

    public void setAccount(Account acc) {
        this.account = acc;
        regenerateCardCaches();
        repaint();
    }

    public void toggleFlip() {
        isFlipped = !isFlipped;
        targetFlipProgress = isFlipped ? 1.0 : 0.0;
    }

    public boolean isCardFlipped() {
        return isFlipped;
    }

    public void regenerateCardCaches() {
        frontCache = new BufferedImage(CARD_W, CARD_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = frontCache.createGraphics();
        UITheme.applyQualityRendering(g2);
        paintCardFront(g2);
        g2.dispose();

        backCache = new BufferedImage(CARD_W, CARD_H, BufferedImage.TYPE_INT_ARGB);
        g2 = backCache.createGraphics();
        UITheme.applyQualityRendering(g2);
        paintCardBack(g2);
        g2.dispose();
    }

    private void paintCardFront(Graphics2D g2) {
        int w = CARD_W;
        int h = CARD_H;
        int arc = 22;

        GradientPaint gp = new GradientPaint(
            0, 0, new Color(15, 23, 42),
            w, h, new Color(30, 27, 75)
        );
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = -100; i < w + 100; i += 35) {
            g2.drawLine(i, 0, i + 80, h);
        }

        g2.setColor(new Color(0, 235, 255, 90));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        g2.drawString("NEXUS VAULT", 24, 34);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(UITheme.ACCENT_CYAN);
        g2.drawString("PLATINUM ELITE", 25, 48);

        g2.setColor(new Color(255, 255, 255, 180));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int wx = 150, wy = 36;
        g2.drawArc(wx - 4, wy - 4, 8, 8, 45, 90);
        g2.drawArc(wx - 8, wy - 8, 16, 16, 45, 90);
        g2.drawArc(wx - 12, wy - 12, 24, 24, 45, 90);

        int chipX = 26, chipY = 66, chipW = 46, chipH = 34;
        GradientPaint chipGrad = new GradientPaint(
            chipX, chipY, new Color(253, 224, 71),
            chipX + chipW, chipY + chipH, new Color(202, 138, 4)
        );
        g2.setPaint(chipGrad);
        g2.fill(new RoundRectangle2D.Float(chipX, chipY, chipW, chipH, 8, 8));

        g2.setColor(new Color(113, 63, 18, 190));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(chipX + 6, chipY + 4, chipW - 12, chipH - 8, 4, 4);
        g2.drawLine(chipX + chipW / 2, chipY + 4, chipX + chipW / 2, chipY + chipH - 4);
        g2.drawLine(chipX + 6, chipY + chipH / 2, chipX + chipW - 6, chipY + chipH / 2);

        g2.setFont(UITheme.FONT_CARD_NUM);
        String accNum = (account != null) ? account.getAccountNumber() : "101";
        String cardNum = "4532  8901  2456  " + String.format("%04d", Integer.parseInt(accNum.replaceAll("\\D", "0") + "0") % 10000);
        
        g2.setColor(new Color(0, 0, 0, 160));
        g2.drawString(cardNum, 26, 135);
        g2.setColor(new Color(243, 244, 246));
        g2.drawString(cardNum, 25, 134);

        String name = (account != null) ? account.getHolderName().toUpperCase() : "KRUNAL";
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(156, 163, 175));
        g2.drawString("CARDHOLDER", 25, 165);
        g2.setColor(Color.WHITE);
        g2.drawString(name, 25, 182);

        g2.setColor(new Color(156, 163, 175));
        g2.drawString("EXPIRES", 160, 165);
        g2.setColor(Color.WHITE);
        g2.drawString("12 / 30", 160, 182);

        int hx = 265, hy = 160;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2.setColor(new Color(239, 68, 68));
        g2.fillOval(hx, hy, 34, 34);
        g2.setColor(new Color(245, 158, 11));
        g2.fillOval(hx + 18, hy, 34, 34);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void paintCardBack(Graphics2D g2) {
        int w = CARD_W;
        int h = CARD_H;
        int arc = 22;

        GradientPaint gp = new GradientPaint(
            0, 0, new Color(20, 24, 35),
            w, h, new Color(10, 14, 23)
        );
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        g2.setColor(new Color(15, 15, 15));
        g2.fillRect(0, 26, w, 44);

        g2.setColor(new Color(230, 230, 235));
        g2.fillRect(20, 92, 220, 32);

        g2.setColor(Color.WHITE);
        g2.fillRect(240, 92, 60, 32);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Consolas", Font.BOLD, 15));
        g2.drawString("842", 258, 113);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(new Color(100, 116, 139));
        g2.drawString("CVV", 258, 86);

        GradientPaint holo = new GradientPaint(
            20, 145, new Color(139, 92, 246, 160),
            65, 175, new Color(0, 235, 255, 180)
        );
        g2.setPaint(holo);
        g2.fill(new RoundRectangle2D.Float(20, 140, 48, 32, 6, 6));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString("24/7 Global Concierge: 1800-VAULT-3D", 80, 153);
        g2.drawString("Authorized Signature - Not Transferable", 80, 167);
        g2.drawString("Property of Nexus Vault. If found, return to nearest branch.", 20, 192);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        double flipAngle = flipProgress * Math.PI;
        double totalYaw = currentYaw + flipAngle;
        double pitch = currentPitch;

        boolean showBack = Math.cos(totalYaw) < 0;

        double cosY = Math.cos(totalYaw);
        double sinY = Math.sin(totalYaw);
        double cosX = Math.cos(pitch);
        double sinX = Math.sin(pitch);

        double shearX = sinY * sinX * 0.35;
        double scaleX = Math.abs(cosY);
        double scaleY = cosX;

        if (scaleX < 0.02) scaleX = 0.02;

        int shadowDistX = (int) (-sinY * 26);
        int shadowDistY = (int) (sinX * 24) + 14;
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fill(new RoundRectangle2D.Float(
            cx - (float)(CARD_W * scaleX / 2) + shadowDistX,
            cy - (float)(CARD_H * scaleY / 2) + shadowDistY,
            (float)(CARD_W * scaleX),
            (float)(CARD_H * scaleY),
            22, 22
        ));

        AffineTransform oldTx = g2.getTransform();
        AffineTransform at = new AffineTransform();
        at.translate(cx, cy);
        at.shear(shearX, 0);
        at.scale(scaleX, scaleY);
        at.translate(-CARD_W / 2.0, -CARD_H / 2.0);

        g2.transform(at);

        BufferedImage face = showBack ? backCache : frontCache;
        g2.drawImage(face, 0, 0, null);

        double sheenOffset = (sinY * 1.5 - sinX * 1.2);
        float sheenCenter = (float) (0.5 + sheenOffset * 0.5);
        sheenCenter = Math.max(0.1f, Math.min(0.9f, sheenCenter));

        LinearGradientPaint sheen = new LinearGradientPaint(
            new Point2D.Float(0, 0),
            new Point2D.Float(CARD_W, CARD_H),
            new float[]{ 0.0f, Math.max(0.01f, sheenCenter - 0.2f), sheenCenter, Math.min(0.99f, sheenCenter + 0.2f), 1.0f },
            new Color[]{
                new Color(255, 255, 255, 0),
                new Color(255, 255, 255, 0),
                new Color(255, 255, 255, (int)(95 * (1.0 - Math.abs(sheenOffset * 0.5)))),
                new Color(255, 255, 255, 0),
                new Color(255, 255, 255, 0)
            }
        );
        g2.setPaint(sheen);
        g2.fill(new RoundRectangle2D.Float(0, 0, CARD_W, CARD_H, 22, 22));

        g2.setTransform(oldTx);
        g2.dispose();
    }
}
