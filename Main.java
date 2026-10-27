import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Main {
    private static BankSystem bank = new BankSystem();
    private static Account currentAccount = null;

    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainContainer;
    private static LoginView loginView;
    private static DashboardView dashboardView;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            bank.addAccount(new SavingsAccount("101", "1234", "Krunal", 5000.0));

            frame = new JFrame("Smart ATM / Bank Management System");
            frame.setSize(1240, 840);
            frame.setMinimumSize(new Dimension(1024, 720));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(UITheme.BG_DARK);

            cardLayout = new CardLayout();
            mainContainer = new JPanel(cardLayout);
            mainContainer.setOpaque(false);

            loginView = new LoginView(bank, loggedInAccount -> {
                currentAccount = loggedInAccount;
                if (dashboardView == null) {
                    dashboardView = new DashboardView(frame, currentAccount, () -> logout());
                    mainContainer.add(dashboardView, "DASHBOARD");
                } else {
                    dashboardView.updateAccount(currentAccount);
                }
                cardLayout.show(mainContainer, "DASHBOARD");
            });

            mainContainer.add(loginView, "LOGIN");

            frame.add(mainContainer);
            frame.setVisible(true);
        });
    }

    private static void logout() {
        currentAccount = null;
        cardLayout.show(mainContainer, "LOGIN");
    }
}

class Vector3D {
    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D rotateX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector3D(x, y * c - z * s, y * s + z * c);
    }

    public Vector3D rotateY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector3D(x * c + z * s, y, -x * s + z * c);
    }

    public double[] project(double fov, double screenW, double screenH) {
        double dist = fov + z;
        if (dist < 10.0) dist = 10.0;
        double scale = fov / dist;
        double px = (x * scale) + (screenW / 2.0);
        double py = (y * scale) + (screenH / 2.0);
        return new double[]{ px, py, scale };
    }
}

class UITheme {
    public static final Color BG_DARK = new Color(10, 14, 23);
    public static final Color SURFACE_GLASS = new Color(18, 25, 40, 210);
    public static final Color SURFACE_BORDER = new Color(255, 255, 255, 24);
    public static final Color SURFACE_BORDER_GLOW = new Color(0, 235, 255, 90);

    public static final Color ACCENT_CYAN = new Color(0, 235, 255);
    public static final Color ACCENT_GOLD = new Color(245, 158, 11);
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);
    public static final Color ACCENT_RED = new Color(239, 68, 68);

    public static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    public static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.BOLD, 15);
    public static final Font FONT_CARD_NUM = new Font("Consolas", Font.BOLD, 17);

    public static void applyQualityRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    public static void drawGlassPanel(Graphics2D g2, int x, int y, int w, int h, int arc, boolean highlight) {
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 4, w - 4, h - 2, arc, arc));

        GradientPaint gp = new GradientPaint(
            x, y, highlight ? new Color(26, 36, 56, 220) : SURFACE_GLASS,
            x, y + h, highlight ? new Color(17, 24, 39, 230) : new Color(12, 17, 28, 220)
        );
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, arc, arc));

        g2.setColor(highlight ? SURFACE_BORDER_GLOW : SURFACE_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, arc, arc));
    }
}

class ModernButton extends JButton {
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

        int yOffset = pressed ? 1 : 0;
        GradientPaint gp = new GradientPaint(0, yOffset, c1, 0, h + yOffset, c2);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, h - 2, arc, arc));

        if (hoverAlpha > 0.01f) {
            g2.setColor(new Color(255, 255, 255, (int)(45 * hoverAlpha)));
            g2.fill(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, (h - 2) / 2.0f, arc, arc));
            g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(), (int)(180 * hoverAlpha)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f + yOffset, w - 3, h - 3, arc, arc));
        } else {
            g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(), 60));
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, h - 2, arc, arc));
        }

        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
        g2.setColor(Color.WHITE);
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }
}

class ModernTextField extends JTextField {
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int arc = 12;

        g2.setColor(new Color(15, 23, 42, 220));
        g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        if (focused) {
            g2.setColor(UITheme.ACCENT_CYAN);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(new Color(255, 255, 255, 30));
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));

        super.paintComponent(g);

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

class GlassPanel extends JPanel {
    private int cornerRadius = 18;
    private boolean highlighted = false;

    public GlassPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public GlassPanel() {
        this(18);
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

class Interactive3DCard extends JPanel {
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
        g2.drawString("SMART ATM BANK", 24, 34);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(UITheme.ACCENT_CYAN);
        g2.drawString("SAVINGS PLATINUM", 25, 48);

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
        g2.drawString("Smart ATM 24/7 Helpline: 1800-SMART-ATM", 80, 153);
        g2.drawString("Authorized Signature - Not Transferable", 80, 167);
        g2.drawString("Smart ATM / Bank Management System Network.", 20, 192);
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

class Animated3DVault extends JPanel {
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

        targetDialAngle += Math.PI * 4.5;

        Timer t1 = new Timer(300, e -> {
            targetBoltProgress = 0.0;
            ((Timer) e.getSource()).stop();
        });
        t1.setRepeats(false);
        t1.start();

        Timer t2 = new Timer(600, e -> {
            targetDoorOpenAngle = Math.PI / 2.5;
            ((Timer) e.getSource()).stop();
        });
        t2.setRepeats(false);
        t2.start();

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

class CoinParticleSystem extends JPanel {
    private static class Coin3D {
        double x, y, z;
        double vx, vy, vz;
        double yaw, pitch;
        double vYaw, vPitch;
        double radius;
        double life;
        double maxLife;

        Coin3D(double x, double y, double vx, double vy) {
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
        }

        void update(int floorY) {
            x += vx;
            y += vy;
            z += vz;

            vy += 0.45;
            vx *= 0.985;
            vz *= 0.985;

            yaw += vYaw;
            pitch += vPitch;

            if (y > floorY) {
                y = floorY;
                vy = -vy * 0.52;
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
                coins.add(new Coin3D(sx, sy, vx, vy));
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
                coins.add(new Coin3D(cx, cy, vx, vy));
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

        double cosY = Math.cos(c.yaw);
        double cosX = Math.cos(c.pitch);
        double rX = Math.abs(cosY) * c.radius;
        double rY = Math.abs(cosX) * c.radius;
        if (rX < 1.0) rX = 1.0;
        if (rY < 1.0) rY = 1.0;

        AffineTransform oldTx = g2.getTransform();
        g2.translate(c.x, c.y);

        double rimThickness = 4.0 * Math.abs(Math.sin(c.yaw));
        if (rimThickness > 1.0) {
            g2.setColor(new Color(180, 83, 9));
            g2.fill(new Ellipse2D.Double(-rX - rimThickness / 2, -rY, (rX * 2) + rimThickness, rY * 2));
        }

        GradientPaint goldGrad = new GradientPaint(
            (float) -rX, (float) -rY, new Color(254, 240, 138),
            (float) rX, (float) rY, new Color(217, 119, 6)
        );
        g2.setPaint(goldGrad);
        g2.fill(new Ellipse2D.Double(-rX, -rY, rX * 2, rY * 2));

        g2.setColor(new Color(251, 191, 36));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new Ellipse2D.Double(-rX, -rY, rX * 2, rY * 2));

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

class IsometricChart3D extends JPanel {
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

        data.add(new BarData("Prev Bal", 5000, true));
        data.add(new BarData("Deposits", 2500, true));
        data.add(new BarData("Withdrawals", 1500, false));
        data.add(new BarData("Net Flow", 6000, true));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
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

        g2.setFont(UITheme.FONT_SUBTITLE);
        g2.setColor(UITheme.TEXT_PRIMARY);
        g2.drawString("3D Cash Flow Analysis", 15, 24);

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_MUTED);
        g2.drawString("Real-time 3D transaction pillars", 15, 40);

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
        int barD = 16;

        for (int i = 0; i < count; i++) {
            BarData d = data.get(i);
            int cx = 25 + i * slotW + (slotW / 2);
            double targetH = (d.amount / maxVal) * (baseY - 70);
            int barH = (int) (targetH * animProgress);

            boolean isHovered = (i == hoveredIndex);
            if (isHovered) barH += 6;

            paint3DPillar(g2, cx, baseY, barW, barH, barD, d.isIncome, isHovered);

            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(isHovered ? Color.WHITE : UITheme.TEXT_SECONDARY);
            FontMetrics fm = g2.getFontMetrics();
            int lx = cx - fm.stringWidth(d.label) / 2;
            g2.drawString(d.label, lx, baseY + 18);

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

        Path2D front = new Path2D.Double();
        front.moveTo(left, baseY);
        front.lineTo(left, topY);
        front.lineTo(right, topY);
        front.lineTo(right, baseY);
        front.closePath();
        g2.setColor(leftColor);
        g2.fill(front);

        Path2D side = new Path2D.Double();
        side.moveTo(right, baseY);
        side.lineTo(right, topY);
        side.lineTo(right + d, topY - d / 2);
        side.lineTo(right + d, baseY - d / 2);
        side.closePath();
        g2.setColor(rightColor);
        g2.fill(side);

        Path2D top = new Path2D.Double();
        top.moveTo(left, topY);
        top.lineTo(left + d, topY - d / 2);
        top.lineTo(right + d, topY - d / 2);
        top.lineTo(right, topY);
        top.closePath();
        g2.setColor(topColor);
        g2.fill(top);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(top);
    }
}

class ModernDialogs {

    public static void showDepositDialog(JFrame parent, Account account, Runnable onDepositSuccess) {
        JDialog dialog = createBaseDialog(parent, "Smart ATM - Cash Deposit", 460, 360);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Deposit Cash Terminal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Current Balance: Rs. " + String.format("%,.2f", account.getBalance()));
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.ACCENT_CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField amountField = new ModernTextField("Enter deposit amount (Rs.)");
        amountField.setMaximumSize(new Dimension(380, 42));

        JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        presets.setOpaque(false);
        int[] presetAmounts = { 500, 1000, 2000, 5000 };
        for (int pVal : presetAmounts) {
            ModernButton pBtn = new ModernButton("+" + pVal, ModernButton.Style.SECONDARY);
            pBtn.setPreferredSize(new Dimension(80, 32));
            pBtn.addActionListener(e -> amountField.setText(String.valueOf(pVal)));
            presets.add(pBtn);
        }

        ModernButton confirmBtn = new ModernButton("Confirm Deposit", ModernButton.Style.SUCCESS);
        confirmBtn.setMaximumSize(new Dimension(380, 44));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmBtn.addActionListener(e -> {
            try {
                String txt = amountField.getText().trim();
                if (txt.isEmpty()) {
                    showError(dialog, "Please enter a valid amount.");
                    return;
                }
                double amt = Double.parseDouble(txt);
                account.deposit(amt);
                dialog.dispose();
                if (onDepositSuccess != null) onDepositSuccess.run();
                showSuccess(parent, "Deposit Successful", "Deposit successful! New Balance: Rs. " + account.getBalance());
            } catch (NumberFormatException ex) {
                showError(dialog, "Please enter a valid numeric amount.");
            } catch (IllegalArgumentException ex) {
                showError(dialog, ex.getMessage());
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(presets);
        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showWithdrawDialog(JFrame parent, Account account, Runnable onWithdrawSuccess) {
        JDialog dialog = createBaseDialog(parent, "Smart ATM - Cash Withdrawal", 460, 370);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Withdraw Cash Terminal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Available Funds: Rs. " + String.format("%,.2f", account.getBalance()));
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.ACCENT_GOLD);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField amountField = new ModernTextField("Enter withdrawal amount (Rs.)");
        amountField.setMaximumSize(new Dimension(380, 42));

        JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        presets.setOpaque(false);
        int[] presetAmounts = { 500, 1000, 2000, 5000 };
        for (int pVal : presetAmounts) {
            ModernButton pBtn = new ModernButton("Rs." + pVal, ModernButton.Style.SECONDARY);
            pBtn.setPreferredSize(new Dimension(80, 32));
            pBtn.addActionListener(e -> amountField.setText(String.valueOf(pVal)));
            presets.add(pBtn);
        }

        ModernButton confirmBtn = new ModernButton("Collect Cash", ModernButton.Style.GOLD);
        confirmBtn.setMaximumSize(new Dimension(380, 44));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmBtn.addActionListener(e -> {
            try {
                String txt = amountField.getText().trim();
                if (txt.isEmpty()) {
                    showError(dialog, "Please enter a valid amount.");
                    return;
                }
                double amt = Double.parseDouble(txt);
                account.withdraw(amt);
                dialog.dispose();
                if (onWithdrawSuccess != null) onWithdrawSuccess.run();
                showSuccess(parent, "Cash Dispensed", "Please collect your cash. Remaining Balance: Rs. " + account.getBalance());
            } catch (NumberFormatException ex) {
                showError(dialog, "Please enter a valid numeric amount.");
            } catch (Exception ex) {
                showError(dialog, ex.getMessage());
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(presets);
        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showStatementDialog(JFrame parent, Account account) {
        JDialog dialog = createBaseDialog(parent, "Smart ATM - Transaction Statement", 680, 460);

        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel top = new JPanel(new GridLayout(2, 1, 4, 4));
        top.setOpaque(false);
        JLabel title = new JLabel("Transaction Statement - " + account.getAccountNumber());
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Account Holder: " + account.getHolderName() + " | Balance: Rs. " + String.format("%,.2f", account.getBalance()));
        sub.setFont(UITheme.FONT_REGULAR);
        sub.setForeground(UITheme.ACCENT_CYAN);
        top.add(title);
        top.add(sub);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = { "Type", "Amount", "Date & Time" };
        List<Transaction> list = account.getTransactionHistory();
        Object[][] rowData = new Object[list.size()][3];
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            rowData[i][0] = t.getType();
            rowData[i][1] = t.getFormattedAmount();
            rowData[i][2] = t.getFormattedDate();
        }

        DefaultTableModel model = new DefaultTableModel(rowData, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setOpaque(false);
        table.setBackground(new Color(15, 23, 42));
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_REGULAR);
        table.setRowHeight(32);
        table.setGridColor(new Color(255, 255, 255, 15));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFoc, r, c);
                l.setOpaque(false);
                String str = val != null ? val.toString() : "";
                if (str.startsWith("+")) {
                    l.setForeground(UITheme.ACCENT_GREEN);
                } else if (str.startsWith("-")) {
                    l.setForeground(UITheme.ACCENT_RED);
                } else {
                    l.setForeground(UITheme.TEXT_PRIMARY);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);

        ModernButton printBtn = new ModernButton("Print to IDE Console", ModernButton.Style.SECONDARY);
        printBtn.setPreferredSize(new Dimension(180, 38));
        printBtn.addActionListener(e -> {
            account.printTransactionHistory();
            JOptionPane.showMessageDialog(dialog, "Transaction statement printed to your IDE console!", "Statement", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton closeBtn = new ModernButton("Close Statement", ModernButton.Style.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(150, 38));
        closeBtn.addActionListener(e -> dialog.dispose());

        bottom.add(printBtn);
        bottom.add(closeBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void showSuccess(Component parent, String title, String msg) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Transaction Error", JOptionPane.ERROR_MESSAGE);
    }

    private static JDialog createBaseDialog(JFrame parent, String title, int w, int h) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        return dialog;
    }
}

class LoginView extends JPanel {
    private final BankSystem bank;
    private final Consumer<Account> onLoginSuccess;

    private Animated3DVault vault;
    private ModernTextField userText;
    private JPasswordField pinText;
    private ModernButton loginButton;
    private ModernButton demoButton;
    private JLabel statusLabel;

    public LoginView(BankSystem bank, Consumer<Account> onLoginSuccess) {
        this.bank = bank;
        this.onLoginSuccess = onLoginSuccess;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);

        vault = new Animated3DVault();
        vault.setPreferredSize(new Dimension(380, 380));

        GlassPanel loginCard = new GlassPanel(24);
        loginCard.setPreferredSize(new Dimension(420, 480));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel brandLabel = new JLabel("Smart ATM");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brandLabel.setForeground(UITheme.TEXT_PRIMARY);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Bank Management System");
        subLabel.setFont(UITheme.FONT_SUBTITLE);
        subLabel.setForeground(UITheme.ACCENT_CYAN);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLbl = new JLabel("Account Number:");
        userLbl.setFont(UITheme.FONT_BOLD);
        userLbl.setForeground(UITheme.TEXT_SECONDARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        userText = new ModernTextField("e.g. 101");
        userText.setText("101");
        userText.setMaximumSize(new Dimension(350, 42));

        JLabel pinLbl = new JLabel("4-Digit PIN:");
        pinLbl.setFont(UITheme.FONT_BOLD);
        pinLbl.setForeground(UITheme.TEXT_SECONDARY);
        pinLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        pinText = new JPasswordField("1234");
        pinText.setFont(UITheme.FONT_MONO);
        pinText.setOpaque(false);
        pinText.setForeground(UITheme.TEXT_PRIMARY);
        pinText.setCaretColor(UITheme.ACCENT_CYAN);
        pinText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        pinText.setMaximumSize(new Dimension(350, 42));

        loginButton = new ModernButton("Login", ModernButton.Style.PRIMARY);
        loginButton.setMaximumSize(new Dimension(350, 46));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        demoButton = new ModernButton("1-Click Demo Login (Krunal #101)", ModernButton.Style.GOLD);
        demoButton.setMaximumSize(new Dimension(350, 38));
        demoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.ACCENT_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> attemptLogin());
        demoButton.addActionListener(e -> {
            userText.setText("101");
            pinText.setText("1234");
            attemptLogin();
        });

        loginCard.add(brandLabel);
        loginCard.add(Box.createVerticalStrut(4));
        loginCard.add(subLabel);
        loginCard.add(Box.createVerticalStrut(28));
        loginCard.add(userLbl);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(userText);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(pinLbl);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(pinText);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(statusLabel);
        loginCard.add(Box.createVerticalStrut(14));
        loginCard.add(loginButton);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(demoButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        centerContainer.add(vault, gbc);
        centerContainer.add(loginCard, gbc);

        add(centerContainer, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String accNum = userText.getText().trim();
        String pin = new String(pinText.getPassword()).trim();

        try {
            Account acc = bank.login(accNum, pin);
            statusLabel.setForeground(UITheme.ACCENT_GREEN);
            statusLabel.setText("Login Verified. Accessing 3D Terminal...");

            loginButton.setEnabled(false);
            demoButton.setEnabled(false);

            vault.triggerUnlockSequence(() -> {
                onLoginSuccess.accept(acc);
                loginButton.setEnabled(true);
                demoButton.setEnabled(true);
                statusLabel.setText(" ");
            });

        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.ACCENT_RED);
            statusLabel.setText(ex.getMessage());
        }
    }
}

class DashboardView extends JLayeredPane {
    private final JFrame parentFrame;
    private Account account;
    private final Runnable onLogout;

    private JPanel contentPanel;
    private Interactive3DCard card3D;
    private CoinParticleSystem coinParticles;
    private IsometricChart3D chart3D;
    private JLabel balanceLabel;
    private JTable ledgerTable;
    private DefaultTableModel tableModel;
    private JLabel clockLabel;

    public DashboardView(JFrame parentFrame, Account account, Runnable onLogout) {
        this.parentFrame = parentFrame;
        this.account = account;
        this.onLogout = onLogout;

        setOpaque(false);
        initUI();
    }

    public void updateAccount(Account acc) {
        this.account = acc;
        if (card3D != null) card3D.setAccount(acc);
        refreshBalanceDisplay();
        refreshLedgerTable();
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();
        if (contentPanel != null) contentPanel.setBounds(0, 0, w, h);
        if (coinParticles != null) coinParticles.setBounds(0, 0, w, h);
    }

    private void initUI() {
        contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        contentPanel.add(createHeaderBar(), BorderLayout.NORTH);

        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);
        centerGrid.add(createCardSection());
        centerGrid.add(createActionsSection());
        contentPanel.add(centerGrid, BorderLayout.CENTER);

        JPanel bottomGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomGrid.setOpaque(false);
        bottomGrid.setPreferredSize(new Dimension(1100, 240));
        bottomGrid.add(createLedgerSection());
        bottomGrid.add(createChartSection());
        contentPanel.add(bottomGrid, BorderLayout.SOUTH);

        coinParticles = new CoinParticleSystem();
        coinParticles.setOpaque(false);

        add(contentPanel, Integer.valueOf(100));
        add(coinParticles, Integer.valueOf(200));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                doLayout();
            }
        });
    }

    private JPanel createHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel logo = new JLabel("Smart ATM / Bank Management System");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(UITheme.TEXT_PRIMARY);

        JLabel userGreeting = new JLabel(" | Welcome, " + (account != null ? account.getHolderName() : "User") + "!");
        userGreeting.setFont(UITheme.FONT_SUBTITLE);
        userGreeting.setForeground(UITheme.ACCENT_CYAN);

        brandPanel.add(logo);
        brandPanel.add(userGreeting);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(UITheme.FONT_MONO);
        clockLabel.setForeground(UITheme.TEXT_SECONDARY);
        Timer clockTimer = new Timer(1000, e -> {
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " IST");
        });
        clockTimer.start();
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " IST");

        ModernButton logoutBtn = new ModernButton("Logout", ModernButton.Style.DANGER);
        logoutBtn.setPreferredSize(new Dimension(110, 36));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        rightPanel.add(clockLabel);
        rightPanel.add(logoutBtn);

        bar.add(brandPanel, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel createCardSection() {
        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        card3D = new Interactive3DCard(account);
        panel.add(card3D, BorderLayout.CENTER);

        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        ctrlPanel.setOpaque(false);

        ModernButton flipBtn = new ModernButton("3D Flip Card", ModernButton.Style.SECONDARY);
        flipBtn.setPreferredSize(new Dimension(200, 36));
        flipBtn.addActionListener(e -> card3D.toggleFlip());

        JLabel tip = new JLabel("Move mouse over card to tilt in 3D");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_MUTED);

        ctrlPanel.add(flipBtn);
        ctrlPanel.add(tip);

        panel.add(ctrlPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createActionsSection() {
        GlassPanel panel = new GlassPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel balBox = new JPanel(new GridLayout(3, 1, 2, 2));
        balBox.setOpaque(false);

        JLabel balTitle = new JLabel("AVAILABLE TOTAL BALANCE");
        balTitle.setFont(UITheme.FONT_SMALL);
        balTitle.setForeground(UITheme.TEXT_SECONDARY);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        balanceLabel.setForeground(UITheme.TEXT_PRIMARY);
        refreshBalanceDisplay();

        JLabel tierLbl = new JLabel("Account #101 • Savings Account");
        tierLbl.setFont(UITheme.FONT_SMALL);
        tierLbl.setForeground(UITheme.ACCENT_GREEN);

        balBox.add(balTitle);
        balBox.add(balanceLabel);
        balBox.add(tierLbl);
        panel.add(balBox, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        btnGrid.setOpaque(false);

        ModernButton depositBtn = new ModernButton("Deposit Cash", ModernButton.Style.SUCCESS);
        depositBtn.addActionListener(e -> {
            ModernDialogs.showDepositDialog(parentFrame, account, () -> {
                coinParticles.burstDeposit(35);
                if (card3D != null) card3D.regenerateCardCaches();
                refreshBalanceDisplay();
                refreshLedgerTable();
            });
        });

        ModernButton withdrawBtn = new ModernButton("Withdraw Cash", ModernButton.Style.GOLD);
        withdrawBtn.addActionListener(e -> {
            ModernDialogs.showWithdrawDialog(parentFrame, account, () -> {
                coinParticles.burstWithdraw(35);
                if (card3D != null) card3D.regenerateCardCaches();
                refreshBalanceDisplay();
                refreshLedgerTable();
            });
        });

        ModernButton balanceBtn = new ModernButton("Check Balance", ModernButton.Style.SECONDARY);
        balanceBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(parentFrame,
                "Current Balance: Rs. " + account.getBalance(),
                "Balance", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton statementBtn = new ModernButton("View Statement", ModernButton.Style.PRIMARY);
        statementBtn.addActionListener(e -> {
            ModernDialogs.showStatementDialog(parentFrame, account);
        });

        btnGrid.add(depositBtn);
        btnGrid.add(withdrawBtn);
        btnGrid.add(balanceBtn);
        btnGrid.add(statementBtn);

        panel.add(btnGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLedgerSection() {
        GlassPanel panel = new GlassPanel(18);
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Real-time Transaction Feed");
        title.setFont(UITheme.FONT_BOLD);
        title.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = { "Type", "Amount", "Time" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ledgerTable = new JTable(tableModel);
        ledgerTable.setOpaque(false);
        ledgerTable.setBackground(new Color(15, 23, 42));
        ledgerTable.setForeground(UITheme.TEXT_PRIMARY);
        ledgerTable.setRowHeight(28);
        ledgerTable.setGridColor(new Color(255, 255, 255, 12));
        ledgerTable.getTableHeader().setBackground(new Color(25, 35, 55));
        ledgerTable.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);

        ledgerTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFoc, r, c);
                l.setOpaque(false);
                if (val != null && val.toString().startsWith("+")) {
                    l.setForeground(UITheme.ACCENT_GREEN);
                } else {
                    l.setForeground(UITheme.ACCENT_RED);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(ledgerTable);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        refreshLedgerTable();
        return panel;
    }

    private JPanel createChartSection() {
        GlassPanel panel = new GlassPanel(18);
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        chart3D = new IsometricChart3D();
        panel.add(chart3D, BorderLayout.CENTER);
        return panel;
    }

    public void refreshBalanceDisplay() {
        if (balanceLabel != null && account != null) {
            balanceLabel.setText("Rs. " + String.format("%,.2f", account.getBalance()));
        }
    }

    public void refreshLedgerTable() {
        if (tableModel == null || account == null) return;
        tableModel.setRowCount(0);
        List<Transaction> list = account.getTransactionHistory();
        double depSum = 0;
        double witSum = 0;

        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getType(),
                t.getFormattedAmount(),
                t.getFormattedDate()
            });

            if (t.getType().equalsIgnoreCase("Deposit") || t.getType().equalsIgnoreCase("Account Created")) {
                depSum += t.getAmount();
            } else {
                witSum += t.getAmount();
            }
        }

        if (chart3D != null) {
            chart3D.updateData(depSum, witSum);
        }
    }
}
