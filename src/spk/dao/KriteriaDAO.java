package spk.dao;

import spk.config.DBConnection;
import spk.model.Kriteria;
import java.sql.*;
import java.util.*;

/**
 * KriteriaDAO.java
 * Data Access Object untuk tabel tb_kriteria.
 * Operasi: CRUD + batch update bobot.
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.dao
 */
public class KriteriaDAO {
    private final Connection conn;
    public KriteriaDAO() { this.conn = DBConnection.getInstance().getConnection(); }

    public List<Kriteria> getAll() throws SQLException {
        List<Kriteria> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM tb_kriteria ORDER BY id_kriteria ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int insert(Kriteria k) throws SQLException {
        String sql = "INSERT INTO tb_kriteria (nama_kriteria,jenis,bobot,satuan,keterangan) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.getNamaKriteria()); ps.setString(2, k.getJenis());
            ps.setDouble(3, k.getBobot()); ps.setString(4, k.getSatuan()); ps.setString(5, k.getKeterangan());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) { if (g.next()) return g.getInt(1); }
        }
        return -1;
    }

    public int update(Kriteria k) throws SQLException {
        String sql = "UPDATE tb_kriteria SET nama_kriteria=?,jenis=?,bobot=?,satuan=?,keterangan=? WHERE id_kriteria=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,k.getNamaKriteria()); ps.setString(2,k.getJenis()); ps.setDouble(3,k.getBobot());
            ps.setString(4,k.getSatuan()); ps.setString(5,k.getKeterangan()); ps.setInt(6,k.getIdKriteria());
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_kriteria WHERE id_kriteria=?")) {
            ps.setInt(1, id); return ps.executeUpdate();
        }
    }

    public double getTotalBobot() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(bobot) FROM tb_kriteria");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public void updateBobotBatch(Map<Integer, Double> bobotMap) throws SQLException {
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_kriteria SET bobot=? WHERE id_kriteria=?")) {
            for (Map.Entry<Integer, Double> e : bobotMap.entrySet()) {
                ps.setDouble(1, e.getValue()); ps.setInt(2, e.getKey()); ps.addBatch();
            }
            ps.executeBatch(); conn.commit();
        } catch (SQLException e) { conn.rollback(); throw e; }
        finally { conn.setAutoCommit(true); }
    }

    private Kriteria mapRow(ResultSet rs) throws SQLException {
        return new Kriteria(rs.getInt("id_kriteria"), rs.getString("nama_kriteria"),
            rs.getString("jenis"), rs.getDouble("bobot"), rs.getString("satuan"), rs.getString("keterangan"));
    }
}
