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
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

/**
 * PerhitunganPanel – Hitung SAW + tampilkan:
 * 1. Data Awal  2. Normalisasi  3. Hasil Akhir  4. Perangkingan
 */
public class PerhitunganPanel extends JPanel {

    private JTabbedPane tabs;
    private DefaultTableModel mdlAwal, mdlNorm, mdlAkhir, mdlRank;
    private JLabel lblWinner;
    private MainFrame mainFrame;
    private List<HasilSAW> lastHasil;
    private List<Kriteria> lastKrit;
    private List<Alternatif> lastAlts;

    public PerhitunganPanel(MainFrame frame) {
        this.mainFrame = frame;
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Perhitungan & Perangkingan", "Jalankan metode SAW untuk menemukan paket menu terbaik");
        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); hRight.setOpaque(false);
        JButton btnHitung  = accentBtn("Hitung & Simpan Peringkat");
        btnHitung.addActionListener(e  -> doHitung());
        hRight.add(btnHitung);
        hdr.add(hRight, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // Winner banner (hidden initially)
        lblWinner = new JLabel(" ");
        lblWinner.setFont(fontBold(15));
        lblWinner.setForeground(Color.WHITE);
        lblWinner.setOpaque(false);
        lblWinner.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel winnerBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g; UITheme.polish(g2);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_700, getWidth(), 0, SLATE_900);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        winnerBar.setOpaque(false);
        winnerBar.setBorder(new EmptyBorder(0, 0, 14, 0));
        winnerBar.add(lblWinner, BorderLayout.WEST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(fontBold(13));
        tabs.setBackground(Color.WHITE);

        mdlAwal  = buildTabModel(tabs, "1. Data Awal");
        mdlNorm  = buildTabModel(tabs, "2. Normalisasi");
        mdlAkhir = buildTabModel(tabs, "3. Hasil Akhir");
        mdlRank  = buildTabModel(tabs, "4. Perangkingan");

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(winnerBar, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadHasilTerakhir();
    }

    private DefaultTableModel buildTabModel(JTabbedPane tp, String title) {
        DefaultTableModel m = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = styledTable(m);
        JScrollPane sp = new JScrollPane(t); sp.setBorder(null);
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE); p.add(sp, BorderLayout.CENTER);
        tp.addTab(title, p);
        return m;
    }

    private void loadHasilTerakhir() {
        new SwingWorker<List<HasilSAW>, Void>() {
            @Override protected List<HasilSAW> doInBackground() {
                try {
                    HasilDAO dao = new HasilDAO();
                    int id = dao.getIdSesiTerakhir();
                    if (id < 0) return null;
                    return dao.getHasilBySesi(id);
                } catch (Exception ex) { return null; }
            }
            @Override protected void done() {
                try { List<HasilSAW> h = get(); if (h != null && !h.isEmpty()) renderAll(h); }
                catch (Exception ignored) {}
            }
        }.execute();
    }

    private void doHitung() {
        new SwingWorker<List<HasilSAW>, Void>() {
            String err;
            @Override protected List<HasilSAW> doInBackground() {
                try {
                    KriteriaDAO kDao = new KriteriaDAO(); AlternatifDAO aDao = new AlternatifDAO();
                    lastKrit = kDao.getAll(); lastAlts = aDao.getAllWithNilai();
                    double tb = kDao.getTotalBobot();
                    // Validasi total bobot tidak diwajibkan — bobot bebas (desimal)
                    if (lastAlts.size() < 2) throw new IllegalStateException("Minimal 2 alternatif menu!");
                    List<HasilSAW> hasil = SAWCalculator.hitung(lastAlts, lastKrit);
                    new HasilDAO().simpanHasil(hasil, "Perhitungan SAW");
                    return hasil;
                } catch (Exception ex) { err = ex.getMessage(); return null; }
            }
            @Override protected void done() {
                try {
                    List<HasilSAW> hasil = get();
                    if (hasil == null) { JOptionPane.showMessageDialog(PerhitunganPanel.this, err, "Error", JOptionPane.ERROR_MESSAGE); return; }
                    renderAll(hasil);
                    toast("Perhitungan selesai! Hasil telah disimpan.");
                } catch (Exception ex) { JOptionPane.showMessageDialog(PerhitunganPanel.this, ex.getMessage()); }
            }
        }.execute();
    }

    private void renderAll(List<HasilSAW> hasil) {
        lastHasil = hasil;
        if (hasil.isEmpty()) return;
        HasilSAW best = hasil.get(0);
        lblWinner.setText("  Rekomendasi Terbaik: " + best.getAlternatif().getNamaPaket() + "   (Skor Vi = " + best.getSkorFormatted() + ")");

        // ── Tab 1: Data Awal ─────────────────────────────────────
        mdlAwal.setColumnCount(0);
        mdlAwal.addColumn("Alternatif");

        // Re-fetch krits from hasil order
        if (lastKrit != null) {
            for (Kriteria k : lastKrit) mdlAwal.addColumn(k.getNamaKriteria() + "\n(" + (k.isBenefit() ? "B" : "C") + ")");
            mdlAwal.setRowCount(0);
            for (HasilSAW h : hasil) {
                Object[] row = new Object[1 + lastKrit.size()];
                row[0] = h.getAlternatif().getNamaPaket();
                for (int j = 0; j < lastKrit.size(); j++) {
                    double v = h.getAlternatif().getNilai(lastKrit.get(j).getIdKriteria());
                    row[1+j] = spk.ui.UITheme.formatDesimal(v);
                }
                mdlAwal.addRow(row);
            }
        }

        // ── Tab 2: Normalisasi ────────────────────────────────────
        mdlNorm.setColumnCount(0);
        mdlNorm.addColumn("Alternatif");
        if (lastKrit != null) {
            for (Kriteria k : lastKrit) mdlNorm.addColumn("r_" + k.getNamaKriteria());
            mdlNorm.setRowCount(0);
            for (HasilSAW h : hasil) {
                Object[] row = new Object[1 + lastKrit.size()];
                row[0] = h.getAlternatif().getNamaPaket();
                for (int j = 0; j < lastKrit.size(); j++) {
                    double r = h.getNormalisasi(lastKrit.get(j).getIdKriteria());
                    row[1+j] = spk.ui.UITheme.formatDesimal(r);
                }
                mdlNorm.addRow(row);
            }
        }

        // ── Tab 3: Hasil Akhir ────────────────────────────────────
        mdlAkhir.setColumnCount(0);
        mdlAkhir.addColumn("Alternatif");
        if (lastKrit != null) {
            for (Kriteria k : lastKrit) mdlAkhir.addColumn("w*r_" + k.getNamaKriteria());
            mdlAkhir.addColumn("Vi (Total)");
            mdlAkhir.setRowCount(0);
            for (HasilSAW h : hasil) {
                Object[] row = new Object[2 + lastKrit.size()];
                row[0] = h.getAlternatif().getNamaPaket();
                double vi = 0;
                for (int j = 0; j < lastKrit.size(); j++) {
                    double wr = h.getNormalisasi(lastKrit.get(j).getIdKriteria()) * lastKrit.get(j).getBobotDesimal();
                    row[1+j] = spk.ui.UITheme.formatDesimal(wr);
                    vi += wr;
                }
                row[1 + lastKrit.size()] = spk.ui.UITheme.formatDesimal(vi);
                mdlAkhir.addRow(row);
            }
        }

        // ── Tab 4: Perangkingan ───────────────────────────────────
        mdlRank.setColumnCount(0);
        mdlRank.addColumn("Peringkat"); mdlRank.addColumn("Nama Paket"); mdlRank.addColumn("Skor Vi"); mdlRank.addColumn("Kategori");
        mdlRank.setRowCount(0);
        String[] medals = {"Terbaik", "Sangat Baik", "Baik"};
        for (HasilSAW h : hasil) {
            String kat = h.getRanking() <= 3 ? medals[h.getRanking()-1] : "Cukup";
            mdlRank.addRow(new Object[]{"#" + h.getRanking(), h.getAlternatif().getNamaPaket(), h.getSkorFormatted(), kat});
        }

        tabs.setSelectedIndex(3);
    }
}
