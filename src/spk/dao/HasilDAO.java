package spk.dao;

import spk.config.DBConnection;
import spk.model.Alternatif;
import spk.model.HasilSAW;
import java.sql.*;
import java.util.*;

/**
 * HasilDAO.java
 * Data Access Object untuk tabel tb_sesi dan tb_hasil.
 * Operasi: Simpan hasil SAW per sesi + riwayat perhitungan.

 */
public class HasilDAO {
    private final Connection conn;
    public HasilDAO() { this.conn = DBConnection.getInstance().getConnection(); }

    public int simpanHasil(List<HasilSAW> hasilList, String catatan) throws SQLException {
        int idSesi = -1;
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tb_sesi(catatan) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, catatan != null ? catatan : "Perhitungan SAW");
                ps.executeUpdate();
                try (ResultSet g = ps.getGeneratedKeys()) { if (g.next()) idSesi = g.getInt(1); }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tb_hasil(id_sesi,id_alternatif,skor_akhir,ranking) VALUES(?,?,?,?)")) {
                for (HasilSAW h : hasilList) {
                    ps.setInt(1,idSesi); ps.setInt(2,h.getAlternatif().getIdAlternatif());
                    ps.setDouble(3,h.getSkorAkhir()); ps.setInt(4,h.getRanking()); ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) { conn.rollback(); throw e; }
        finally { conn.setAutoCommit(true); }
        return idSesi;
    }

    public List<Object[]> getRiwayatSesi(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.id_sesi,s.catatan,s.created_at,a.nama_paket,h.skor_akhir " +
                     "FROM tb_sesi s JOIN tb_hasil h ON s.id_sesi=h.id_sesi AND h.ranking=1 " +
                     "JOIN tb_alternatif a ON h.id_alternatif=a.id_alternatif " +
                     "ORDER BY s.created_at DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{
                    rs.getInt("id_sesi"), rs.getString("catatan"),
                    rs.getTimestamp("created_at"), rs.getString("nama_paket"),
                    rs.getDouble("skor_akhir")
                });
            }
        }
        return list;
    }

    public int deleteSesi(int idSesi) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_sesi WHERE id_sesi=?")) {
            ps.setInt(1, idSesi); return ps.executeUpdate();
        }
    }

    /** Ambil id_sesi terbaru dari database */
    public int getIdSesiTerakhir() throws SQLException {
        String sql = "SELECT id_sesi FROM tb_sesi ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("id_sesi");
        }
        return -1;
    }

    /** Ambil hasil SAW berdasarkan id_sesi, rekonstruksi sebagai HasilSAW tanpa nilaiNormal */
    public List<spk.model.HasilSAW> getHasilBySesi(int idSesi) throws SQLException {
        List<spk.model.HasilSAW> list = new java.util.ArrayList<>();
        String sql = "SELECT h.ranking, h.skor_akhir, a.id_alternatif, a.kode_makanan, a.nama_paket, a.deskripsi, a.harga " +
                     "FROM tb_hasil h JOIN tb_alternatif a ON h.id_alternatif = a.id_alternatif " +
                     "WHERE h.id_sesi = ? ORDER BY h.ranking ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSesi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    spk.model.Alternatif alt = new spk.model.Alternatif();
                    alt.setIdAlternatif(rs.getInt("id_alternatif"));
                    alt.setNamaPaket(rs.getString("nama_paket"));
                    alt.setDeskripsi(rs.getString("deskripsi"));
                    alt.setHarga(rs.getString("harga"));
                    alt.setKodeMakanan(rs.getString("kode_makanan"));
                    spk.model.HasilSAW h = new spk.model.HasilSAW(
                        rs.getInt("ranking"), alt, rs.getDouble("skor_akhir"),
                        new double[0], new double[0]);
                    list.add(h);
                }
            }
        }
        return list;
    }
}
