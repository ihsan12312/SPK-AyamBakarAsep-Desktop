package spk.ui;

import spk.dao.AlternatifDAO;
import spk.dao.HasilDAO;
import spk.dao.KriteriaDAO;
import spk.model.Alternatif;
import spk.model.HasilSAW;
import spk.model.Kriteria;
import spk.util.SAWCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

public class HasilPanel extends JPanel {

    private DefaultTableModel rankModel;
    private JTable rankTable;
    private JPanel winnerCard;
    private JLabel lblWName, lblWScore, lblWHarga;
    private BarChart chart;
    private JTextField tfCatatan;
    private JPanel contentSwitch;
    private CardLayout contentCard;
    private DefaultTableModel riwayatModel;

    public HasilPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Header ──────────────────────────────────────────────
        JPanel hdr = pageHeader("Perhitungan SAW", "Jalankan algoritma SAW untuk menemukan paket terbaik");
        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); hRight.setOpaque(false);
        tfCatatan = new JTextField(16); tfCatatan.setFont(fontPlain(13)); tfCatatan.setToolTipText("Catatan sesi");
        tfCatatan.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1,true), new EmptyBorder(7,12,7,12)));
        JButton btnHitung = accentBtn("Hitung SAW");
        btnHitung.addActionListener(e -> doHitung());
        hRight.add(new JLabel("Catatan:") {{ setFont(fontBold(12)); setForeground(TEXT_SECONDARY); }});
        hRight.add(tfCatatan); hRight.add(btnHitung);
        hdr.add(hRight, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // ── Winner card ──────────────────────────────────────────
        winnerCard = buildWinnerCard();
        winnerCard.setVisible(false);

        // ── Rank table ───────────────────────────────────────────
        String[] cols = {"Rank","Paket Menu","Harga","Skor Vi","Status"};
        rankModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        rankTable = styledTable(rankModel);
        rankTable.getColumnModel().getColumn(0).setMaxWidth(70);
        rankTable.getColumnModel().getColumn(2).setMaxWidth(130);
        rankTable.getColumnModel().getColumn(3).setMaxWidth(100);
        rankTable.getColumnModel().getColumn(4).setMaxWidth(110);

        JPanel tableCard = shadowCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel tTitle = new JLabel("📋  Tabel Ranking");
        tTitle.setFont(fontBold(14)); tTitle.setForeground(TEXT_PRIMARY); tTitle.setBorder(new EmptyBorder(0,0,10,0));
        tableCard.add(tTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(rankTable) {{ setBorder(null); }}, BorderLayout.CENTER);

        // ── Chart ────────────────────────────────────────────────
        chart = new BarChart();
        chart.setPreferredSize(new Dimension(600, 280));
        chart.setMinimumSize(new Dimension(300, 260));
        JPanel chartCard = shadowCard();
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        chartCard.setPreferredSize(new Dimension(600, 340));
        JLabel cTitle = new JLabel("Grafik Skor Akhir Vi");
        cTitle.setFont(fontBold(14)); cTitle.setForeground(TEXT_PRIMARY); cTitle.setBorder(new EmptyBorder(0,0,10,0));
        chartCard.add(cTitle, BorderLayout.NORTH); chartCard.add(chart, BorderLayout.CENTER);

        // ── Riwayat Sesi ─────────────────────────────────────────
        JPanel riwayatCard = buildRiwayatCard();

        // ── Result Panel (scrollable) ─────────────────────────────
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setOpaque(false);
        winnerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        riwayatCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultPanel.add(winnerCard);
        resultPanel.add(Box.createRigidArea(new Dimension(0,14)));
        resultPanel.add(chartCard);
        resultPanel.add(Box.createRigidArea(new Dimension(0,14)));
        resultPanel.add(tableCard);
        resultPanel.add(Box.createRigidArea(new Dimension(0,14)));
        resultPanel.add(riwayatCard);
        resultPanel.add(Box.createRigidArea(new Dimension(0,10)));

        JScrollPane resultScroll = new JScrollPane(resultPanel); resultScroll.getVerticalScrollBar().setPreferredSize(new Dimension(6,0)); resultScroll.getVerticalScrollBar().setUnitIncrement(16);
        resultScroll.setBorder(null);
        resultScroll.getViewport().setBackground(BG_PAGE);

        // ── Placeholder ───────────────────────────────────────────
        JPanel placeholder = buildPlaceholder();

        contentCard = new CardLayout();
        contentSwitch = new JPanel(contentCard); contentSwitch.setOpaque(false);
        contentSwitch.add(placeholder, "ph");
        contentSwitch.add(resultScroll, "result");

        add(contentSwitch, BorderLayout.CENTER);

        // Auto-load hasil terakhir dari DB
        loadHasilTerakhir();
    }

    // ── Auto-load hasil terakhir ──────────────────────────────────
    public void loadHasilTerakhir() {
        new SwingWorker<List<HasilSAW>, Void>() {
            @Override protected List<HasilSAW> doInBackground() {
                try {
                    HasilDAO dao = new HasilDAO();
                    int idSesi = dao.getIdSesiTerakhir();
                    if (idSesi < 0) return null;
                    return dao.getHasilBySesi(idSesi);
                } catch (Exception ex) { ex.printStackTrace(); return null; }
            }
            @Override protected void done() {
                try {
                    List<HasilSAW> hasil = get();
                    if (hasil != null && !hasil.isEmpty()) {
                        updateResults(hasil);
                        loadRiwayat();
                        contentCard.show(contentSwitch, "result");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void loadRiwayat() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                try { return new HasilDAO().getRiwayatSesi(20); } catch (Exception ex) { return null; }
            }
            @Override protected void done() {
                try {
                    List<Object[]> rows = get(); if (rows == null) return;
                    riwayatModel.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                    int i = 1;
                    for (Object[] r : rows) {
                        riwayatModel.addRow(new Object[]{i++, r[1] != null ? r[1] : "–",
                            "🏆 " + r[3], spk.ui.UITheme.formatDesimal((double)r[4]),
                            r[2] != null ? sdf.format(r[2]) : "–"});
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ── Riwayat Card ─────────────────────────────────────────────
    private JPanel buildRiwayatCard() {
        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Riwayat Sesi Perhitungan");
        title.setFont(fontBold(14)); title.setForeground(TEXT_PRIMARY); title.setBorder(new EmptyBorder(0,0,10,0));

        String[] cols = {"No","Catatan","Pemenang","Skor Vi","Waktu"};
        riwayatModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable tbl = styledTable(riwayatModel);
        tbl.getColumnModel().getColumn(0).setMaxWidth(45);
        tbl.getColumnModel().getColumn(3).setMaxWidth(95);
        tbl.setPreferredScrollableViewportSize(new Dimension(0, 160));

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(tbl) {{ setBorder(null); }}, BorderLayout.CENTER);
        return card;
    }

    // ── Winner card ───────────────────────────────────────────────
    private JPanel buildWinnerCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                drawShadow(g2, 4, 4, getWidth()-8, getHeight()-8, 18);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x991B1B), getWidth(), 0, new Color(0x1E293B));
                g2.setPaint(gp); g2.fillRoundRect(2, 2, getWidth()-6, getHeight()-6, 18, 18);
                g2.setColor(new Color(255,255,255,8));
                g2.fillOval(getWidth()-180, -60, 240, 240);
            }
        };
        card.setOpaque(false); card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(22, 28, 22, 28));

        JLabel trophy = new JLabel("🏆"); trophy.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));

        JPanel info = new JPanel(new GridLayout(3,1,0,6)); info.setOpaque(false);
        JLabel labelTop = new JLabel("🥇  REKOMENDASI TERBAIK");
        labelTop.setFont(fontBold(11)); labelTop.setForeground(GOLD_LIGHT);
        lblWName = new JLabel("–"); lblWName.setFont(fontBold(24)); lblWName.setForeground(Color.WHITE);
        lblWHarga = new JLabel("–"); lblWHarga.setFont(fontPlain(13)); lblWHarga.setForeground(new Color(255,255,255,160));
        info.add(labelTop); info.add(lblWName); info.add(lblWHarga);

        JPanel scoreBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; polish(g2);
                g2.setColor(new Color(255,255,255,15)); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(new Color(255,255,255,40)); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12); super.paintComponent(g);
            }
        };
        scoreBox.setOpaque(false); scoreBox.setLayout(new GridLayout(2,1,0,4));
        scoreBox.setBorder(new EmptyBorder(12,20,12,20)); scoreBox.setPreferredSize(new Dimension(130, 0));
        JLabel sLabel = new JLabel("Skor Vi", SwingConstants.CENTER); sLabel.setFont(fontBold(10)); sLabel.setForeground(new Color(255,255,255,130));
        lblWScore = new JLabel("0.0000", SwingConstants.CENTER); lblWScore.setFont(fontBold(28)); lblWScore.setForeground(GOLD);
        scoreBox.add(sLabel); scoreBox.add(lblWScore);

        card.add(trophy, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(scoreBox, BorderLayout.EAST);
        return card;
    }

    private JPanel buildPlaceholder() {
        JPanel p = new JPanel(new GridBagLayout()); p.setOpaque(false);
        JPanel box = shadowCard(); box.setPreferredSize(new Dimension(520, 260));
        box.setLayout(new GridBagLayout());
        JPanel inner = new JPanel(new GridLayout(3,1,0,10)); inner.setOpaque(false);
        JLabel icon = new JLabel("⚡", SwingConstants.CENTER); icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56)); icon.setForeground(new Color(0xE2E8F0));
        JLabel title = new JLabel("Siap Menghitung!", SwingConstants.CENTER); title.setFont(fontBold(18)); title.setForeground(TEXT_SECONDARY);
        JLabel sub = new JLabel("Klik tombol \"Hitung SAW\" di atas untuk memulai", SwingConstants.CENTER); sub.setFont(fontPlain(13)); sub.setForeground(TEXT_MUTED);
        inner.add(icon); inner.add(title); inner.add(sub);
        box.add(inner); p.add(box); return p;
    }

    // Hitung SAW ────────────────────────────────────────────────
    private void doHitung() {
        String cat = tfCatatan.getText().trim();
        new SwingWorker<List<HasilSAW>, Void>() {
            String err;
            @Override protected List<HasilSAW> doInBackground() {
                try {
                    KriteriaDAO kDao = new KriteriaDAO(); AlternatifDAO aDao = new AlternatifDAO();
                    List<Kriteria> krits = kDao.getAll(); List<Alternatif> alts = aDao.getAllWithNilai();
                    // Validation for 100% weight removed as requested by user
                    // double tb = kDao.getTotalBobot();
                    // if (Math.abs(tb - 100) > 0.01) throw new IllegalStateException("Total bobot = " + String.format("%.2f",tb) + "%. Harus 100%!");
                    if (alts.size() < 2) throw new IllegalStateException("Minimal 2 alternatif!");
                    List<HasilSAW> hasil = SAWCalculator.hitung(alts, krits);
                    new HasilDAO().simpanHasil(hasil, cat.isEmpty() ? "Perhitungan SAW" : cat);
                    return hasil;
                } catch (Exception ex) { err = ex.getMessage(); return null; }
            }
            @Override protected void done() {
                try {
                    List<HasilSAW> hasil = get();
                    if (hasil == null) { JOptionPane.showMessageDialog(HasilPanel.this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                    updateResults(hasil);
                    loadRiwayat();
                    contentCard.show(contentSwitch, "result");
                } catch (Exception ex) { JOptionPane.showMessageDialog(HasilPanel.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void updateResults(List<HasilSAW> hasil) {
        if (hasil.isEmpty()) return;
        HasilSAW best = hasil.get(0);
        winnerCard.setVisible(true);
        lblWName.setText(best.getAlternatif().getNamaPaket());
        lblWScore.setText(best.getSkorFormatted());
        String desc = best.getAlternatif().getDeskripsi();
        lblWHarga.setText(best.getAlternatif().getHargaFormatted() + (desc != null && !desc.isEmpty() ? "  \u2022  " + desc : ""));
        rankModel.setRowCount(0);
        String[] medali = {"🥇","🥈","🥉"};
        for (HasilSAW h : hasil) {
            String rk = h.getRanking() <= 3 ? medali[h.getRanking()-1] + " #" + h.getRanking() : "#" + h.getRanking();
            rankModel.addRow(new Object[]{rk, h.getAlternatif().getNamaPaket(), h.getAlternatif().getHargaFormatted(), h.getSkorFormatted(), h.getStatusLabel()});
        }
        chart.setData(hasil); chart.repaint();
    }

    // ── BAR CHART ─────────────────────────────────────────────────
    static class BarChart extends JPanel {
        private List<HasilSAW> data;
        void setData(List<HasilSAW> d) { this.data = d; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g; polish(g2);
            g2.setColor(Color.WHITE); g2.fillRect(0, 0, getWidth(), getHeight());

            int W = getWidth(), H = getHeight();
            int pL = 55, pR = 20, pT = 36, pB = 56;
            int cW = W - pL - pR, cH = H - pT - pB;
            int n = data.size();
            if (n == 0 || cW <= 0 || cH <= 0) return;

            double maxV = data.stream().mapToDouble(HasilSAW::getSkorAkhir).max().orElse(1);

            // Grid lines
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            for (int i = 1; i <= 5; i++) {
                int y = pT + cH - (int)(cH * i / 5.0);
                g2.setColor(new Color(0xF1F5F9)); g2.drawLine(pL, y, pL+cW, y);
                g2.setColor(TEXT_MUTED); g2.setFont(fontPlain(10));
                g2.drawString(String.format("%.3f", maxV * i / 5.0), 2, y+4);
            }
            g2.setStroke(new BasicStroke(1));

            // Axis
            g2.setColor(BORDER);
            g2.drawLine(pL, pT, pL, pT+cH);
            g2.drawLine(pL, pT+cH, pL+cW, pT+cH);

            // Bars
            int barW = Math.min(72, (cW / n) - 16);
            int gap  = n > 1 ? (cW - barW * n) / (n + 1) : cW/2 - barW/2;

            Color[] COLORS = {RED_600, BLUE_700, GREEN_700, PURPLE_700, new Color(0xD97706), new Color(0x0891B2)};
            String[] TOP_LABELS = {"👑", "🥈", "🥉"};

            for (int i = 0; i < n; i++) {
                HasilSAW h = data.get(i);
                int x = pL + gap + i * (barW + gap);
                int barH = (int)(cH * (h.getSkorAkhir() / maxV));
                int y = pT + cH - barH;
                Color col = COLORS[i % COLORS.length];

                // Shadow
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 30));
                g2.fillRoundRect(x+3, y+4, barW, barH, 10, 10);

                // Bar gradient
                GradientPaint bp = new GradientPaint(x, y, col.brighter(), x, y+barH, col.darker());
                g2.setPaint(bp); g2.fillRoundRect(x, y, barW, barH, 10, 10);

                // Score label inside bar
                if (barH > 24) {
                    g2.setFont(fontBold(10)); g2.setColor(new Color(255,255,255,230));
                    String sc = spk.ui.UITheme.formatDesimal(h.getSkorAkhir());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(sc, x + (barW - fm.stringWidth(sc)) / 2, y + 16);
                }

                // Medal above bar
                if (i < 3) {
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                    FontMetrics fm = g2.getFontMetrics();
                    String medal = TOP_LABELS[i];
                    g2.drawString(medal, x + (barW - fm.stringWidth(medal))/2, y - 6);
                }

                // Name below axis
                g2.setFont(fontPlain(11)); g2.setColor(TEXT_SECONDARY);
                String name = h.getAlternatif().getNamaPaket();
                if (name.length() > 12) name = name.substring(0, 11) + "…";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(name, x + (barW - fm.stringWidth(name))/2, pT+cH+20);
            }
        }
    }
}
