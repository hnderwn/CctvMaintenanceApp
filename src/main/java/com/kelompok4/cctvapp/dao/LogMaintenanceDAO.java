package com.kelompok4.cctvapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import com.kelompok4.cctvapp.LogMaintenance;
import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.CctvModel;
import com.kelompok4.cctvapp.util.DbConnector;


/**
 * Kelas Data Access Object (DAO) untuk mengelola data Log Maintenance di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas Komponen.
 */
public class LogMaintenanceDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }
    
    public int insertLogMaintenance(LogMaintenance log) throws SQLException {
        System.out.println("DAO: insertLogMaintenance (versi mandiri) dipanggil.");
        try (Connection conn = getConnection()) {
            return insertLogMaintenance(log, conn);
        }
    }

    public int insertLogMaintenance(LogMaintenance log, Connection conn) throws SQLException {
        System.out.println("DAO: insertLogMaintenance (versi transaksi) dipanggil.");
        String sql = "INSERT INTO log_maintenance (id_cctv, id_teknisi, tanggal, jam, deskripsi_log) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, log.getCctvUnit().getIdCctv());
            pstmt.setString(2, log.getTeknisi().getIdTeknisi());

            if (log.getTanggal() != null) {
                pstmt.setDate(3, new java.sql.Date(log.getTanggal().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            if (log.getJam() != null && !log.getJam().isEmpty()) {
                try {
                    pstmt.setTime(4, java.sql.Time.valueOf(log.getJam() + ":00"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Format jam tidak valid (HH:mm): " + log.getJam());
                    pstmt.setNull(4, java.sql.Types.TIME);
                }
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }

            pstmt.setString(5, log.getDeskripsiLog());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
    
    public List<LogMaintenance> getAllLogMaintenance() throws SQLException {
        List<LogMaintenance> logList = new ArrayList<>();
        String sql = "SELECT " +
                "lm.id_log, lm.tanggal, lm.jam, lm.deskripsi_log, " +
                "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th, " +
                "t.id_teknisi, t.nama_teknisi, t.kontak " +
                "FROM log_maintenance lm " +
                "JOIN cctv c ON lm.id_cctv = c.id_cctv " +
                "JOIN teknisi t ON lm.id_teknisi = t.id_teknisi " +
                "JOIN cctv_models m ON c.id_model = m.id_model";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idLog = rs.getInt("id_log");

                java.util.Date tanggal = null;
                java.sql.Date sqlDate = rs.getDate("tanggal");
                if (sqlDate != null) {
                    tanggal = new java.util.Date(sqlDate.getTime());
                }

                String jam = null;
                java.sql.Time sqlTime = rs.getTime("jam");
                if (sqlTime != null) {
                    jam = sqlTime.toString().substring(0, 5);
                }

                String deskripsiLog = rs.getString("deskripsi_log");

                String idCctv = rs.getString("id_cctv");
                String lokasi = rs.getString("lokasi");
                String status = rs.getString("status");
                String cctvKeterangan = rs.getString("keterangan");
                String idModel = rs.getString("id_model");
                String namaModel = rs.getString("nama_model");
                String manufaktur = rs.getString("manufaktur");
                String spesifikasi = rs.getString("spesifikasi");
                int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                CctvModel cctvModel = new CctvModel(idModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);
                CctvUnit cctvUnit = new CctvUnit(idCctv, lokasi, status, cctvKeterangan, cctvModel);

                String idTeknisi = rs.getString("id_teknisi");
                String namaTeknisi = rs.getString("nama_teknisi");
                String kontakTeknisi = rs.getString("kontak");
                Teknisi teknisi = new Teknisi(idTeknisi, namaTeknisi, kontakTeknisi);

                LogMaintenance log = new LogMaintenance(idLog, cctvUnit, teknisi, tanggal, jam, deskripsiLog);
                logList.add(log);
            }
            return logList;

        }
    }

    public LogMaintenance findLogMaintenanceById(String idLog) throws SQLException {
        String sql = "SELECT " +
                "lm.id_log, lm.tanggal, lm.jam, lm.deskripsi_log, " +
                "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th, " +
                "t.id_teknisi, t.nama_teknisi, t.kontak " +
                "FROM log_maintenance lm " +
                "JOIN cctv c ON lm.id_cctv = c.id_cctv " +
                "JOIN teknisi t ON lm.id_teknisi = t.id_teknisi " +
                "JOIN cctv_models m ON c.id_model = m.id_model " +
                "WHERE lm.id_log = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idLog);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int foundIdLog = rs.getInt("id_log");

                    java.util.Date tanggal = null;
                    java.sql.Date sqlDate = rs.getDate("tanggal");
                    if (sqlDate != null) {
                        tanggal = new java.util.Date(sqlDate.getTime());
                    }

                    String jam = null;
                    java.sql.Time sqlTime = rs.getTime("jam");
                    if (sqlTime != null) {
                        jam = sqlTime.toString().substring(0, 5);
                    }

                    String deskripsiLog = rs.getString("deskripsi_log");
                    String idCctv = rs.getString("id_cctv");
                    String lokasi = rs.getString("lokasi");
                    String status = rs.getString("status");
                    String cctvKeterangan = rs.getString("keterangan");
                    String idModel = rs.getString("id_model");
                    String namaModel = rs.getString("nama_model");
                    String manufaktur = rs.getString("manufaktur");
                    String spesifikasi = rs.getString("spesifikasi");
                    int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                    CctvModel cctvModel = new CctvModel(idModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);
                    CctvUnit cctvUnit = new CctvUnit(idCctv, lokasi, status, cctvKeterangan, cctvModel);

                    String idTeknisi = rs.getString("id_teknisi");
                    String namaTeknisi = rs.getString("nama_teknisi");
                    String kontakTeknisi = rs.getString("kontak");
                    Teknisi teknisi = new Teknisi(idTeknisi, namaTeknisi, kontakTeknisi);

                    LogMaintenance log = new LogMaintenance(foundIdLog, cctvUnit, teknisi, tanggal, jam, deskripsiLog);
                    return log;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean updateLogMaintenance(LogMaintenance log) throws SQLException {
        String sql = "UPDATE log_maintenance SET id_cctv = ?, id_teknisi = ?, tanggal = ?, jam = ?, deskripsi_log = ? WHERE id_log = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log.getCctvUnit().getIdCctv());
            pstmt.setString(2, log.getTeknisi().getIdTeknisi());

            if (log.getTanggal() != null) {
                pstmt.setDate(3, new java.sql.Date(log.getTanggal().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            if (log.getJam() != null && !log.getJam().isEmpty()) {
                try {
                    pstmt.setTime(4, java.sql.Time.valueOf(log.getJam() + ":00"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Format jam tidak valid (HH:mm): " + log.getJam());
                    pstmt.setNull(4, java.sql.Types.TIME);
                }
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }

            pstmt.setString(5, log.getDeskripsiLog());
            pstmt.setString(6, String.valueOf(log.getIdLog()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate LogMaintenance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLogMaintenance(String idLog) throws SQLException {
        String sql = "DELETE FROM log_maintenance WHERE id_log = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idLog);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus LogMaintenance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
