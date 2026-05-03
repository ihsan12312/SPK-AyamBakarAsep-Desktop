package spk.ui;

import spk.dao.AdminDAO;
import spk.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import static spk.ui.UITheme.*;
import static spk.ui.DashboardPanel.*;
import static spk.ui.KriteriaPanel.*;

public class AdminPanel extends JPanel {

    private DefaultTableModel tblModel;
    private JTable table;
    private List<Admin> adminList;

    public AdminPanel() {
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel hdr = pageHeader("Data Admin", "Daftar administrator sistem SPK");
        add(hdr, BorderLayout.NORTH);

        tblModel = new DefaultTableModel() { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tblModel.addColumn("ID");
        tblModel.addColumn("Username");
        tblModel.addColumn("Nama Lengkap");
        
        table = styledTable(tblModel);

        JPanel card = shadowCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 16, 20));
        JLabel ctitle = new JLabel("Daftar Administrator");
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
                try { adminList = new AdminDAO().getAll(); }
                catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
            @Override protected void done() {
                tblModel.setRowCount(0);
                if (adminList != null) {
                    for (Admin a : adminList) {
                        tblModel.addRow(new Object[]{
                            a.getIdAdmin(), a.getUsername(), a.getNamaLengkap()
                        });
                    }
                }
                if (table.getColumnModel().getColumnCount() > 0) {
                    table.getColumnModel().getColumn(0).setMaxWidth(80);
                }
            }
        }.execute();
    }
}
