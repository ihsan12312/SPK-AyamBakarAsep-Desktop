package spk.dao;

import spk.config.DBConnection;
import spk.model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO.java
 * Data Access Object untuk tabel tb_admin.
 */
public class AdminDAO {
private final Connection conn;

    public AdminDAO() {
    this.conn = DBConnection.getInstance().getConnection();
    }

    public Admin login(String username, String password) throws SQLException {
        // Gunakan BINARY untuk password agar case-sensitive (opsional), 
        // tapi username biasanya case-insensitive di MySQL.
        String sql = "SELECT * FROM tb_admin WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password); // Jangan trim password agar sesuai input user
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT id_admin FROM tb_admin WHERE LOWER(username) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int insert(Admin admin) throws SQLException {
        String sql = "INSERT INTO tb_admin(username, password, nama_lengkap, no_hp, alamat, nama_ibu_kandung) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getNamaLengkap());
            ps.setString(4, admin.getNoHp());
            ps.setString(5, admin.getAlamat());
            ps.setString(6, admin.getNamaIbuKandung());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) return g.getInt(1);
            }
        }
        return -1;
    }

    public List<Admin> getAll() throws SQLException {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_admin ORDER BY id_admin";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void resetPassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE tb_admin SET password = ? WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public boolean isRecoveryValid(String username, String namaIbuKandung) throws SQLException {
        String sql = "SELECT id_admin FROM tb_admin WHERE LOWER(username) = LOWER(?) AND LOWER(TRIM(nama_ibu_kandung)) = LOWER(TRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, namaIbuKandung.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        return new Admin(
            rs.getInt("id_admin"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("nama_lengkap"),
            rs.getString("no_hp"),
            rs.getString("alamat"),
            rs.getString("nama_ibu_kandung")
        );
    }
}
