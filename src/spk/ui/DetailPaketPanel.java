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

public class DetailPaketPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Alternatif> altList;

    public DetailPaketPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel hdr = pageHeader("Detail Paket Menu", "Informasi lengkap mengenai paket menu makanan");
        add(hdr, BorderLayout.NORTH);

        tblModel = new DefaultTableModel() { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tblModel.addColumn("No");
        tblModel.addColumn("Kode Makanan");
        tblModel.addColumn("Nama Paket");
        tblModel.addColumn("Kategori Harga");
        tblModel.addColumn("Deskripsi");
        
        table = styledTable(tblModel);

        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 16, 20));
        JLabel ctitle = new JLabel("Daftar Detail Paket Menu");
        ctitle.setFont(fontBold(14)); ctitle.setForeground(TEXT_PRIMARY); ctitle.setBorder(new EmptyBorder(0,0,12,0));
        card.add(ctitle, BorderLayout.NORTH);
        card.add(new JScrollPane(table) {{ setBorder(null); }}, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout()); center.setOpaque(false);
        center.add(card, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try { altList = new AlternatifDAO().getAll(); }
                catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                tblModel.setRowCount(0);
                if (altList != null) {
                    int i = 1;
                    for (Alternatif a : altList) {
                        tblModel.addRow(new Object[]{
                            i++, a.getKodeMakanan(), a.getNamaPaket(), a.getHarga(), a.getDeskripsi()
                        });
                    }
                }
                if (table.getColumnModel().getColumnCount() > 0) {
                    table.getColumnModel().getColumn(0).setMaxWidth(50);
                    table.getColumnModel().getColumn(1).setPreferredWidth(100);
                    table.getColumnModel().getColumn(2).setPreferredWidth(150);
                    table.getColumnModel().getColumn(3).setPreferredWidth(120);
                    table.getColumnModel().getColumn(4).setPreferredWidth(300);
                }
            }
        }.execute();
    }
}
