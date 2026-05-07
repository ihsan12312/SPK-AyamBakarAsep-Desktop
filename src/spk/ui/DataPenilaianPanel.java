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

/**
 * DataPenilaianPanel – Input penilaian paket menu per kriteria skala 1-5
 */
public class DataPenilaianPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Alternatif> altList;
    private List<Kriteria> kritList;

    private JComboBox<Alternatif> cbMenu;
    private Map<Integer, JComboBox<Integer>> inputMap = new LinkedHashMap<>();
    private JPanel kritForm;
    private MainFrame mainFrame;

    private static final String[] SCALE_LABELS = {"1 – Sangat Kurang", "2 – Kurang", "3 – Cukup", "4 – Baik", "5 – Sangat Baik"};

    public DataPenilaianPanel(MainFrame frame) {
        this.mainFrame = frame;
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Data Penilaian", "Input nilai penilaian setiap menu paket per kriteria (skala 1-5)");
        add(hdr, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────
        JPanel formCard = shadowCard();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel fTitle = new JLabel("Form Penilaian Menu Paket");
        fTitle.setFont(fontBold(14)); fTitle.setForeground(TEXT_PRIMARY); fTitle.setBorder(new EmptyBorder(0,0,12,0));

        // Dropdown pilih menu
        cbMenu = new JComboBox<>(); cbMenu.setFont(fontPlain(13));
        cbMenu.setPreferredSize(new Dimension(260, 36));
        cbMenu.setBackground(Color.WHITE);
        cbMenu.setForeground(TEXT_PRIMARY);
        cbMenu.addActionListener(e -> loadNilai());

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);
        topRow.add(new JLabel("Pilih Menu Paket:") {{ setFont(fontBold(12)); setForeground(TEXT_SECONDARY); }});
        topRow.add(cbMenu);

        // Dynamic kriteria inputs
        kritForm = new JPanel(new GridLayout(0, 2, 12, 10));
        kritForm.setOpaque(false);
        kritForm.setBorder(new EmptyBorder(12, 0, 0, 0));

        JScrollPane formScroll = new JScrollPane(kritForm);
        formScroll.setBorder(null);
        formScroll.setPreferredSize(new Dimension(0, 180));

        // Buttons
        JButton btnSimpan = accentBtn("Simpan Nilai");
        JButton btnHapus  = dangerBtn("Hapus Nilai");
        JButton btnBaru   = successBtn("Reset");
        btnSimpan.addActionListener(e -> doSimpan());
        btnHapus.addActionListener(e  -> doHapus());
        btnBaru.addActionListener(e   -> loadNilai());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnSimpan); btnPanel.add(btnHapus); btnPanel.add(btnBaru);

        JPanel formInner = new JPanel(new BorderLayout(0, 8));
        formInner.setOpaque(false);
        formInner.add(topRow, BorderLayout.NORTH);
        formInner.add(formScroll, BorderLayout.CENTER);
        formInner.add(btnPanel, BorderLayout.SOUTH);

        formCard.add(fTitle, BorderLayout.NORTH);
        formCard.add(formInner, BorderLayout.CENTER);
        formCard.setPreferredSize(new Dimension(0, 320));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // ── Table – ringkasan penilaian ─────────────────────────────
        tblModel = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tblModel);
        table.setFont(fontPlain(13)); table.setRowHeight(36);
        table.setBackground(Color.WHITE); table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(0xE0E7FF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(0xF1F5F9)); table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.getTableHeader().setFont(fontBold(11));
        table.getTableHeader().setBackground(new Color(0xF8FAFC));
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(0xE2E8F0)));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setFont(fontPlain(13)); setBorder(new EmptyBorder(0, 12, 0, 12));
                setBackground(sel ? new Color(0xE0E7FF) : (r%2==0 ? Color.WHITE : new Color(0xFAFBFE)));
                setForeground(TEXT_PRIMARY); return this;
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && altList != null && row < altList.size()) {
                cbMenu.setSelectedItem(altList.get(row));
            }
        });

        JPanel tableCard = shadowCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel tTitle = new JLabel("Matriks Penilaian");
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

    public void refresh() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try {
                    altList  = new AlternatifDAO().getAllWithNilai();
                    kritList = new KriteriaDAO().getAll();
                } catch (SQLException e) { e.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                // Update dropdown
                cbMenu.removeAllItems();
                if (altList != null) for (Alternatif a : altList) cbMenu.addItem(a);

                // Build kriteria form inputs
                kritForm.removeAll();
                inputMap.clear();
                if (kritList != null) {
                    for (Kriteria k : kritList) {
                        JLabel lbl = new JLabel(k.getNamaKriteria() + ":"); lbl.setFont(fontBold(12)); lbl.setForeground(TEXT_SECONDARY);
                        JComboBox<Integer> cb = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
                        cb.setFont(fontPlain(13));
                        cb.setBackground(Color.WHITE);
                        cb.setForeground(TEXT_PRIMARY);
                        cb.setToolTipText("1=Sangat Kurang, 2=Kurang, 3=Cukup, 4=Baik, 5=Sangat Baik");
                        kritForm.add(lbl); kritForm.add(cb);
                        inputMap.put(k.getIdKriteria(), cb);
                    }
                }
                kritForm.revalidate(); kritForm.repaint();

                // Update table
                tblModel.setColumnCount(0);
                tblModel.addColumn("No"); tblModel.addColumn("Nama Menu");
                if (kritList != null) for (Kriteria k : kritList) tblModel.addColumn(k.getNamaKriteria());
                tblModel.setRowCount(0);
                if (altList != null && kritList != null) {
                    int i = 1;
                    for (Alternatif a : altList) {
                        Object[] row = new Object[2 + kritList.size()];
                        row[0] = i++; row[1] = a.getNamaPaket();
                        for (int j = 0; j < kritList.size(); j++) {
                            double v = a.getNilai(kritList.get(j).getIdKriteria());
                            row[2+j] = v == 0 ? "–" : spk.ui.UITheme.formatDesimal(v);
                        }
                        tblModel.addRow(row);
                    }
                }
                if (tblModel.getColumnCount() > 0) table.getColumnModel().getColumn(0).setMaxWidth(50);

                loadNilai();
            }
        }.execute();
    }

    private void loadNilai() {
        Alternatif a = (Alternatif) cbMenu.getSelectedItem();
        if (a == null || kritList == null) return;
        for (Kriteria k : kritList) {
            JComboBox<Integer> cb = inputMap.get(k.getIdKriteria());
            if (cb != null) {
                double v = a.getNilai(k.getIdKriteria());
                int sel = (v >= 1 && v <= 5) ? (int)v : 1;
                cb.setSelectedItem(sel);
            }
        }
    }

    private void doSimpan() {
        Alternatif a = (Alternatif) cbMenu.getSelectedItem();
        if (a == null) { toast("Pilih menu terlebih dahulu!"); return; }
        
        Map<Integer, Double> m = new HashMap<>();
        for (Kriteria k : kritList) {
            JComboBox<Integer> cb = inputMap.get(k.getIdKriteria());
            if (cb != null) m.put(k.getIdKriteria(), (double)(int)cb.getSelectedItem());
        }
        
        new SwingWorker<Boolean, Void>() {
            String errorMsg;
            @Override protected Boolean doInBackground() throws Exception {
                try {
                    new AlternatifDAO().saveNilaiBatch(a.getIdAlternatif(), m);
                    return true;
                } catch (SQLException ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        toast("Penilaian '" + a.getNamaPaket() + "' berhasil disimpan!");
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(DataPenilaianPanel.this, 
                            "Gagal menyimpan: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DataPenilaianPanel.this, 
                        "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void doHapus() {
        Alternatif a = (Alternatif) cbMenu.getSelectedItem();
        if (a == null) { toast("Pilih menu paket terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus semua nilai penilaian untuk '" + a.getNamaPaket() + "'?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                new AlternatifDAO().saveNilaiBatch(a.getIdAlternatif(), new HashMap<>());
                refresh(); toast("Nilai dihapus!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }
}
