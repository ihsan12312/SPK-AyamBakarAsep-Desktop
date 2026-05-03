package spk.ui;

import spk.dao.AdminDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import static spk.ui.UITheme.*;

/**
 * ResetPasswordFrame – Premium full-screen reset password window (same design as Login)
 */
public class ResetPasswordFrame extends JFrame {

    private JTextField     tfUser;
    private JPasswordField tfPass;
    private JPasswordField tfPassConfirm;
    private JButton        btnReset;

    public ResetPasswordFrame() {
        setTitle("SPK Ayam Bakar Asep – Reset Password");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 620, 24, 24));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                g2.setColor(DARK_900);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        root.setOpaque(false);

        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);

        setContentPane(root);
        addDragSupport(root);
    }

    // ══ LEFT PANEL ══════════════════════════════════════════════
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_800, 0, getHeight(), SLATE_900);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth() + 30, getHeight(), 24, 24);
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth()-100, getHeight()-120, 220, 220);
                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(40, getHeight()-160, getWidth()-40, getHeight()-160);
            }
        };
        p.setPreferredSize(new Dimension(380, 620));
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(48, 40, 40, 40));

        JLabel logoLbl = new JLabel() {
            final Image logoImg = LoginFrame.loadLogo(100);
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                int sz = 100;
                if (logoImg != null) {
                    Shape oldClip = g2.getClip();
                    g2.setClip(new Ellipse2D.Double(0, 0, sz, sz));
                    g2.drawImage(logoImg, 0, 0, sz, sz, this);
                    g2.setClip(oldClip);
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(1, 1, sz - 2, sz - 2);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, INDIGO_600, sz, sz, INDIGO_800);
                    g2.setPaint(gp); g2.fillOval(0, 0, sz, sz);
                    g2.setColor(new Color(255,255,255,60));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(1, 1, sz-2, sz-2);
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
        JLabel lblSub   = makeLabel("Lupa Password Admin", fontPlain(13), new Color(255,255,255,160));
        JLabel lblBadge = makeBadge("Keamanan Akun");

        p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(logoLbl);
        p.add(Box.createRigidArea(new Dimension(0, 20)));
        p.add(lblTitle);
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(lblSub);
        p.add(Box.createRigidArea(new Dimension(0, 12)));
        p.add(lblBadge);
        p.add(Box.createVerticalGlue());

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
                g2.setColor(new Color(0,0,0,8));
                for (int x = 20; x < getWidth(); x += 28)
                    for (int y = 20; y < getHeight(); y += 28)
                        g2.fillOval(x, y, 3, 3);
            }
        };
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());
        p.add(buildFormCard());
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
        card.setPreferredSize(new Dimension(420, 560));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 48, 32, 48));

        // Header
        JLabel welcome = makeLabel("Reset Password", fontBold(22), TEXT_PRIMARY);
        JLabel sub     = makeLabel("Verifikasi keamanan untuk mereset password", fontPlain(13), TEXT_SECONDARY);

        // Fields
        JLabel lblUser = makeLabel("Username", fontBold(12), TEXT_PRIMARY);
        tfUser = new LoginFrame.StyledTextField("");

        JLabel lblIbu = makeLabel("Nama Ibu Kandung", fontBold(12), TEXT_PRIMARY);
        tfIbu = new LoginFrame.StyledTextField("");

        JLabel lblPass = makeLabel("Password Baru", fontBold(12), TEXT_PRIMARY);
        tfPass = new LoginFrame.StyledPasswordField("");

        JLabel lblPassConfirm = makeLabel("Konfirmasi Password Baru", fontBold(12), TEXT_PRIMARY);
        tfPassConfirm = new LoginFrame.StyledPasswordField("");

        // Reset button
        btnReset = new LoginFrame.GradientButton("  Reset Password  \u2192");
        btnReset.addActionListener(e -> doReset());

        // Back to login link
        JLabel lblBack = makeLabel("Kembali ke Login", fontBold(12), new Color(0x6366F1));
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { goToLogin(); }
        });

        card.add(welcome);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(sub);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(lblUser);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfUser);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblIbu);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfIbu);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfPass);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblPassConfirm);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(tfPassConfirm);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btnReset);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(lblBack);

        return card;
    }
    
    private JTextField tfIbu;

    // ══ ACTIONS ══════════════════════════════════════════════════
    private void doReset() {
        String user = tfUser.getText().trim();
        String ibu = tfIbu.getText().trim();
        String pass = new String(tfPass.getPassword());
        String conf = new String(tfPassConfirm.getPassword());

        if (user.isEmpty() || ibu.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi password tidak cocok!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            tfPassConfirm.setText("");
            tfPassConfirm.requestFocus();
            return;
        }

        try {
            AdminDAO dao = new AdminDAO();
            if (!dao.isRecoveryValid(user, ibu)) {
                JOptionPane.showMessageDialog(this, "Username atau Nama Ibu Kandung salah!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dao.resetPassword(user, pass);
            JOptionPane.showMessageDialog(this, "Password berhasil direset!\nSilakan login dengan password baru.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            goToLogin();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // ══ HELPERS ══════════════════════════════════════════════════
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
}
