package spk.dao;

import spk.config.DBConnection;
import spk.model.Alternatif;
import java.sql.*;
import java.util.*;

/**
 * AlternatifDAO.java
 * Data Access Object untuk tabel tb_alternatif dan tb_nilai.
 * Operasi: CRUD paket menu + simpan matriks nilai.
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.dao
 */
public class AlternatifDAO {
    private final Connection conn;
    public AlternatifDAO() { this.conn = DBConnection.getInstance().getConnection(); }

    public List<Alternatif> getAll() throws SQLException {
        List<Alternatif> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM tb_alternatif WHERE is_active=1 ORDER BY id_alternatif");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Alternatif> getAllWithNilai() throws SQLException {
        List<Alternatif> list = getAll();
        Map<Integer, Map<Integer, Double>> nilaiIndex = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT n.id_alternatif, n.id_kriteria, n.nilai " +
                "FROM tb_nilai n JOIN tb_alternatif a ON n.id_alternatif = a.id_alternatif " +
                "WHERE a.is_active = 1");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int ai = rs.getInt("id_alternatif"), ki = rs.getInt("id_kriteria");
                nilaiIndex.computeIfAbsent(ai, k -> new HashMap<>()).put(ki, rs.getDouble("nilai"));
            }
        }
        for (Alternatif a : list) a.setNilaiMap(nilaiIndex.getOrDefault(a.getIdAlternatif(), new HashMap<>()));
        return list;
    }

    public int insert(Alternatif a) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_alternatif(kode_makanan,nama_paket,deskripsi,harga) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,a.getKodeMakanan()); ps.setString(2,a.getNamaPaket()); 
            ps.setString(3,a.getDeskripsi()); ps.setString(4,a.getHarga());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) { if (g.next()) return g.getInt(1); }
        }
        return -1;
    }

    public int update(Alternatif a) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_alternatif SET kode_makanan=?,nama_paket=?,deskripsi=?,harga=? WHERE id_alternatif=?")) {
            ps.setString(1,a.getKodeMakanan()); ps.setString(2,a.getNamaPaket()); 
            ps.setString(3,a.getDeskripsi()); ps.setString(4,a.getHarga()); 
            ps.setInt(5,a.getIdAlternatif());
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_alternatif SET is_active=0 WHERE id_alternatif=?")) {
            ps.setInt(1, id); return ps.executeUpdate();
        }
    }

    public void saveNilaiBatch(int idAlt, Map<Integer, Double> nilaiMap) throws SQLException {
        String sql = "INSERT INTO tb_nilai(id_alternatif,id_kriteria,nilai) VALUES(?,?,?) ON DUPLICATE KEY UPDATE nilai=VALUES(nilai)";
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer,Double> e : nilaiMap.entrySet()) {
                ps.setInt(1,idAlt); ps.setInt(2,e.getKey()); ps.setDouble(3,e.getValue()); ps.addBatch();
            }
            ps.executeBatch(); conn.commit();
        } catch (SQLException e) { conn.rollback(); throw e; }
        finally { conn.setAutoCommit(true); }
    }

    private Alternatif mapRow(ResultSet rs) throws SQLException {
        return new Alternatif(rs.getInt("id_alternatif"), rs.getString("kode_makanan"), rs.getString("nama_paket"),
            rs.getString("deskripsi"), rs.getString("harga"), rs.getInt("is_active")==1);
    }
}
