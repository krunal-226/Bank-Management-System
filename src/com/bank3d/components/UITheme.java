package com.bank3d.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UITheme {
    // Cyber & Fintech Color Palette
    public static final Color BG_DARK = new Color(10, 14, 23);
    public static final Color BG_DARKER = new Color(7, 10, 16);
    public static final Color SURFACE_GLASS = new Color(18, 25, 40, 210);
    public static final Color SURFACE_ELEVATED = new Color(26, 36, 56, 230);
    public static final Color SURFACE_BORDER = new Color(255, 255, 255, 24);
    public static final Color SURFACE_BORDER_GLOW = new Color(0, 235, 255, 90);

    public static final Color ACCENT_CYAN = new Color(0, 235, 255);
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    public static final Color ACCENT_GOLD = new Color(245, 158, 11);
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);
    public static final Color ACCENT_RED = new Color(239, 68, 68);

    public static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    public static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    // Fonts
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
        // Soft drop shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 4, w - 4, h - 2, arc, arc));

        // Glass background gradient
        GradientPaint gp = new GradientPaint(
            x, y, highlight ? new Color(26, 36, 56, 220) : SURFACE_GLASS,
            x, y + h, highlight ? new Color(17, 24, 39, 230) : new Color(12, 17, 28, 220)
        );
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, arc, arc));

        // Border highlight
        g2.setColor(highlight ? SURFACE_BORDER_GLOW : SURFACE_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, arc, arc));
    }
}
