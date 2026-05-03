package spk.util;

import spk.model.Alternatif;
import spk.model.HasilSAW;
import spk.model.Kriteria;
import java.util.*;

/**
 * SAWCalculator.java
 * Implementasi algoritma Simple Additive Weighting (SAW).
 *
 * Tahapan:
 *  1. Buat matriks keputusan X[n][m]
 *  2. Hitung nilai referensi (max/min per kriteria)
 *  3. Normalisasi matriks → R[n][m]
 *  4. Hitung skor akhir Vi = Σ(Wj × Rij)
 *  5. Urutkan berdasarkan skor tertinggi → ranking
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.util
 */
public class SAWCalculator {

    public static List<HasilSAW> hitung(List<Alternatif> alts, List<Kriteria> crits) {
        if (alts == null || alts.isEmpty()) throw new IllegalArgumentException("Daftar alternatif kosong.");
        if (crits == null || crits.isEmpty()) throw new IllegalArgumentException("Daftar kriteria kosong.");

        int n = alts.size(), m = crits.size();

        // Step 1: Matriks keputusan
        double[][] X = new double[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                X[i][j] = alts.get(i).getNilai(crits.get(j).getIdKriteria());

        // Step 2: Nilai referensi (max untuk benefit, min untuk cost)
        double[] ref = new double[m];
        for (int j = 0; j < m; j++) {
            boolean isBenefit = crits.get(j).isBenefit();
            ref[j] = isBenefit ? Double.MIN_VALUE : Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (isBenefit) { if (X[i][j] > ref[j]) ref[j] = X[i][j]; }
                else           { if (X[i][j] < ref[j]) ref[j] = X[i][j]; }
            }
        }

        // Step 3: Normalisasi
        double[][] R = new double[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                double xij = X[i][j], rv = ref[j];
                R[i][j] = crits.get(j).isBenefit()
                    ? (rv == 0 ? 0 : xij / rv)
                    : (xij == 0 ? 0 : rv / xij);
            }

        // Step 4: Bobot desimal
        double[] W = new double[m];
        for (int j = 0; j < m; j++) W[j] = crits.get(j).getBobotDesimal();

        // Step 5: Skor akhir Vi = Σ(Wj × Rij)
        double[] V = new double[n];
        for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) V[i] += W[j] * R[i][j];

        // Step 6: Buat hasil + ranking
        List<HasilSAW> hasil = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double[] terbobot = new double[m];
            for (int j = 0; j < m; j++) terbobot[j] = W[j] * R[i][j];
            HasilSAW hs = new HasilSAW(0, alts.get(i), V[i], R[i].clone(), terbobot);
            // Populate normalisasiMap (id_kriteria -> nilai normalisasi)
            java.util.Map<Integer, Double> normMap = new java.util.HashMap<>();
            for (int j = 0; j < m; j++) normMap.put(crits.get(j).getIdKriteria(), R[i][j]);
            hs.setNormalisasiMap(normMap);
            hasil.add(hs);
        }
        hasil.sort((a, b) -> Double.compare(b.getSkorAkhir(), a.getSkorAkhir()));
        for (int i = 0; i < hasil.size(); i++) hasil.get(i).setRanking(i + 1);

        return hasil;
    }
}
