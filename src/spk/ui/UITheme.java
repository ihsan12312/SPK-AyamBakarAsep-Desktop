package spk.ui;

import java.awt.*;

/**
 * UITheme.java – Premium Design System
 * Modern dark + vibrant accent color palette
 */
public final class UITheme {

    // ── Slate Sidebar Palette ────────────────────────────────────
    public static final Color SLATE_950 = new Color(0x020617);
    public static final Color SLATE_900 = new Color(0x0F172A);
    public static final Color SLATE_800 = new Color(0x1E293B);
    public static final Color SLATE_700 = new Color(0x334155);
    public static final Color SLATE_600 = new Color(0x475569);
    public static final Color SLATE_500 = new Color(0x64748B);
    
    // Compatibility aliases for sidebar
    public static final Color DARK_900 = SLATE_950;
    public static final Color DARK_800 = SLATE_900;
    public static final Color DARK_700 = SLATE_800;
    public static final Color DARK_600 = SLATE_500;

    // ── Page / Card Backgrounds ──────────────────────────────────
    public static final Color BG_PAGE  = new Color(0xF0F4FF);
    public static final Color BG_CARD  = Color.WHITE;

    // ── Indigo / Primary Brand ───────────────────────────────────
    public static final Color INDIGO_900 = new Color(0x312E81);
    public static final Color INDIGO_800 = new Color(0x3730A3);
    public static final Color INDIGO_700 = new Color(0x4338CA);
    public static final Color INDIGO_600 = new Color(0x4F46E5);
    public static final Color INDIGO_500 = new Color(0x6366F1);
    public static final Color INDIGO_100 = new Color(0xE0E7FF);
    public static final Color INDIGO_50  = new Color(0xEEF2FF);

    public static final Color PRIMARY_800 = INDIGO_800;
    public static final Color PRIMARY_700 = INDIGO_700;
    public static final Color PRIMARY_600 = INDIGO_600;
    public static final Color PRIMARY_500 = INDIGO_500;
    public static final Color PRIMARY_100 = INDIGO_100;

    // ── Rose / Danger Palette ────────────────────────────────────
    public static final Color ROSE_700 = new Color(0xBE123C);
    public static final Color ROSE_600 = new Color(0xE11D48);
    public static final Color ROSE_500 = new Color(0xF43F5E);
    public static final Color ROSE_100 = new Color(0xFFE4E6);

    // Compatibility aliases (replacing Red with Indigo/Rose)
    public static final Color RED_800  = ROSE_700; // Danger should stay reddish
    public static final Color RED_700  = ROSE_700;
    public static final Color RED_600  = ROSE_600;
    public static final Color RED_500  = ROSE_500;
    public static final Color RED_100  = ROSE_100;

    // ── Accent Colors ────────────────────────────────────────────
    public static final Color GOLD     = new Color(0xF59E0B);
    public static final Color GOLD_LIGHT = new Color(0xFDE68A);

    public static final Color GREEN_800 = new Color(0x065F46);
    public static final Color GREEN_700 = new Color(0x047857);
    public static final Color GREEN_600 = new Color(0x059669);
    public static final Color GREEN_100 = new Color(0xD1FAE5);

    public static final Color BLUE_800  = new Color(0x1E3A8A);
    public static final Color BLUE_700  = new Color(0x1D4ED8);
    public static final Color BLUE_600  = new Color(0x2563EB);
    public static final Color BLUE_100  = new Color(0xDBEAFE);

    public static final Color PURPLE_800 = new Color(0x4C1D95);
    public static final Color PURPLE_700 = new Color(0x6D28D9);
    public static final Color PURPLE_600 = new Color(0x7C3AED);
    public static final Color PURPLE_100 = new Color(0xEDE9FE);

    public static final Color ORANGE_700 = new Color(0xC2410C);
    public static final Color ORANGE_600 = new Color(0xEA580C);
    public static final Color ORANGE_100 = new Color(0xFFEDD5);

    // ── Text ─────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(0x0F172A);
    public static final Color TEXT_SECONDARY = new Color(0x475569);
    public static final Color TEXT_MUTED     = new Color(0x94A3B8);
    public static final Color BORDER         = new Color(0xE2E8F0);

    // ── Font ─────────────────────────────────────────────────────
    public static Font font(int style, float size) {
        return new Font("Segoe UI", style, (int) size);
    }
    public static Font fontBold(float size)        { return font(Font.BOLD,  size); }
    public static Font fontPlain(float size)       { return font(Font.PLAIN, size); }
    public static Font fontItalic(float size)      { return font(Font.ITALIC, size); }

    // ── Paint Utilities ──────────────────────────────────────────

    public static void polish(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    /** Soft multi-layer drop shadow */
    public static void drawShadow(Graphics2D g2, int x, int y, int w, int h, int r) {
        // Softer, more elegant multi-layered shadow
        Color[] shadowColors = {
            new Color(0, 0, 0, 10),
            new Color(0, 0, 0, 15),
            new Color(0, 0, 0, 20)
        };
        int[] offsets = {4, 2, 1};
        int[] blurs   = {3, 2, 1};
        
        for (int i = 0; i < shadowColors.length; i++) {
            g2.setColor(shadowColors[i]);
            g2.fillRoundRect(x - blurs[i], y + offsets[i], w + blurs[i]*2, h + blurs[i], r+2, r+2);
        }
    }

    /** Horizontal gradient fill */
    public static void fillGradientH(Graphics2D g2, int x, int y, int w, int h, Color c1, Color c2) {
        g2.setPaint(new GradientPaint(x, y, c1, x + w, y, c2));
        g2.fillRect(x, y, w, h);
    }

    /** Vertical gradient fill */
    public static void fillGradientV(Graphics2D g2, int x, int y, int w, int h, Color c1, Color c2) {
        g2.setPaint(new GradientPaint(x, y, c1, x, y + h, c2));
        g2.fillRect(x, y, w, h);
    }

    /** Draw decorative dot pattern */
    public static void drawDotPattern(Graphics2D g2, int w, int h, Color dotColor) {
        g2.setColor(dotColor);
        for (int x = 16; x < w; x += 24)
            for (int y = 16; y < h; y += 24)
                g2.fillOval(x, y, 2, 2);
    }

    /** Draw a glowing circle accent */
    public static void drawGlow(Graphics2D g2, int cx, int cy, int r, Color color) {
        for (int i = r; i >= r/2; i -= 4) {
            int alpha = (int)(25.0 * (r - i) / (r / 2.0));
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 40)));
            g2.fillOval(cx - i, cy - i, i*2, i*2);
        }
    }

    private UITheme() {}
}
