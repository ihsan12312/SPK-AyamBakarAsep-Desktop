package spk.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import spk.dao.AdminDAO;
import spk.model.Admin;

import static spk.ui.UITheme.*;

/**
 * LoginFrame – Premium split-panel login window
 */
public class LoginFrame extends JFrame {

    private JTextField     tfUser;
    private JPasswordField tfPass;
    private JButton        btnLogin;

    public LoginFrame() {
        setTitle("SPK Ayam Bakar Asep – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 620, 24, 24));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                // Outer dark bg
                g2.setColor(DARK_900);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        root.setOpaque(false);

        root.add(buildLeftPanel(),   BorderLayout.WEST);
        root.add(buildRightPanel(),  BorderLayout.CENTER);

        setContentPane(root);

        // Drag to move (undecorated window)
        addDragSupport(root);
    }

    // ══ LEFT PANEL ══════════════════════════════════════════════
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; polish(g2);

                // Deep Indigo gradient bg
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_800, 0, getHeight(), SLATE_900);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth() + 30, getHeight(), 24, 24);

                // Decorative circles
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth()-100, getHeight()-120, 220, 220);

                // Horizontal line divider
                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(40, getHeight()-160, getWidth()-40, getHeight()-160);
            }
        };
        p.setPreferredSize(new Dimension(380, 620));
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(48, 40, 40, 40));

        // Logo dari file resources/logo.png (dipotong bulat)
        JLabel logoLbl = new JLabel() {
            final Image logoImg = loadLogo(100);
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                int sz = 100;
                if (logoImg != null) {
                    // Clip lingkaran
                    Shape oldClip = g2.getClip();
                    g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, sz, sz));
                    g2.drawImage(logoImg, 0, 0, sz, sz, this);
                    g2.setClip(oldClip);
                    // Ring border putih
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new java.awt.BasicStroke(2.5f));
                    g2.drawOval(1, 1, sz - 2, sz - 2);
                } else {
                    // Fallback gradient indigo
                    GradientPaint gp = new GradientPaint(0, 0, INDIGO_600, sz, sz, INDIGO_800);
                    g2.setPaint(gp); g2.fillOval(0, 0, sz, sz);
                    g2.setColor(new Color(255,255,255,60));
                    g2.setStroke(new java.awt.BasicStroke(2));
                    g2.drawOval(1, 1, sz-2, sz-2);
                    // Inisial teks
                    g2.setFont(fontBold(28)); g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = "AB";
                    g2.drawString(txt, (sz-fm.stringWidth(txt))/2, sz/2+fm.getAscent()/2-4);
                }
            }
            @Override public Dimension getPreferredSize() { return new Dimension(100, 100); }
            @Override public Dimension getMaximumSize()   { return new Dimension(100, 100); }
        };
        logoLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle = makeLabel("Ayam Bakar Asep", fontBold(22), Color.WHITE);
        JLabel lblSub   = makeLabel("Sistem Pendukung Keputusan", fontPlain(13), new Color(255,255,255,160));
        JLabel lblBadge = makeBadge("Metode SAW");

        p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(logoLbl);
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(lblTitle);
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(lblSub);
        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lblBadge);
        p.add(Box.createVerticalGlue());

        // Footer tagline
        JLabel tagline = makeLabel("\"Bantu Anda Memilih Menu Terbaik\"", fontItalic(12), new Color(255,255,255,100));
        p.add(tagline);

        return p;
    }

    // ══ RIGHT PANEL ═════════════════════════════════════════════
    private JPanel buildRightPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                g2.setColor(new Color(0xF8FAFC));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative dot pattern
                g2.setColor(new Color(0,0,0,8));
                for (int x = 20; x < getWidth(); x += 28)
                    for (int y = 20; y < getHeight(); y += 28)
                        g2.fillOval(x, y, 3, 3);
            }
        };
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());

        JPanel card = buildFormCard();
        p.add(card);
        return p;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                drawShadow(g2, 4, 8, getWidth()-8, getHeight()-8, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(400, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(44, 48, 44, 48));

        // Header
        JLabel welcome = makeLabel("Selamat Datang", fontBold(22), TEXT_PRIMARY);
        JLabel sub     = makeLabel("Masuk ke Sistem SPK Ayam Bakar", fontPlain(13), TEXT_SECONDARY);

        // Username
        JLabel lblUser = makeLabel("Username", fontBold(12), TEXT_PRIMARY);
        tfUser = new StyledTextField("admin");

        // Password
        JLabel lblPass = makeLabel("Password", fontBold(12), TEXT_PRIMARY);
        tfPass = new StyledPasswordField("admin");

        // Login button
        btnLogin = new GradientButton("  Masuk ke Sistem  →");
        btnLogin.addActionListener(e -> doLogin());
        tfPass.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode()==KeyEvent.VK_ENTER) doLogin(); }
        });

        // Hint
        JLabel hint = makeLabel("admin / admin", fontItalic(11), TEXT_MUTED);

        // Registration Link
        JLabel lblReg = makeLabel("Belum punya akun? Daftar disini", fontBold(12), INDIGO_600);
        lblReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblReg.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { goToRegister(); }
        });

        // Lupa Password link
        JLabel lblLupa = makeLabel("Lupa Password?", fontBold(12), new Color(0x6366F1));
        lblLupa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLupa.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { goToResetPassword(); }
        });

        card.add(welcome);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(sub);
        card.add(Box.createRigidArea(new Dimension(0, 32)));
        card.add(lblUser);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfUser);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfPass);
        card.add(Box.createRigidArea(new Dimension(0, 28)));
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblLupa);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblReg);

        return card;
    }

    // ══ ACTIONS ══════════════════════════════════════════════════
    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(tfPass.getPassword());
        
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.login(u, p);
            if (admin != null) {
                // Login sukses
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
            } else {
                tfPass.setText("");
                tfUser.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ROSE_500, 1, true),
                    new EmptyBorder(8, 12, 8, 12)));
                JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goToRegister() {
        dispose();
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }

    private void goToResetPassword() {
        dispose();
        SwingUtilities.invokeLater(() -> new ResetPasswordFrame().setVisible(true));
    }

    // ══ INNER COMPONENTS ═════════════════════════════════════════
    private static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font); l.setForeground(color); l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel makeBadge(String text) {
        JLabel l = new JLabel("  " + text + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                g2.setColor(new Color(255,255,255,25));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2.setColor(new Color(255,255,255,60));
                g2.setStroke(new BasicStroke(1)); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                super.paintComponent(g);
            }
        };
        l.setFont(fontBold(11)); l.setForeground(GOLD_LIGHT); l.setOpaque(false);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    /** Styled text field with bottom+side border */
    static class StyledTextField extends JTextField {
        StyledTextField(String text) {
            super(text);
            setFont(fontPlain(14)); setOpaque(true); setBackground(new Color(0xF8FAFC));
            setForeground(TEXT_PRIMARY); setCaretColor(RED_600);
            setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(INDIGO_600, 2, true), new EmptyBorder(7, 11, 7, 11))); }
                @Override public void focusLost(FocusEvent e)   { setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12))); }
            });
        }
    }

    static class StyledPasswordField extends JPasswordField {
        StyledPasswordField(String text) {
            super(text);
            setFont(fontPlain(14)); setOpaque(true); setBackground(new Color(0xF8FAFC));
            setForeground(TEXT_PRIMARY); setCaretColor(RED_600);
            setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(INDIGO_600, 2, true), new EmptyBorder(7, 11, 7, 11))); }
                @Override public void focusLost(FocusEvent e)   { setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12))); }
            });
        }
    }

    /** Gradient button */
    static class GradientButton extends JButton {
        private boolean hovered = false;
        GradientButton(String text) {
            super(text);
            setFont(fontBold(14)); setForeground(Color.WHITE);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Short.MAX_VALUE, 48)); setAlignmentX(LEFT_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; polish(g2);
            Color c1 = hovered ? INDIGO_700 : INDIGO_600;
            Color c2 = hovered ? INDIGO_800 : INDIGO_700;
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
            g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            g2.setFont(getFont()); g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
        }
    }

    private static int dragX, dragY;
    private void addDragSupport(JPanel root) {
        root.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragX = e.getX(); dragY = e.getY(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - dragX, getY() + e.getY() - dragY);
            }
        });
    }

    /**
     * Logo loader — pakai ImageIO.read() agar langsung tersedia (tidak lazy).
     * Dicoba dari file absolut dulu, lalu classloader.
     */
    static Image loadLogo(int size) {
        java.awt.image.BufferedImage src = null;

        // 1. Coba file absolut / relatif langsung
        String[] paths = {
            "src/resources/logo.png",
            "build/classes/resources/logo.png"
        };
        for (String p : paths) {
            try {
                java.io.File f = new java.io.File(p);
                if (f.exists() && f.length() > 0) {
                    src = javax.imageio.ImageIO.read(f);
                    if (src != null) break;
                }
            } catch (Exception ignored) {}
        }

        // 2. Coba classloader (untuk JAR)
        if (src == null) {
            try {
                java.net.URL url = LoginFrame.class.getClassLoader().getResource("resources/logo.png");
                if (url == null) url = LoginFrame.class.getResource("/resources/logo.png");
                if (url != null) src = javax.imageio.ImageIO.read(url);
            } catch (Exception ignored) {}
        }

        if (src == null) return null;

        // Scale menggunakan Graphics2D berkualitas tinggi — hasilnya langsung siap
        java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2s = scaled.createGraphics();
        g2s.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2s.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2s.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g2s.drawImage(src, 0, 0, size, size, null);
        g2s.dispose();
        return scaled;
    }
}
