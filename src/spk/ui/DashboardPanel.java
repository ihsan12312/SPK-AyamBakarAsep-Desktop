package spk.ui;

import spk.dao.AlternatifDAO;
import spk.dao.HasilDAO;
import spk.dao.KriteriaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import static spk.ui.UITheme.*;

/**
 * DashboardPanel – Premium stat cards (full gradient) + history table
 */
public class DashboardPanel extends JPanel {

    private JLabel lblKrit, lblAlt, lblBobot, lblSesi;
    private DefaultTableModel tblModel;

    public DashboardPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));

        // ── Header ──────────────────────────────────────────────
        JPanel hdr = new JPanel(new BorderLayout()); hdr.setOpaque(false);
        hdr.setBorder(new EmptyBorder(0, 0, 24, 0));
        JPanel hl = new JPanel(new GridLayout(2,1,0,6)); hl.setOpaque(false);
        JLabel title = new JLabel("Selamat datang di SPK Ayam Bakar Asep");
        title.setFont(fontBold(20)); title.setForeground(TEXT_PRIMARY);
        JLabel sub = new JLabel("Kelola data kriteria dan alternatif, lalu jalankan perhitungan SAW");
        sub.setFont(fontPlain(13)); sub.setForeground(TEXT_SECONDARY);
        hl.add(title); hl.add(sub);
        hdr.add(hl, BorderLayout.CENTER);

        // ── Stat Cards ──────────────────────────────────────────
        lblKrit  = new JLabel("0");
        lblAlt   = new JLabel("0");
        lblBobot = new JLabel("0%");
        lblSesi  = new JLabel("0");

        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false); cards.setBorder(new EmptyBorder(0, 0, 20, 0));
        cards.setPreferredSize(new Dimension(0, 130));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        cards.add(gradientCard("Kriteria Aktif",  lblKrit,  INDIGO_600, INDIGO_800));
        cards.add(gradientCard("Paket Menu",       lblAlt,   BLUE_600, BLUE_800));
        cards.add(gradientCard("Total Bobot",      lblBobot, GREEN_600, GREEN_800));
        cards.add(gradientCard("Sesi Hitung",      lblSesi,  PURPLE_600, PURPLE_800));

        // ── History Table ────────────────────────────────────────
        JPanel tableCard = buildHistoryCard();

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(hdr, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            int nK=0, nA=0, nS=0; double tb=0;
            List<Object[]> riwayat;
            @Override protected Void doInBackground() {
                try {
                    nK = new KriteriaDAO().getAll().size();
                    nA = new AlternatifDAO().getAll().size();
                    tb = new KriteriaDAO().getTotalBobot();
                    riwayat = new HasilDAO().getRiwayatSesi(10);
                    nS = new HasilDAO().countSesi();
                } catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                lblKrit.setText(String.valueOf(nK));
                lblAlt.setText(String.valueOf(nA));
                lblBobot.setText(String.format("%.0f%%", tb));
                lblSesi.setText(String.valueOf(nS));
                tblModel.setRowCount(0);
                if (riwayat != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                    for (int i = 0; i < riwayat.size(); i++) {
                        Object[] r = riwayat.get(i);
                        tblModel.addRow(new Object[]{i+1, r[1]!=null?r[1]:"–",
                            r[3], String.format("%.4f",(double)r[4]),
                            r[2]!=null?sdf.format(r[2]):"–"});
                    }
                }
            }
        }.execute();
    }

    // ── Full-gradient stat card ───────────────────────────────────
    private JPanel gradientCard(String label, JLabel valLbl, Color from, Color to) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                drawShadow(g2, 4, 4, getWidth()-8, getHeight()-8, 18);
                GradientPaint gp = new GradientPaint(0, 0, from, getWidth(), getHeight(), to);
                g2.setPaint(gp); g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 18, 18);
                g2.setColor(new Color(255,255,255,10));
                g2.fillOval(getWidth()-60, -30, 130, 130);
                g2.setColor(new Color(255,255,255,7));
                g2.fillOval(-30, getHeight()-50, 100, 100);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridLayout(2, 1, 0, 6));
        card.setBorder(new EmptyBorder(20, 22, 18, 16));

        // Nilai — font 28px cocok untuk semua text termasuk "100%"
        valLbl.setFont(fontBold(28));
        valLbl.setForeground(Color.WHITE);
        valLbl.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(fontBold(11));
        lbl.setForeground(new Color(255,255,255,190));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(valLbl);
        card.add(lbl);
        return card;
    }

    // ── History Table Card ────────────────────────────────────────
    private JPanel buildHistoryCard() {
        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 26, 22, 26));

        // Card header
        JPanel cardHdr = new JPanel(new BorderLayout()); cardHdr.setOpaque(false);
        cardHdr.setBorder(new EmptyBorder(0,0,14,0));
        JLabel title = new JLabel("Riwayat Perhitungan Terakhir");
        title.setFont(fontBold(15)); title.setForeground(TEXT_PRIMARY);

        JLabel badge = new JLabel(" 10 terakhir ");
        badge.setFont(fontBold(10)); badge.setForeground(BLUE_600);
        badge.setOpaque(true); badge.setBackground(BLUE_100);
        badge.setBorder(new EmptyBorder(3,8,3,8));

        cardHdr.add(title, BorderLayout.WEST); cardHdr.add(badge, BorderLayout.EAST);

        String[] cols = {"No","Catatan","Pemenang","Skor (Vi)","Waktu"};
        tblModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = styledTable(tblModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(100);

        card.add(cardHdr, BorderLayout.NORTH);
        card.add(new JScrollPane(table) {{ setBorder(null); getViewport().setBackground(Color.WHITE); }}, BorderLayout.CENTER);
        return card;
    }

    // ── Shared Components ─────────────────────────────────────────

    public static JPanel shadowCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                drawShadow(g2, 4, 6, getWidth()-8, getHeight()-8, 16);
                g2.setColor(Color.WHITE); g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 16, 16);
                // Subtle top border accent
                g2.setColor(new Color(0xF1F5F9)); g2.setStroke(new java.awt.BasicStroke(1));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 16, 16);
            }
        };
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(fontPlain(13)); t.setRowHeight(40);
        t.setBackground(Color.WHITE); t.setForeground(TEXT_PRIMARY);
        t.setSelectionBackground(new Color(0xE0E7FF));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setGridColor(new Color(0xF1F5F9)); t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.getTableHeader().setFont(fontBold(11));
        t.getTableHeader().setBackground(new Color(0xF8FAFC));
        t.getTableHeader().setForeground(TEXT_SECONDARY);
        t.getTableHeader().setPreferredSize(new Dimension(0, 38));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,2,0, new Color(0xE2E8F0)));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setFont(r == 0 ? fontBold(13) : fontPlain(13));
                setBorder(new EmptyBorder(0, 16, 0, 16));
                setBackground(sel ? new Color(0xEFF6FF) : (r%2==0 ? Color.WHITE : new Color(0xFAFBFE)));
                setForeground(TEXT_PRIMARY);
                return this;
            }
        });
        return t;
    }
}
