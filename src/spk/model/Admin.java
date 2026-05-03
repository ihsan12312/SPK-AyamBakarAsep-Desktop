package spk.model;

/**
 * Admin.java
 * Model untuk data administrator.
 */
public class Admin {
    private int idAdmin;
    private String username;
    private String password;
    private String namaLengkap;
    private String noHp;
    private String alamat;
    private String namaIbuKandung;

    public Admin() {}

    public Admin(int idAdmin, String username, String password, String namaLengkap, String noHp, String alamat, String namaIbuKandung) {
        this.idAdmin = idAdmin;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.noHp = noHp;
        this.alamat = alamat;
        this.namaIbuKandung = namaIbuKandung;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getNamaIbuKandung() { return namaIbuKandung; }
    public void setNamaIbuKandung(String namaIbuKandung) { this.namaIbuKandung = namaIbuKandung; }
}
