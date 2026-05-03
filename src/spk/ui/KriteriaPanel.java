package spk.ui;

import spk.dao.KriteriaDAO;
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

public class KriteriaPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private JLabel lblBobot;
    private List<Kriteria> list;

    public KriteriaPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel hdr = pageHeader("Manajemen Kriteria", "Kelola kriteria SAW dan atur bobot (total harus = 100%)");
        JButton btnAdd = accentBtn("Tambah Kriteria");
        btnAdd.addActionListener(e -> showTambah());
        hdr.add(btnAdd, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // Bobot status bar
        lblBobot = new JLabel();
        lblBobot.setFont(fontBold(12));
        JPanel bobotBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bobotBar.setOpaque(false); bobotBar.setBorder(new EmptyBorder(0, 0, 12, 0));
        bobotBar.add(lblBobot);

        // Table
        String[] cols = {"ID","Nama Kriteria","Jenis","Bobot (%)","Satuan","Keterangan"};
        tblModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = styledTable(tblModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(100);

        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel ctitle = new JLabel("Daftar Kriteria");
        ctitle.setFont(fontBold(14)); ctitle.setForeground(TEXT_PRIMARY);
        ctitle.setBorder(new EmptyBorder(0,0,12,0));

        JScrollPane sp = new JScrollPane(table); sp.setBorder(null);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        card.add(ctitle, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        // Action buttons
        JPanel acts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acts.setOpaque(false); acts.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton bEdit  = outlineBtn("Edit");
        JButton bHapus = dangerBtn("Hapus");
        JButton bBobot = successBtn("Atur Bobot");
        bEdit.addActionListener(e  -> showEdit());
        bHapus.addActionListener(e -> doHapus());
        bBobot.addActionListener(e -> showBobot());
        acts.add(bEdit); acts.add(bHapus); acts.add(bBobot);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(bobotBar, BorderLayout.NORTH);
        center.add(card, BorderLayout.CENTER);
        center.add(acts, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            double tb = 0;
            @Override protected Void doInBackground() {
                try { KriteriaDAO dao = new KriteriaDAO(); list = dao.getAll(); tb = dao.getTotalBobot(); }
                catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                tblModel.setRowCount(0);
                if (list != null) {
                    int i = 1;
                    for (Kriteria k : list)
                        tblModel.addRow(new Object[]{i++, k.getNamaKriteria(),
                            k.isBenefit() ? "Benefit ↑" : "Cost ↓",
                            String.format("%.2f", k.getBobot()), k.getSatuan(),
                            k.getKeterangan() != null ? k.getKeterangan() : "–"});
                }
                boolean valid = Math.abs(tb - 100) < 0.01;
                lblBobot.setText("  Total Bobot: " + String.format("%.2f", tb) + "%   " + (valid ? "Valid" : "Belum 100%"));
                lblBobot.setForeground(valid ? GREEN_700 : RED_600);
            }
        }.execute();
    }

    private void showTambah() {
        JDialog d = dialog("Tambah Kriteria", 440, 370);
        JTextField tNama = tf(), tBobot = tf("0"), tSatuan = tf("Poin"), tKet = tf();
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"benefit","cost"});
        cbJenis.setFont(fontPlain(13));
        JPanel form = form(); addRow(form,"Nama Kriteria",tNama); addRow(form,"Jenis",cbJenis);
        addRow(form,"Bobot (%)",tBobot); addRow(form,"Satuan",tSatuan); addRow(form,"Keterangan",tKet);
        JButton save = accentBtn("Simpan");
        save.addActionListener(e -> {
            try {
                Kriteria k = new Kriteria(); k.setNamaKriteria(tNama.getText().trim());
                k.setJenis((String)cbJenis.getSelectedItem()); k.setBobot(Double.parseDouble(tBobot.getText().trim()));
                k.setSatuan(tSatuan.getText().trim()); k.setKeterangan(tKet.getText().trim());
                new KriteriaDAO().insert(k); d.dispose(); refresh();
                toast("Kriteria berhasil ditambahkan!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    private void showEdit() {
        int row = table.getSelectedRow(); if (row < 0) { toast("Pilih kriteria terlebih dahulu!"); return; }
        Kriteria k = list.get(row);
        JDialog d = dialog("Edit Kriteria", 440, 370);
        JTextField tNama = tf(k.getNamaKriteria()), tBobot = tf(String.valueOf(k.getBobot()));
        JTextField tSatuan = tf(k.getSatuan()), tKet = tf(k.getKeterangan() != null ? k.getKeterangan() : "");
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"benefit","cost"});
        cbJenis.setSelectedItem(k.getJenis()); cbJenis.setFont(fontPlain(13));
        JPanel form = form(); addRow(form,"Nama Kriteria",tNama); addRow(form,"Jenis",cbJenis);
        addRow(form,"Bobot (%)",tBobot); addRow(form,"Satuan",tSatuan); addRow(form,"Keterangan",tKet);
        JButton save = accentBtn("Perbarui");
        save.addActionListener(e -> {
            try {
                k.setNamaKriteria(tNama.getText().trim()); k.setJenis((String)cbJenis.getSelectedItem());
                k.setBobot(Double.parseDouble(tBobot.getText().trim()));
                k.setSatuan(tSatuan.getText().trim()); k.setKeterangan(tKet.getText().trim());
                new KriteriaDAO().update(k); d.dispose(); refresh(); toast("Kriteria diperbarui!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    private void doHapus() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Pilih kriteria!"); return; }
        Kriteria k = list.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Hapus kriteria '" + k.getNamaKriteria() + "'?\nData penilaian yang menggunakan kriteria ini akan ikut terpengaruh.", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                String errorMsg;
                @Override protected Boolean doInBackground() throws Exception {
                    try {
                        new KriteriaDAO().delete(k.getIdKriteria());
                        return true;
                    } catch (SQLException ex) {
                        errorMsg = ex.getMessage();
                        return false;
                    }
                }
                @Override protected void done() {
                    try {
                        if (get()) {
                            refresh();
                            toast("Kriteria berhasil dihapus!");
                        } else {
                            JOptionPane.showMessageDialog(KriteriaPanel.this, 
                                "Gagal menghapus: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(KriteriaPanel.this, 
                            "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void showBobot() {
        if (list == null || list.isEmpty()) return;
        JDialog d = dialog("Atur Bobot Kriteria", 400, 60 + list.size() * 52 + 80);
        JPanel form = form();
        Map<Integer, JTextField> inputs = new HashMap<>();
        for (Kriteria k : list) {
            JTextField tf = tf(String.format("%.2f", k.getBobot()));
            inputs.put(k.getIdKriteria(), tf);
            addRow(form, k.getNamaKriteria(), tf);
        }
        JButton save = accentBtn("Simpan Bobot");
        save.addActionListener(e -> {
            try {
                Map<Integer,Double> bobotMap = new HashMap<>();
                for (Map.Entry<Integer,JTextField> en : inputs.entrySet())
                    bobotMap.put(en.getKey(), Double.parseDouble(en.getValue().getText().trim()));
                new KriteriaDAO().updateBobotBatch(bobotMap); d.dispose(); refresh(); toast("Bobot disimpan!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, ex.getMessage()); }
        });
        showDialog(d, form, save);
    }

    // Helpers
    static JPanel pageHeader(String title, String sub) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false); p.setBorder(new EmptyBorder(0,0,20,0));
        JPanel l = new JPanel(new GridLayout(2,1,0,4)); l.setOpaque(false);
        JLabel t = new JLabel(title); t.setFont(fontBold(20)); t.setForeground(TEXT_PRIMARY);
        JLabel s = new JLabel(sub);   s.setFont(fontPlain(12)); s.setForeground(TEXT_SECONDARY);
        l.add(t); l.add(s); p.add(l, BorderLayout.WEST); return p;
    }
    static JButton accentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hv; { addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){hv=true;repaint();} public void mouseExited(java.awt.event.MouseEvent e){hv=false;repaint();}}); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g; UITheme.polish(g2);
                g2.setColor(hv ? PRIMARY_700 : PRIMARY_600); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setFont(getFont()); g2.setColor(Color.WHITE);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setFont(fontBold(13)); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 20, 9, 20)); return b;
    }
    static JButton outlineBtn(String t) { return colorBtn(t, INDIGO_700, INDIGO_50); }
    static JButton dangerBtn(String t)  { return colorBtn(t, ROSE_700, ROSE_100); }
    static JButton successBtn(String t) { return colorBtn(t, GREEN_700, GREEN_100); }
    private static JButton colorBtn(String t, Color fg, Color bg) {
        JButton b = new JButton(t); b.setFont(fontBold(12)); b.setForeground(fg); b.setBackground(bg);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fg,1,true), new EmptyBorder(6,14,6,14)));
        b.setContentAreaFilled(true); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    static JTextField tf(String v) {
        JTextField tf = new JTextField(v); tf.setFont(fontPlain(13));
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1,true), new EmptyBorder(7,10,7,10)));
        return tf;
    }
    static JTextField tf() { return tf(""); }
    static JPanel form() { JPanel p = new JPanel(new GridLayout(0,2,10,12)); p.setOpaque(true); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(20,20,12,20)); return p; }
    static void addRow(JPanel p, String label, JComponent c) {
        JLabel l = new JLabel(label); l.setFont(fontBold(12)); l.setForeground(TEXT_SECONDARY); p.add(l); p.add(c);
    }
    static JDialog dialog(String title, int w, int h) {
        JDialog d = new JDialog((Frame)null, title, true); d.setSize(w,h); d.setLocationRelativeTo(null); return d;
    }
    static void showDialog(JDialog d, JPanel form, JButton save) {
        JPanel fp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12)); fp.setBackground(new Color(0xF8FAFC));
        fp.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER)); fp.add(save);
        d.setLayout(new BorderLayout());
        d.add(new JScrollPane(form) {{ setBorder(null); }}, BorderLayout.CENTER);
        d.add(fp, BorderLayout.SOUTH); d.setVisible(true);
    }
    static void toast(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
