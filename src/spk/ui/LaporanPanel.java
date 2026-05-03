package spk.ui;

import spk.dao.AlternatifDAO;
import spk.dao.HasilDAO;
import spk.dao.KriteriaDAO;
import spk.model.Alternatif;
import spk.model.HasilSAW;
import spk.model.Kriteria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

/**
 * LaporanPanel – Cetak 4 laporan dalam format tabel, masing-masing ada tombol Cetak PDF.
 */
public class LaporanPanel extends JPanel {

    private MainFrame mainFrame;

    public LaporanPanel(MainFrame frame) {
        this.mainFrame = frame;
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Cetak Laporan", "Cetak laporan data SPK dalam format tabel PDF");
        add(hdr, BorderLayout.NORTH);

        // Scroll area
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(buildLaporanCard("1. Laporan Data Kriteria",
                new String[]{"ID","Nama Kriteria","Tipe","Bobot (%)"},
                this::loadKriteria));
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(buildLaporanCard("2. Laporan Data Menu Makanan",
                new String[]{"No","Kode Menu","Nama Paket","Deskripsi"},
                this::loadMenu));
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(buildLaporanCard("3. Laporan Data Penilaian Menu Paket",
                null, // dynamic columns
                this::loadPenilaian));
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(buildLaporanCard("4. Data Hasil Akhir Peringkat Menu Terbaik",
                new String[]{"Rank", "Kode Menu", "Nama Menu", "Nilai Akhir"},
                this::loadRanking));
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(BG_PAGE);
        add(scroll, BorderLayout.CENTER);
    }

    private interface DataLoader {
        void load(DefaultTableModel model);
    }

    private JPanel buildLaporanCard(String title, String[] cols, DataLoader loader) {
        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Title + Cetak button
        JPanel topRow = new JPanel(new BorderLayout()); topRow.setOpaque(false); topRow.setBorder(new EmptyBorder(0,0,12,0));
        JLabel lbl = new JLabel(title); lbl.setFont(fontBold(14)); lbl.setForeground(TEXT_PRIMARY);
        JButton btnCetak = accentBtn("Cetak PDF");

        DefaultTableModel model;
        if (cols != null) {
            model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        } else {
            model = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
        }

        JTable table = styledTable(model);
        JScrollPane sp = new JScrollPane(table); sp.setBorder(null);
        sp.setPreferredSize(new Dimension(0, 160));

        // Load data button
        JButton btnMuat = successBtn("Muat Data");
        btnMuat.addActionListener(e -> {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() {
                    loader.load(model); return null;
                }
            }.execute();
        });

        btnCetak.addActionListener(e -> {
            printFormal(title, model);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); btnRow.setOpaque(false);
        btnRow.add(btnMuat); btnRow.add(btnCetak);

        topRow.add(lbl, BorderLayout.WEST); topRow.add(btnRow, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        // Auto load
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { loader.load(model); return null; }
        }.execute();

        return card;
    }

    private void loadKriteria(DefaultTableModel m) {
        try {
            List<Kriteria> list = new KriteriaDAO().getAll();
            SwingUtilities.invokeLater(() -> {
                m.setRowCount(0);
                int i = 1;
                for (Kriteria k : list)
                    m.addRow(new Object[]{k.getIdKriteria(), k.getNamaKriteria(),
                        k.isBenefit() ? "Benefit" : "Cost", String.format("%.2f%%", k.getBobot())});
            });
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadMenu(DefaultTableModel m) {
        try {
            List<Alternatif> list = new AlternatifDAO().getAll();
            SwingUtilities.invokeLater(() -> {
                m.setRowCount(0);
                int i = 1;
                for (Alternatif a : list)
                    m.addRow(new Object[]{i, a.getKodeMakanan(), a.getNamaPaket(), a.getDeskripsi()});
            });
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadPenilaian(DefaultTableModel m) {
        try {
            List<Alternatif> alts = new AlternatifDAO().getAllWithNilai();
            List<Kriteria>  krits = new KriteriaDAO().getAll();
            SwingUtilities.invokeLater(() -> {
                m.setColumnCount(0);
                m.addColumn("No"); m.addColumn("Nama Menu");
                for (Kriteria k : krits) m.addColumn(k.getNamaKriteria());
                m.setRowCount(0);
                int i = 1;
                for (Alternatif a : alts) {
                    Object[] row = new Object[2 + krits.size()];
                    row[0] = i++; row[1] = a.getNamaPaket();
                    for (int j = 0; j < krits.size(); j++) {
                        double v = a.getNilai(krits.get(j).getIdKriteria());
                        row[2+j] = v == 0 ? "–" : String.format("%.0f", v);
                    }
                    m.addRow(row);
                }
            });
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadRanking(DefaultTableModel m) {
        try {
            HasilDAO dao = new HasilDAO();
            int idSesi = dao.getIdSesiTerakhir();
            if (idSesi < 0) { SwingUtilities.invokeLater(() -> m.setRowCount(0)); return; }
            List<HasilSAW> hasil = dao.getHasilBySesi(idSesi);
            SwingUtilities.invokeLater(() -> {
                m.setRowCount(0);
                for (HasilSAW h : hasil) {
                    m.addRow(new Object[]{
                        h.getRanking(), 
                        h.getAlternatif().getKodeMakanan(), 
                        h.getAlternatif().getNamaPaket(),
                        h.getSkorFormatted()
                    });
                }
            });
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void printFormal(String title, DefaultTableModel model) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<html><head><style>");
            html.append("body { font-family: Arial, sans-serif; }");
            html.append("td, th { padding: 8px; font-size: 11px; }");
            html.append("th { font-size: 12px; }");
            html.append("</style></head><body style=\"margin: 30px;\">");
            
            // Kop Surat
            java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
            String imgSrc = (logoUrl != null) ? logoUrl.toString() : "";
            
            html.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
            html.append("<tr>");
            html.append("<td width=\"130\" align=\"center\">");
            if (!imgSrc.isEmpty()) html.append("<img src=\"").append(imgSrc).append("\" width=\"90\" height=\"90\">");
            html.append("</td>");
            html.append("<td align=\"center\">");
            html.append("<font size=\"6\"><b>AYAM BAKAR ASEP</b></font><br>");
            html.append("<font size=\"4\">Jl. Lestari No.23E, RT.4/RW.3, Kalisari, Kec. Ps. Rebo</font><br>");
            html.append("<font size=\"4\">Kota Jakarta Timur, DKI Jakarta 13790. Bpk. Asep : +62 812-3456-7890</font>");
            html.append("</td>");
            html.append("</tr>");
            html.append("</table>");
            
            html.append("<hr size=\"2\" color=\"black\">");
            html.append("<hr size=\"1\" color=\"black\" style=\"margin-top:-5px;\">");
            html.append("<br>");
            
            // Title
            html.append("<center><u><font size=\"5\"><b>").append(title).append("</b></font></u></center>");
            html.append("<br><br>");
            
            // Data Table
            html.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"6\" bordercolor=\"#000000\">");
            html.append("<tr bgcolor=\"#E2E8F0\">");
            for (int i = 0; i < model.getColumnCount(); i++) {
                html.append("<th>").append(model.getColumnName(i)).append("</th>");
            }
            html.append("</tr>");
            
            for (int r = 0; r < model.getRowCount(); r++) {
                html.append("<tr>");
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    html.append("<td align=\"center\">").append(val != null ? val.toString() : "").append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</table>");
            
            html.append("<br><br><br>");
            
            // Signature
            html.append("<table width=\"100%\" border=\"0\">");
            html.append("<tr><td width=\"70%\"></td><td align=\"center\">");
            html.append("<font size=\"4\">Jakarta, ").append(new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id","ID")).format(new java.util.Date())).append("</font><br><br><br><br><br>");
            html.append("<font size=\"4\"><b><u>Administrator Utama</u></b></font>");
            html.append("</td></tr></table>");
            
            html.append("</body></html>");
            
            JEditorPane pane = new JEditorPane("text/html", html.toString());
            pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            
            boolean printed = pane.print(null, null, true, null, null, true);
            if (printed) {
                JOptionPane.showMessageDialog(this, "Laporan berhasil dicetak!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
