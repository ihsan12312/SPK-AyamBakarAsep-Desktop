package spk.model;

/**
 * HasilSAW.java
 * Model untuk menyimpan hasil perhitungan SAW per alternatif.
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.model
 */
public class HasilSAW {
    private int        ranking;
    private Alternatif alternatif;
    private double     skorAkhir;
    private double[]   nilaiNormal;
    private double[]   nilaiTerbobot;
    private java.util.Map<Integer, Double> normalisasiMap = new java.util.HashMap<>();

    public HasilSAW() {}
    public HasilSAW(int ranking, Alternatif alt, double skor, double[] normal, double[] terbobot) {
        this.ranking = ranking; this.alternatif = alt; this.skorAkhir = skor;
        this.nilaiNormal = normal; this.nilaiTerbobot = terbobot;
    }

    public int        getRanking()               { return ranking; }
    public void       setRanking(int v)          { this.ranking = v; }
    public Alternatif getAlternatif()            { return alternatif; }
    public void       setAlternatif(Alternatif v){ this.alternatif = v; }
    public double     getSkorAkhir()             { return skorAkhir; }
    public void       setSkorAkhir(double v)     { this.skorAkhir = v; }
    public double[]   getNilaiNormal()           { return nilaiNormal; }
    public void       setNilaiNormal(double[] v) { this.nilaiNormal = v; }
    public double[]   getNilaiTerbobot()         { return nilaiTerbobot; }
    public void       setNilaiTerbobot(double[] v){ this.nilaiTerbobot = v; }
    public String     getSkorFormatted()         { return spk.ui.UITheme.formatDesimal(skorAkhir); }
    public java.util.Map<Integer,Double> getNormalisasiMap()          { return normalisasiMap; }
    public void       setNormalisasiMap(java.util.Map<Integer,Double> m) { this.normalisasiMap = m; }
    public double     getNormalisasi(int idKriteria) { return normalisasiMap.getOrDefault(idKriteria, 0.0); }
    public String     getStatusLabel() {
        if (ranking == 1) return "Terbaik";
        if (ranking <= 3) return "Unggulan";
        return "Cukup Baik";
    }
}
