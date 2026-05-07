
package spk.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Koneksi Singleton ke MySQL – spk_ayambakar
 *
 * @author SPK Ayam Bakar Asep
 * @package spk.config
 */
public class DBConnection {

    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private static final String URL      = "jdbc:mysql://localhost:3306/spk_ayambakar"
                                        + "?useSSL=false&serverTimezone=Asia/Jakarta"
                                        + "&characterEncoding=UTF-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";   // sesuaikan jika ada password MySQL

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Gagal konek database: " + e.getMessage(), e);
        }
    }

    public static DBConnection getInstance() {
        try {
            if (instance == null || instance.connection.isClosed()) {
                instance = new DBConnection();
            }
        } catch (SQLException e) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }
}
