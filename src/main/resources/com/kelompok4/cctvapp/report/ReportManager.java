// File: com/kelompok4/cctvapp/util/ReportManager.java
package com.kelompok4.cctvapp.report;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.Component; // Untuk parent component JOptionPane & JFrame
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

public class ReportManager {

    // --- Detail Koneksi Database Terpusat di Sini ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_cctv_maintenance"; // Sesuaikan!
    private static final String DB_USER = "root"; // Sesuaikan!
    private static final String DB_PASSWORD = ""; // Sesuaikan!

    /**
     * Method private untuk mendapatkan KONEKSI BARU ke database.
     * Akan ditutup otomatis jika dipanggil menggunakan try-with-resources.
     */
    private static Connection getNewConnection() throws SQLException {
        // Class.forName("com.mysql.cj.jdbc.Driver"); // Gak wajib di JDBC 4.0+
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Method utama untuk menampilkan laporan JasperReports.
     * @param parentComponent Komponen parent (biasanya JFrame form) untuk posisi dialog/frame laporan.
     * @param judulFrame Judul untuk window laporan.
     * @param pathJasperFileDiClasspath Path absolut ke file .jasper di classpath (e.g., "/com/path/Laporan.jasper").
     * @param parameterReport Map berisi parameter untuk laporan (bisa null atau HashMap kosong).
     */
    public static void tampilkanLaporan(Component parentComponent, String judulFrame, String pathJasperFileDiClasspath, Map<String, Object> parameterReport) {
        
        InputStream jasperStream = ReportManager.class.getResourceAsStream(pathJasperFileDiClasspath);
        // Kita pake ReportManager.class karena method ini static. Kalo non-static, bisa pake getClass().

        if (jasperStream == null) {
            JOptionPane.showMessageDialog(parentComponent, "File laporan " + pathJasperFileDiClasspath + " tidak ditemukan.", "Error File Laporan", JOptionPane.ERROR_MESSAGE);
            System.err.println("Resource not found: " + pathJasperFileDiClasspath);
            return;
        }

        if (parameterReport == null) {
            parameterReport = new HashMap<>(); // Default parameter kosong
        }

        // --- PAKE TRY-WITH-RESOURCES UNTUK KONEKSI YANG BENAR ---
        try (Connection dbConnection = getNewConnection()) { // Koneksi diambil & akan otomatis ditutup
            
            System.out.println("Koneksi DB baru berhasil dibuka untuk laporan: " + judulFrame);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameterReport, dbConnection);
            
            // dbConnection akan otomatis ditutup di sini, baik ada error maupun tidak.
            System.out.println("Koneksi DB otomatis ditutup setelah laporan: " + judulFrame);
            
            if (jasperPrint.getPages().isEmpty()) {
                 JOptionPane.showMessageDialog(parentComponent, "Laporan '" + judulFrame + "' kosong, tidak ada data untuk ditampilkan.", "Info Laporan", JOptionPane.INFORMATION_MESSAGE);
                 return;
            }

            JasperViewer viewer = new JasperViewer(jasperPrint);
            JFrame reportFrame = new JFrame(judulFrame);
            reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            reportFrame.getContentPane().add(viewer);
            reportFrame.setSize(900, 700);
            reportFrame.setLocationRelativeTo(parentComponent); // Posisi frame laporan relatif ke parent
            reportFrame.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error DB saat menyiapkan laporan '" + judulFrame + "': " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error JasperReports saat membuat laporan '" + judulFrame + "': " + ex.getMessage(), "Error JasperReports", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}