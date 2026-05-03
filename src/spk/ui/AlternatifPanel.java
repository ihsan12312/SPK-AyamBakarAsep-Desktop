package spk.ui;

import spk.dao.AlternatifDAO;
import spk.dao.KriteriaDAO;
import spk.model.Alternatif;
import spk.model.Kriteria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

public class AlternatifPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Alternatif> altList;
    private List<Kriteria>   kritList;

    public AlternatifPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel hdr = pageHeader("Manajemen Paket Menu", "Tambah paket menu dan isi nilai per kriteria");
        JButton btnAdd = accentBtn("Tambah Paket");
        btnAdd.addActionListener(e -> showTambah());
        hdr.add(btnAdd, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        tblModel = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = styledTable(tblModel);

        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 16, 20));
        JLabel ctitle = new JLabel("Matriks Nilai Paket Menu");
        ctitle.setFont(fontBold(14)); ctitle.setForeground(TEXT_PRIMARY); ctitle.setBorder(new EmptyBorder(0,0,12,0));
        card.add(ctitle, BorderLayout.NORTH);
        card.add(new JScrollPane(table) {{ setBorder(null); }}, BorderLayout.CENTER);

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acts.setOpaque(false); acts.setBorder(new EmptyBorder(12,0,0,0));
        JButton bEdit  = outlineBtn("Edit Paket");
        JButton bNilai = successBtn("Isi Nilai");
        JButton bHapus = dangerBtn("Hapus");
        bEdit.addActionListener(e  -> showEdit());
        bNilai.addActionListener(e -> showNilai());
        bHapus.addActionListener(e -> doHapus());
        acts.add(bEdit); acts.add(bNilai); acts.add(bHapus);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(card, BorderLayout.CENTER); center.add(acts, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try { altList = new AlternatifDAO().getAllWithNilai(); kritList = new KriteriaDAO().getAll(); }
                catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                if (kritList == null) return;
                tblModel.setColumnCount(0);
                tblModel.addColumn("No"); tblModel.addColumn("Kode"); tblModel.addColumn("Nama Paket"); tblModel.addColumn("Harga");
                for (Kriteria k : kritList) tblModel.addColumn(k.getNamaKriteria());
                tblModel.setRowCount(0);
                if (altList != null) {
                    int i = 1;
                    for (Alternatif a : altList) {
                        Object[] row = new Object[4 + kritList.size()];
                        row[0] = i++; row[1] = a.getKodeMakanan(); row[2] = a.getNamaPaket(); row[3] = a.getHarga();
                        for (int j = 0; j < kritList.size(); j++) {
                            double v = a.getNilai(kritList.get(j).getIdKriteria());
                            row[4+j] = v == 0 ? "–" : String.format("%.0f", v);
                        }
                        tblModel.addRow(row);
                    }
                }
                if (table.getColumnModel().getColumnCount() > 0) table.getColumnModel().getColumn(0).setMaxWidth(50);
            }
        }.execute();
    }

    private void showTambah() {
        JDialog d = dialog("Tambah Paket Menu", 420, 360);
        JTextField tKode = tf(), tNama = tf(), tDesk = tf();
        JComboBox<String> cbHarga = new JComboBox<>(new String[]{"Sangat Murah", "Murah", "Rata-rata", "Mahal", "Sangat Mahal"});
        cbHarga.setFont(fontPlain(14)); cbHarga.setBackground(Color.WHITE);
        JPanel form = form(); 
        addRow(form,"Kode Makanan",tKode); addRow(form,"Nama Paket",tNama); 
        addRow(form,"Deskripsi",tDesk); addRow(form,"Kategori Harga",cbHarga);
        JButton save = accentBtn("Simpan");
        save.addActionListener(e -> {
            try {
                Alternatif a = new Alternatif(); 
                a.setKodeMakanan(tKode.getText().trim());
                a.setNamaPaket(tNama.getText().trim());
                a.setDeskripsi(tDesk.getText().trim());
                a.setHarga((String) cbHarga.getSelectedItem());
                new AlternatifDAO().insert(a); d.dispose(); refresh(); toast("Paket ditambahkan!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    private void showEdit() {
        int row = table.getSelectedRow(); if (row < 0) { toast("Pilih paket!"); return; }
        Alternatif a = altList.get(row);
        JDialog d = dialog("Edit Paket", 420, 360);
        JTextField tKode = tf(a.getKodeMakanan()), tNama = tf(a.getNamaPaket()), tDesk = tf(a.getDeskripsi() != null ? a.getDeskripsi() : "");
        JComboBox<String> cbHarga = new JComboBox<>(new String[]{"Sangat Murah", "Murah", "Rata-rata", "Mahal", "Sangat Mahal"});
        cbHarga.setFont(fontPlain(14)); cbHarga.setBackground(Color.WHITE);
        if (a.getHarga() != null) cbHarga.setSelectedItem(a.getHarga());
        JPanel form = form(); 
        addRow(form,"Kode Makanan",tKode); addRow(form,"Nama Paket",tNama); 
        addRow(form,"Deskripsi",tDesk); addRow(form,"Kategori Harga",cbHarga);
        JButton save = accentBtn("Perbarui");
        save.addActionListener(e -> {
            try {
                a.setKodeMakanan(tKode.getText().trim());
                a.setNamaPaket(tNama.getText().trim()); a.setDeskripsi(tDesk.getText().trim());
                a.setHarga((String) cbHarga.getSelectedItem());
                new AlternatifDAO().update(a); d.dispose(); refresh(); toast("Paket diperbarui!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    private void showNilai() {
        int row = table.getSelectedRow(); if (row < 0) { toast("Pilih paket!"); return; }
        Alternatif a = altList.get(row);
        if (kritList == null || kritList.isEmpty()) { toast("Belum ada kriteria!"); return; }
        JDialog d = dialog("Isi Nilai – " + a.getNamaPaket(), 440, 80 + kritList.size() * 52 + 80);
        JPanel form = form();
        Map<Integer, JTextField> inputs = new HashMap<>();
        for (Kriteria k : kritList) {
            double v = a.getNilai(k.getIdKriteria());
            JTextField tf = tf(v == 0 ? "" : String.format("%.0f", v));
            inputs.put(k.getIdKriteria(), tf);
            addRow(form, k.getNamaKriteria() + " (" + k.getSatuan() + ")", tf);
        }
        JButton save = accentBtn("Simpan Nilai");
        save.addActionListener(e -> {
            try {
                Map<Integer,Double> m = new HashMap<>();
                for (Map.Entry<Integer,JTextField> en : inputs.entrySet()) {
                    String val = en.getValue().getText().trim();
                    if (!val.isEmpty()) m.put(en.getKey(), Double.parseDouble(val));
                }
                new AlternatifDAO().saveNilaiBatch(a.getIdAlternatif(), m); d.dispose(); refresh(); toast("Nilai disimpan!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    private void doHapus() {
        int row = table.getSelectedRow(); if (row < 0) { toast("Pilih paket!"); return; }
        Alternatif a = altList.get(row);
        if (JOptionPane.showConfirmDialog(this,"Hapus '"+a.getNamaPaket()+"'?","Konfirmasi",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { new AlternatifDAO().delete(a.getIdAlternatif()); refresh(); toast("Dihapus!"); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }
}
