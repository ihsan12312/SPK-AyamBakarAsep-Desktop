package spk.ui;

import spk.dao.KriteriaDAO;
import spk.model.Kriteria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

/**
 * DataKriteriaPanel – Kelola kriteria dengan Kode, Nama, Bobot, Tipe
 */
public class DataKriteriaPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Kriteria> list;

    private JTextField tfNama, tfBobot;
    private JComboBox<String> cbTipe;
    private JLabel lblTotal;

    private MainFrame mainFrame;

    public DataKriteriaPanel(MainFrame frame) {
        this.mainFrame = frame;
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Data Kriteria", "Kelola kriteria penilaian SAW (total bobot harus = 100%)");
        add(hdr, BorderLayout.NORTH);

        // ── Form ──────────────────────────────────────────────────
        JPanel formCard = shadowCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel fTitle = new JLabel("Form Input Kriteria");
        fTitle.setFont(fontBold(14)); fTitle.setForeground(TEXT_PRIMARY);

        tfNama  = new JTextField(); tfNama.setFont(fontPlain(13));  tfNama.setPreferredSize(new Dimension(200,36));
        tfBobot = new JTextField(); tfBobot.setFont(fontPlain(13)); tfBobot.setPreferredSize(new Dimension(200,36));
        cbTipe  = new JComboBox<>(new String[]{"benefit","cost"});
        cbTipe.setFont(fontPlain(13)); cbTipe.setPreferredSize(new Dimension(200,36));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8); gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; formCard.add(fTitle, gc);
        gc.gridwidth = 1;
        addFRow(formCard, gc, 1, "Nama Kriteria:", tfNama);
        addFRow(formCard, gc, 2, "Bobot (%):", tfBobot);
        addFRow(formCard, gc, 3, "Tipe:", cbTipe);

        // Total bobot label
        lblTotal = new JLabel("Total Bobot: 0%");
        lblTotal.setFont(fontBold(12));

        JButton btnSimpan = accentBtn("Simpan");
        JButton btnEdit   = outlineBtn("Edit");
        JButton btnHapus  = dangerBtn("Hapus");
        JButton btnBaru   = successBtn("Baru");
        btnSimpan.addActionListener(e -> doSimpan());
        btnEdit.addActionListener(e   -> doEdit());
        btnHapus.addActionListener(e  -> doHapus());
        btnBaru.addActionListener(e   -> clearForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnSimpan); btnPanel.add(btnEdit); btnPanel.add(btnHapus); btnPanel.add(btnBaru);

        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 1; gc.insets = new Insets(12, 8, 4, 8);
        formCard.add(lblTotal, gc);
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2; gc.insets = new Insets(4, 8, 6, 8);
        formCard.add(btnPanel, gc);
        formCard.setPreferredSize(new Dimension(0, 270));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));

        // ── Table ─────────────────────────────────────────────────
        String[] cols = {"ID","Nama Kriteria","Tipe","Bobot (%)"};
        tblModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = styledTable(tblModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        JPanel tableCard = shadowCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel tTitle = new JLabel("Daftar Kriteria");
        tTitle.setFont(fontBold(14)); tTitle.setForeground(TEXT_PRIMARY); tTitle.setBorder(new EmptyBorder(0,0,10,0));
        tableCard.add(tTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table) {{ setBorder(null); }}, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(formCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private void addFRow(JPanel p, GridBagConstraints gc, int row, String label, JComponent comp) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        JLabel lbl = new JLabel(label); lbl.setFont(fontBold(12)); lbl.setForeground(TEXT_SECONDARY);
        p.add(lbl, gc);
        gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;
        p.add(comp, gc);
        gc.fill = GridBagConstraints.NONE; gc.weightx = 0;
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            double total = 0;
            @Override protected Void doInBackground() {
                try {
                    KriteriaDAO dao = new KriteriaDAO();
                    list = dao.getAll(); total = dao.getTotalBobot();
                } catch (SQLException e) { e.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                tblModel.setRowCount(0);
                if (list != null) for (Kriteria k : list)
                    tblModel.addRow(new Object[]{k.getIdKriteria(), k.getNamaKriteria(),
                        k.isBenefit() ? "Benefit ↑" : "Cost ↓", String.format("%.2f", k.getBobot())});
                boolean valid = Math.abs(total - 100) < 0.01;
                lblTotal.setText("Total Bobot: " + String.format("%.2f", total) + "%  " + (valid ? "✓ Valid" : "⚠ Belum 100%"));
                lblTotal.setForeground(valid ? GREEN_700 : RED_600);
            }
        }.execute();
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row >= 0 && list != null && row < list.size()) {
            Kriteria k = list.get(row);
            tfNama.setText(k.getNamaKriteria());
            tfBobot.setText(String.format("%.2f", k.getBobot()));
            cbTipe.setSelectedItem(k.getJenis());
        }
    }

    private void doSimpan() {
        String nama = tfNama.getText().trim();
        String bobotStr = tfBobot.getText().trim();
        if (nama.isEmpty() || bobotStr.isEmpty()) { toast("Nama dan Bobot harus diisi!"); return; }
        try {
            Kriteria k = new Kriteria();
            k.setNamaKriteria(nama);
            k.setBobot(Double.parseDouble(bobotStr));
            k.setJenis((String) cbTipe.getSelectedItem());
            k.setSatuan("Poin");
            new KriteriaDAO().insert(k);
            clearForm(); refresh(); toast("Kriteria berhasil disimpan!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Pilih baris terlebih dahulu!"); return; }
        Kriteria k = list.get(row);
        try {
            k.setNamaKriteria(tfNama.getText().trim());
            k.setBobot(Double.parseDouble(tfBobot.getText().trim()));
            k.setJenis((String) cbTipe.getSelectedItem());
            new KriteriaDAO().update(k); refresh(); toast("Kriteria diperbarui!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void doHapus() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Pilih baris terlebih dahulu!"); return; }
        Kriteria k = list.get(row);
        if (JOptionPane.showConfirmDialog(this, "Hapus '" + k.getNamaKriteria() + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { new KriteriaDAO().delete(k.getIdKriteria()); clearForm(); refresh(); toast("Dihapus!"); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }

    private void clearForm() {
        tfNama.setText(""); tfBobot.setText(""); cbTipe.setSelectedIndex(0);
        table.clearSelection();
    }
}
