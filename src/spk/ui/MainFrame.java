package spk.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import static spk.ui.UITheme.*;

/**
 * MainFrame – Navigasi baru: Dashboard, Data Admin, Data Menu, Data Kriteria,
 *             Data Penilaian, Perhitungan & Perangkingan, Cetak Laporan
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentArea;
    private JButton activeBtn = null;
    private JLabel pageTitle;

    private DashboardPanel       dashPanel;
    private AdminPanel           adminPanel;
    private DataMenuPanel        menuPanel;
    private DataKriteriaPanel    kriteriaPanel;
    private DataPenilaianPanel   penilaianPanel;
    private PerhitunganPanel     perhitunganPanel;
    private LaporanPanel         laporanPanel;

    public MainFrame() {
        setTitle("SPK Ayam Bakar Asep");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildTopBar(),  BorderLayout.NORTH);

        // CardLayout
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG_PAGE);

        dashPanel        = new DashboardPanel();
        adminPanel       = new AdminPanel();
        menuPanel        = new DataMenuPanel(this);
        kriteriaPanel    = new DataKriteriaPanel(this);
        penilaianPanel   = new DataPenilaianPanel(this);
        perhitunganPanel = new PerhitunganPanel(this);
        laporanPanel     = new LaporanPanel(this);

        contentArea.add(dashPanel,        "dash");
        contentArea.add(adminPanel,       "admin");
        contentArea.add(menuPanel,        "menu");
        contentArea.add(kriteriaPanel,    "kriteria");
        contentArea.add(penilaianPanel,   "penilaian");
        contentArea.add(perhitunganPanel, "perhitungan");
        contentArea.add(laporanPanel,     "laporan");

        root.add(contentArea, BorderLayout.CENTER);
        setContentPane(root);
        navigateTo("dash");
    }

    /** Public navigate by card name – used by child panels */
    public void navigateTo(String card) {
        cardLayout.show(contentArea, card);
        if (pageTitle != null) pageTitle.setText(cardToTitle(card));
    }

    private String cardToTitle(String card) {
        switch (card) {
            case "dash":         return "Dashboard";
            case "admin":        return "Data Admin";
            case "menu":         return "Data Menu";
            case "kriteria":     return "Data Kriteria";
            case "penilaian":    return "Data Penilaian";
            case "perhitungan":  return "Perhitungan & Perangkingan";
            case "laporan":      return "Cetak Laporan";
            default:             return card;
        }
    }

    // ── TOP BAR ──────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 58));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(fontBold(17));
        pageTitle.setForeground(TEXT_PRIMARY);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBadge.setOpaque(false);
        JPanel nameBox = new JPanel(new GridLayout(2,1,0,1)); nameBox.setOpaque(false);
        JLabel userName = new JLabel("Administrator"); userName.setFont(fontBold(12)); userName.setForeground(TEXT_PRIMARY);
        JLabel userRole = new JLabel("Admin SPK");     userRole.setFont(fontPlain(10)); userRole.setForeground(TEXT_MUTED);
        nameBox.add(userName); nameBox.add(userRole);
        userBadge.add(nameBox);

        bar.add(pageTitle, BorderLayout.WEST);
        bar.add(userBadge, BorderLayout.EAST);
        return bar;
    }

    // ── SIDEBAR ──────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; UITheme.polish(g2);
                UITheme.fillGradientV(g2, 0, 0, getWidth(), getHeight(), DARK_900, DARK_800);
                g2.setColor(new Color(255,255,255,8));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Brand header
        JPanel brand = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; UITheme.polish(g2);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_600, getWidth(), getHeight(), SLATE_900);
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,15));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        brand.setOpaque(false);
        // Vertical layout: logo on left (large), text on right
        brand.setLayout(new BorderLayout(12, 0));
        brand.setBorder(new EmptyBorder(16, 14, 16, 14));
        brand.setMaximumSize(new Dimension(260, 130));
        brand.setMinimumSize(new Dimension(260, 130));
        brand.setPreferredSize(new Dimension(260, 130));

        // Load at 2x size (160px) for crisp rendering at 80px display size
        Image logoImg = LoginFrame.loadLogo(160);
        JLabel iconLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; UITheme.polish(g2);
                int sz = getWidth();
                if (logoImg != null) {
                    Shape cl = g2.getClip();
                    // Clip to circle
                    g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, sz, sz));
                    g2.drawImage(logoImg, 0, 0, sz, sz, this);
                    g2.setClip(cl);
                    // White ring border
                    g2.setColor(new Color(255, 255, 255, 130));
                    g2.setStroke(new java.awt.BasicStroke(2.5f));
                    g2.drawOval(1, 1, sz - 2, sz - 2);
                } else {
                    GradientPaint gp = new GradientPaint(0,0,INDIGO_600,sz,sz,INDIGO_800);
                    g2.setPaint(gp); g2.fillOval(0,0,sz,sz);
                    g2.setFont(fontBold(24)); g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String t = "AB";
                    g2.drawString(t, (sz-fm.stringWidth(t))/2, sz/2+fm.getAscent()/2-3);
                }
            }
            // 80×80px display size
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
            @Override public Dimension getMinimumSize()   { return new Dimension(80, 80); }
            @Override public Dimension getMaximumSize()   { return new Dimension(80, 80); }
        };

        JPanel brandText = new JPanel(new GridLayout(2,1,0,4)); brandText.setOpaque(false);
        JLabel b1 = new JLabel("Ayam Bakar Asep"); b1.setFont(fontBold(13)); b1.setForeground(Color.WHITE);
        JLabel b2 = new JLabel("SPK Metode SAW");  b2.setFont(fontPlain(11)); b2.setForeground(new Color(255,255,255,150));
        brandText.add(b1); brandText.add(b2);
        brand.add(iconLbl, BorderLayout.WEST);
        brand.add(brandText, BorderLayout.CENTER);
        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        addNavSection(sidebar, "MENU UTAMA");

        JButton b_dash  = navBtn("Dashboard");
        JButton b_admin = navBtn("Data Admin");
        sidebar.add(b_dash); sidebar.add(b_admin);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        addNavSection(sidebar, "DATA MASTER");
        JButton b_menu      = navBtn("Data Menu");
        JButton b_kriteria  = navBtn("Data Kriteria");
        JButton b_penilaian = navBtn("Data Penilaian");
        sidebar.add(b_menu); sidebar.add(b_kriteria); sidebar.add(b_penilaian);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        addNavSection(sidebar, "PROSES & LAPORAN");
        JButton b_hitung  = navBtn("Perhitungan & Perangkingan");
        JButton b_laporan = navBtn("Cetak Laporan");
        sidebar.add(b_hitung); sidebar.add(b_laporan);
        sidebar.add(Box.createVerticalGlue());

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,12));
        sep.setMaximumSize(new Dimension(260, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        JButton b_out = navBtn("Logout");
        sidebar.add(b_out);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // Wire up
        b_dash.addActionListener(e      -> { navigateTo("dash");         setActive(b_dash);      dashPanel.refresh(); });
        b_admin.addActionListener(e     -> { navigateTo("admin");        setActive(b_admin);     adminPanel.refresh(); });
        b_menu.addActionListener(e      -> { navigateTo("menu");         setActive(b_menu);      menuPanel.refresh(); });
        b_kriteria.addActionListener(e  -> { navigateTo("kriteria");     setActive(b_kriteria);  kriteriaPanel.refresh(); });
        b_penilaian.addActionListener(e -> { navigateTo("penilaian");    setActive(b_penilaian); penilaianPanel.refresh(); });
        b_hitung.addActionListener(e    -> { navigateTo("perhitungan");  setActive(b_hitung); });
        b_laporan.addActionListener(e   -> { navigateTo("laporan");      setActive(b_laporan); });
        b_out.addActionListener(e       -> doLogout());

        setActive(b_dash);
        return sidebar;
    }

    private void setActive(JButton btn) {
        if (activeBtn != null) { activeBtn.setBackground(new Color(0,0,0,0)); activeBtn.setForeground(new Color(255,255,255,140)); }
        activeBtn = btn;
        btn.setBackground(INDIGO_600); btn.setForeground(Color.WHITE);
    }

    private JButton navBtn(String label) {
        boolean[] hv = {false};
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; UITheme.polish(g2);
                Color bg = getBackground();
                if (bg != null && bg.getAlpha() > 0) {
                    g2.setColor(bg); g2.fillRoundRect(0, 2, getWidth(), getHeight()-4, 8, 8);
                    // Accent bar kiri
                    g2.setColor(new Color(255,255,255,200)); g2.fillRoundRect(0, 10, 4, getHeight()-20, 4, 4);
                } else if (hv[0]) {
                    g2.setColor(new Color(255,255,255,15)); g2.fillRoundRect(0, 2, getWidth(), getHeight()-4, 8, 8);
                }
                g2.setFont(getFont()); g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                // x=14 agar rata kiri sejajar dengan tepi logo (brand padding=14)
                g2.drawString(getText(), 14, y);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(255,255,255,170));
        btn.setBackground(new Color(0,0,0,0));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btn != activeBtn) { hv[0]=true; btn.repaint(); } }
            @Override public void mouseExited(MouseEvent e)  { hv[0]=false; btn.repaint(); }
        });
        return btn;
    }

    private void addNavSection(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(255, 255, 255, 90));
        lbl.setMaximumSize(new Dimension(260, 28));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        // left=14px agar rata kiri sejajar dengan nav button (x=14) dan tepi logo
        lbl.setBorder(new EmptyBorder(12, 14, 3, 0));
        p.add(lbl);
    }

    private void doLogout() {
        int r = JOptionPane.showConfirmDialog(this, "Keluar dari aplikasi?", "Logout", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) { dispose(); new LoginFrame().setVisible(true); }
    }
}
