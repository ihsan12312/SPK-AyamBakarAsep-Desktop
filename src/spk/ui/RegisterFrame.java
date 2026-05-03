package spk.ui;

import spk.dao.AdminDAO;
import spk.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import static spk.ui.UITheme.*;

/**
 * RegisterFrame – Premium full-screen registration window (same design as Login)
 */
public class RegisterFrame extends JFrame {

    private JTextField     tfNama;
    private JTextField     tfUser;
    private JPasswordField tfPass;
    private JPasswordField tfPassConfirm;
    private JButton        btnRegister;

    public RegisterFrame() {
        setTitle("SPK Ayam Bakar Asep – Registrasi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 700, 24, 24));

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
        p.setPreferredSize(new Dimension(380, 700));
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
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0xDC2626), sz, sz, new Color(0x7F1D1D));
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
        JLabel lblSub   = makeLabel("Daftarkan Akun Admin Baru", fontPlain(13), new Color(255,255,255,160));
        JLabel lblBadge = makeBadge("Registrasi Admin");

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
        card.setPreferredSize(new Dimension(540, 600));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(36, 48, 36, 48));

        // Header
        JPanel pHead = new JPanel(); pHead.setLayout(new BoxLayout(pHead, BoxLayout.Y_AXIS)); pHead.setOpaque(false);
        JLabel welcome = makeLabel("Buat Akun Baru", fontBold(22), TEXT_PRIMARY);
        JLabel sub     = makeLabel("Lengkapi biodata dan informasi keamanan", fontPlain(13), TEXT_SECONDARY);
        pHead.add(welcome); pHead.add(Box.createRigidArea(new Dimension(0,4))); pHead.add(sub);
        pHead.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(pHead, BorderLayout.NORTH);

        // Fields (2 Columns)
        JPanel pGrid = new JPanel(new GridLayout(4, 2, 16, 12));
        pGrid.setOpaque(false);

        tfNama = new LoginFrame.StyledTextField("");
        tfUser = new LoginFrame.StyledTextField("");
        tfPass = new LoginFrame.StyledPasswordField("");
        tfPassConfirm = new LoginFrame.StyledPasswordField("");
        JTextField tfHp = new LoginFrame.StyledTextField("");
        JTextField tfAlamat = new LoginFrame.StyledTextField("");
        JTextField tfIbu = new LoginFrame.StyledTextField("");

        pGrid.add(makeFieldPanel("Nama Lengkap", tfNama));
        pGrid.add(makeFieldPanel("Username", tfUser));
        pGrid.add(makeFieldPanel("Password", tfPass));
        pGrid.add(makeFieldPanel("Konfirmasi Password", tfPassConfirm));
        pGrid.add(makeFieldPanel("Nomor HP", tfHp));
        pGrid.add(makeFieldPanel("Alamat", tfAlamat));
        pGrid.add(makeFieldPanel("Nama Ibu Kandung (Keamanan)", tfIbu));

        // Simpan referensi field baru ke property class agar bisa dibaca oleh doRegister()
        this.tfHpRef = tfHp;
        this.tfAlamatRef = tfAlamat;
        this.tfIbuRef = tfIbu;

        card.add(pGrid, BorderLayout.CENTER);

        // Footer / Buttons
        JPanel pFoot = new JPanel(); pFoot.setLayout(new BoxLayout(pFoot, BoxLayout.Y_AXIS)); pFoot.setOpaque(false);
        pFoot.add(Box.createRigidArea(new Dimension(0, 24)));
        btnRegister = new LoginFrame.GradientButton("  Daftar Sekarang  \u2192");
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.addActionListener(e -> doRegister());

        JLabel lblBack = makeLabel("Sudah punya akun? Masuk disini", fontBold(12), INDIGO_600);
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { goToLogin(); }
        });
        pFoot.add(btnRegister); pFoot.add(Box.createRigidArea(new Dimension(0,14))); pFoot.add(lblBack);
        card.add(pFoot, BorderLayout.SOUTH);

        return card;
    }

    private JTextField tfHpRef, tfAlamatRef, tfIbuRef;

    private JPanel makeFieldPanel(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(makeLabel(label, fontBold(12), TEXT_PRIMARY), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // ══ ACTIONS ══════════════════════════════════════════════════
    private void doRegister() {
        String nama = tfNama.getText().trim();
        String user = tfUser.getText().trim();
        String pass = new String(tfPass.getPassword());
        String passConfirm = new String(tfPassConfirm.getPassword());
        String hp = tfHpRef.getText().trim();
        String alamat = tfAlamatRef.getText().trim();
        String ibu = tfIbuRef.getText().trim();

        if (nama.isEmpty() || user.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || ibu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Username, Password, dan Nama Ibu Kandung harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(passConfirm)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi password tidak cocok!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            tfPassConfirm.setText("");
            tfPassConfirm.requestFocus();
            return;
        }

        try {
            AdminDAO dao = new AdminDAO();
            if (dao.isUsernameExists(user)) {
                JOptionPane.showMessageDialog(this, "Username '" + user + "' sudah digunakan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Admin a = new Admin();
            a.setNamaLengkap(nama);
            a.setUsername(user);
            a.setPassword(pass);
            a.setNoHp(hp);
            a.setAlamat(alamat);
            a.setNamaIbuKandung(ibu);
            dao.insert(a);

            JOptionPane.showMessageDialog(this, "Registrasi berhasil!\nSilakan login dengan akun baru Anda.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
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
