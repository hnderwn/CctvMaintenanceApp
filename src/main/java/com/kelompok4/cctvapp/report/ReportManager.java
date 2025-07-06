package com.kelompok4.cctvapp.report;

import com.kelompok4.cctvapp.util.DbConnector;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.Component;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/*
 * =================================================================================
 * RINGKASAN METHOD
 * =================================================================================
 * tampilkanLaporan(...) - Method utama statis untuk menampilkan laporan JasperReports.
 * =================================================================================
 */
public class ReportManager {

    /**
     * Method utama untuk menampilkan laporan JasperReports.
     * Menggunakan koneksi database terpusat dari DbConnector.
     *
     * @param parentComponent             Komponen parent untuk posisi dialog/frame laporan.
     * @param judulFrame                  Judul untuk window laporan.
     * @param pathJasperFileDiClasspath Path absolut ke file .jasper di classpath.
     * @param parameterReport             Map berisi parameter untuk laporan (bisa null).
     */
    public static void tampilkanLaporan(Component parentComponent, String judulFrame, String pathJasperFileDiClasspath, Map<String, Object> parameterReport) {

        InputStream jasperStream = ReportManager.class.getResourceAsStream(pathJasperFileDiClasspath);

        if (jasperStream == null) {
            JOptionPane.showMessageDialog(parentComponent, "File laporan " + pathJasperFileDiClasspath + " tidak ditemukan.", "Error File Laporan", JOptionPane.ERROR_MESSAGE);
            System.err.println("Resource not found: " + pathJasperFileDiClasspath);
            return;
        }

        if (parameterReport == null) {
            parameterReport = new HashMap<>();
        }

        try (Connection dbConnection = DbConnector.getNewConnection()) {
            System.out.println("Koneksi DB dari DbConnector berhasil dibuka untuk laporan: " + judulFrame);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameterReport, dbConnection);
            
            System.out.println("Koneksi DB otomatis ditutup setelah laporan: " + judulFrame);

            if (jasperPrint.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Laporan '" + judulFrame + "' kosong, tidak ada data untuk ditampilkan.", "Info Laporan", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tampilkan laporan di viewer
            JasperViewer viewer = new JasperViewer(jasperPrint, false); // false = jangan exit aplikasi saat viewer ditutup
            viewer.setTitle(judulFrame);
            viewer.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error DB saat menyiapkan laporan '" + judulFrame + "': " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error JasperReports saat membuat laporan '" + judulFrame + "': " + ex.getMessage(), "Error JasperReports", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}