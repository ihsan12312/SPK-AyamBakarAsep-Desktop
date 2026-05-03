package spk.ui;

import spk.dao.AlternatifDAO;
import spk.model.Alternatif;

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
 * DataMenuPanel – Input, Edit, Hapus data menu paket makanan.
 * Kolom: Kode Menu, Nama Paket, Deskripsi
 */
public class DataMenuPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Alternatif> altList;

    // Form fields
    private JTextField tfKode, tfNama, tfDesk;
    private JButton btnSimpan, btnEdit, btnHapus, btnBaru;

    private MainFrame mainFrame;

    public DataMenuPanel(MainFrame frame) {
        this.mainFrame = frame;
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Data Menu Paket", "Kelola data paket menu makanan");
        add(hdr, BorderLayout.NORTH);

        // ── Form Input ────────────────────────────────────────────
        JPanel formCard = shadowCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel fTitle = new JLabel("Form Input Data Menu");
        fTitle.setFont(fontBold(14)); fTitle.setForeground(TEXT_PRIMARY);

        tfKode = new JTextField(); tfKode.setFont(fontPlain(13)); tfKode.setPreferredSize(new Dimension(200, 36));
        tfNama = new JTextField(); tfNama.setFont(fontPlain(13)); tfNama.setPreferredSize(new Dimension(200, 36));
        tfDesk = new JTextField(); tfDesk.setFont(fontPlain(13)); tfDesk.setPreferredSize(new Dimension(200, 36));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; formCard.add(fTitle, gc);
        gc.gridwidth = 1;
        addFormRow(formCard, gc, 1, "Kode Menu:", tfKode);
        addFormRow(formCard, gc, 2, "Nama Paket:", tfNama);
        addFormRow(formCard, gc, 3, "Deskripsi:", tfDesk);

        // Buttons
        btnSimpan = accentBtn("Simpan");
        btnEdit   = outlineBtn("Edit");
        btnHapus  = dangerBtn("Hapus");
        btnBaru   = successBtn("Baru");

        btnSimpan.addActionListener(e -> doSimpan());
        btnEdit.addActionListener(e   -> doEdit());
        btnHapus.addActionListener(e  -> doHapus());
        btnBaru.addActionListener(e   -> clearForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnSimpan); btnPanel.add(btnEdit); btnPanel.add(btnHapus); btnPanel.add(btnBaru);

        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.insets = new Insets(16, 8, 6, 8);
        formCard.add(btnPanel, gc);

        formCard.setPreferredSize(new Dimension(0, 240));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        // ── Table ──────────────────────────────────────────────────
        String[] cols = {"No", "Kode Menu", "Nama Paket", "Deskripsi"};
        tblModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = styledTable(tblModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        JPanel tableCard = shadowCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel tTitle = new JLabel("Daftar Paket Menu");
        tTitle.setFont(fontBold(14)); tTitle.setForeground(TEXT_PRIMARY); tTitle.setBorder(new EmptyBorder(0,0,10,0));
        tableCard.add(tTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table) {{ setBorder(null); }}, BorderLayout.CENTER);

        // ── Center layout ─────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(formCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, int row, String label, JComponent comp) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.insets = new Insets(6, 8, 6, 8);
        JLabel lbl = new JLabel(label); lbl.setFont(fontBold(12)); lbl.setForeground(TEXT_SECONDARY);
        p.add(lbl, gc);
        gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;
        p.add(comp, gc);
        gc.fill = GridBagConstraints.NONE; gc.weightx = 0;
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try { altList = new AlternatifDAO().getAll(); } catch (SQLException e) { e.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                tblModel.setRowCount(0);
                if (altList != null) {
                    int i = 1;
                    for (Alternatif a : altList)
                        tblModel.addRow(new Object[]{i++, a.getKodeMakanan(), a.getNamaPaket(), a.getDeskripsi()});
                }
            }
        }.execute();
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row >= 0 && altList != null && row < altList.size()) {
            Alternatif a = altList.get(row);
            tfKode.setText(a.getKodeMakanan() != null ? a.getKodeMakanan() : "");
            tfNama.setText(a.getNamaPaket() != null ? a.getNamaPaket() : "");
            tfDesk.setText(a.getDeskripsi() != null ? a.getDeskripsi() : "");
        }
    }

    private void doSimpan() {
        String kode = tfKode.getText().trim();
        String nama = tfNama.getText().trim();
        String desk = tfDesk.getText().trim();
        if (nama.isEmpty()) { toast("Nama paket tidak boleh kosong!"); return; }
        try {
            Alternatif a = new Alternatif();
            a.setKodeMakanan(kode);
            a.setNamaPaket(nama);
            a.setDeskripsi(desk);
            a.setHarga("Rata-rata");
            new AlternatifDAO().insert(a);
            clearForm(); refresh(); toast("Paket menu berhasil disimpan!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Pilih baris terlebih dahulu!"); return; }
        Alternatif a = altList.get(row);
        a.setKodeMakanan(tfKode.getText().trim());
        a.setNamaPaket(tfNama.getText().trim());
        a.setDeskripsi(tfDesk.getText().trim());
        try {
            new AlternatifDAO().update(a); refresh(); toast("Data berhasil diperbarui!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void doHapus() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Pilih baris terlebih dahulu!"); return; }
        Alternatif a = altList.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Hapus paket menu '" + a.getNamaPaket() + "'?\nData penilaian terkait menu ini juga akan dihapus.", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                String errorMsg;
                @Override protected Boolean doInBackground() throws Exception {
                    try {
                        new AlternatifDAO().delete(a.getIdAlternatif());
                        return true;
                    } catch (SQLException ex) {
                        errorMsg = ex.getMessage();
                        return false;
                    }
                }
                @Override protected void done() {
                    try {
                        if (get()) {
                            clearForm();
                            refresh();
                            toast("Paket menu berhasil dihapus!");
                        } else {
                            JOptionPane.showMessageDialog(DataMenuPanel.this, 
                                "Gagal menghapus: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DataMenuPanel.this, 
                            "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void clearForm() {
        tfKode.setText(""); tfNama.setText(""); tfDesk.setText("");
        table.clearSelection();
    }
}
