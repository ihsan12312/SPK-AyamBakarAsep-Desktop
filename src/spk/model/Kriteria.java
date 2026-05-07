package spk.model;

/**
 * Kriteria.java
 * Model untuk data kriteria penilaian SAW.
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.model
 */
public class Kriteria {
    private int    idKriteria;
    private String namaKriteria;
    private String jenis;
    private double bobot;
    private String satuan;
    private String keterangan;

    public Kriteria() {}
    public Kriteria(int id, String nama, String jenis, double bobot, String satuan, String ket) {
        this.idKriteria = id; this.namaKriteria = nama; this.jenis = jenis;
        this.bobot = bobot; this.satuan = satuan; this.keterangan = ket;
    }

    public int    getIdKriteria()            { return idKriteria; }
    public void   setIdKriteria(int v)       { this.idKriteria = v; }
    public String getNamaKriteria()          { return namaKriteria; }
    public void   setNamaKriteria(String v)  { this.namaKriteria = v; }
    public String getJenis()                 { return jenis; }
    public void   setJenis(String v)         { this.jenis = v; }
    public double getBobot()                 { return bobot; }
    public void   setBobot(double v)         { this.bobot = v; }
    public String getSatuan()                { return satuan; }
    public void   setSatuan(String v)        { this.satuan = v; }
    public String getKeterangan()            { return keterangan; }
    public void   setKeterangan(String v)    { this.keterangan = v; }
    public double getBobotDesimal()          { return bobot; }  // bobot sudah dalam format desimal (misal: 0.25, 0.03)
    public boolean isBenefit()               { return "benefit".equalsIgnoreCase(jenis); }

    @Override public String toString()       { return namaKriteria; }
}
