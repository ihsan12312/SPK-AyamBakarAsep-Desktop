package spk.ui;

import java.awt.*;

/**
 * UITheme.java – Premium Design System (Maroon Edition)
 * Modern dark + vibrant Maroon accent color palette
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
    public static final Color BG_PAGE  = new Color(0xFFF5F5); // Nuansa merah sangat muda
    public static final Color BG_CARD  = Color.WHITE;

    // ── Maroon / Primary Brand (#610000) ──────────────────────────
    // Mengganti semua Indigo menjadi variasi Maroon
    public static final Color MAROON_900 = new Color(0x3D0000); 
    public static final Color MAROON_800 = new Color(0x4D0000); 
    public static final Color MAROON_700 = new Color(0x610000); // Warna utama Anda
    public static final Color MAROON_600 = new Color(0x800000); 
    public static final Color MAROON_500 = new Color(0xA52A2A); 
    public static final Color MAROON_100 = new Color(0xFFEAEA); 
    public static final Color MAROON_50  = new Color(0xFFF5F5); 

    // Aliases agar komponen yang memanggil INDIGO/PRIMARY otomatis berubah
    public static final Color INDIGO_900 = MAROON_900;
    public static final Color INDIGO_800 = MAROON_800;
    public static final Color INDIGO_700 = MAROON_700;
    public static final Color INDIGO_600 = MAROON_600;
    public static final Color INDIGO_500 = MAROON_500;
    public static final Color INDIGO_100 = MAROON_100;
    public static final Color INDIGO_50  = MAROON_50;

    public static final Color PRIMARY_800 = MAROON_800;
    public static final Color PRIMARY_700 = MAROON_700;
    public static final Color PRIMARY_600 = MAROON_600;
    public static final Color PRIMARY_500 = MAROON_500;
    public static final Color PRIMARY_100 = MAROON_100;

    // ── Rose / Danger Palette ────────────────────────────────────
    public static final Color ROSE_700 = new Color(0xBE123C);
    public static final Color ROSE_600 = new Color(0xE11D48);
    public static final Color ROSE_500 = new Color(0xF43F5E);
    public static final Color ROSE_100 = new Color(0xFFE4E6);

    // Compatibility aliases
    public static final Color RED_800  = ROSE_700; 
    public static final Color RED_700  = ROSE_700;
    public static final Color RED_600  = ROSE_600;
    public static final Color RED_500  = ROSE_500;
    public static final Color RED_100  = ROSE_100;

    // ── Accent Colors (Restored for Dashboard compatibility) ─────
    public static final Color GOLD       = new Color(0xF59E0B);
    public static final Color GOLD_LIGHT = new Color(0xFDE68A);

    public static final Color GREEN_800 = new Color(0x065F46);
    public static final Color GREEN_700 = new Color(0x047857);
    public static final Color GREEN_600 = new Color(0x059669);
    public static final Color GREEN_100 = new Color(0xD1FAE5);

    public static final Color BLUE_800  = MAROON_800; // Diarahkan ke Maroon agar senada
    public static final Color BLUE_700  = MAROON_700;
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
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,          RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    /** Soft multi-layer drop shadow */
    public static void drawShadow(Graphics2D g2, int x, int y, int w, int h, int r) {
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