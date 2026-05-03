package spk.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Alternatif.java
 * Model untuk data paket menu beserta nilai per kriteria.
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.model
 */
public class Alternatif {
    private int     idAlternatif;
    private String  kodeMakanan;
    private String  namaPaket;
    private String  deskripsi;
    private String  harga;
    private boolean isActive;
    private Map<Integer, Double> nilaiMap = new HashMap<>();

    public Alternatif() {}
    public Alternatif(int id, String kodeMakanan, String nama, String desk, String harga, boolean active) {
        this.idAlternatif = id; this.kodeMakanan = kodeMakanan; this.namaPaket = nama; this.deskripsi = desk;
        this.harga = harga; this.isActive = active;
    }

    public Alternatif(int aInt, String string, String string0, int aInt0, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int    getIdAlternatif()                       { return idAlternatif; }
    public void   setIdAlternatif(int v)                  { this.idAlternatif = v; }
    public String getKodeMakanan()                        { return kodeMakanan; }
    public void   setKodeMakanan(String v)                { this.kodeMakanan = v; }
    public String getNamaPaket()                          { return namaPaket; }
    public void   setNamaPaket(String v)                  { this.namaPaket = v; }
    public String getDeskripsi()                          { return deskripsi; }
    public void   setDeskripsi(String v)                  { this.deskripsi = v; }
    public String getHarga()                              { return harga; }
    public void   setHarga(String v)                      { this.harga = v; }
    public boolean isActive()                             { return isActive; }
    public void   setActive(boolean v)                    { this.isActive = v; }
    public Map<Integer,Double> getNilaiMap()              { return nilaiMap; }
    public void   setNilaiMap(Map<Integer,Double> m)      { this.nilaiMap = m; }
    public double getNilai(int idKriteria)                { return nilaiMap.getOrDefault(idKriteria, 0.0); }
    public void   setNilai(int idKriteria, double nilai)  { nilaiMap.put(idKriteria, nilai); }
    @Override public String toString() { return namaPaket; }

    public Object getHargaFormatted() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
