package com.kelompok4.cctvapp.dao;

import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.LogKerusakan;
import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.CctvModel;
import com.kelompok4.cctvapp.util.DbConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Kelas Data Access Object (DAO) untuk mengelola data Log Kerusakan di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas CctvModel.
 */
public class LogKerusakanDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }
    
    public int insertLogKerusakan(LogKerusakan log) throws SQLException {
        try (Connection conn = getConnection()) {
            return insertLogKerusakan(log, conn); 
        }
    }
    

    public int insertLogKerusakan(LogKerusakan log, Connection conn) throws SQLException {
        String sql = "INSERT INTO log_kerusakan (id_cctv, id_teknisi, tanggal, jam, deskripsi_kerusakan) VALUES (?, ?, ?, ?, ?)";

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
                    pstmt.setTime(4, Time.valueOf(log.getJam() + ":00")); 
                } catch (IllegalArgumentException e) {
                    System.err.println("Format jam tidak valid (HH:mm): " + log.getJam());
                    pstmt.setNull(4, java.sql.Types.TIME);
                }
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }

            pstmt.setString(5, log.getDeskripsi());

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

    public List<LogKerusakan> getAllLogKerusakan() throws SQLException {
        List<LogKerusakan> logList = new ArrayList<>();
        String sql = "SELECT " +
                "lk.id_log, lk.tanggal, lk.jam, lk.deskripsi_kerusakan, " +
                "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th, " +
                "t.id_teknisi, t.nama_teknisi, t.kontak " +
                "FROM log_kerusakan lk " +
                "JOIN cctv c ON lk.id_cctv = c.id_cctv " +
                "JOIN teknisi t ON lk.id_teknisi = t.id_teknisi " +
                "JOIN cctv_models m ON c.id_model = m.id_model";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int idLog = rs.getInt("id_log");
                java.util.Date tanggal = null;
                Date sqlDate = rs.getDate("tanggal");
                if (sqlDate != null) {
                    tanggal = new java.util.Date(sqlDate.getTime());
                }
                String jam = null;
                Time sqlTime = rs.getTime("jam");
                if (sqlTime != null) {
                    jam = sqlTime.toString().substring(0, 5);
                }
                String deskripsi = rs.getString("deskripsi_kerusakan");

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

                LogKerusakan log = new LogKerusakan(idLog, cctvUnit, teknisi, tanggal, jam, deskripsi);
                logList.add(log);
            }
            return logList;
        }
    }

    public LogKerusakan findLogKerusakanById(String idLog) throws SQLException {
        String sql = "SELECT " +
                "lk.id_log, lk.tanggal, lk.jam, lk.deskripsi_kerusakan, " +
                "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th, " +
                "t.id_teknisi, t.nama_teknisi, t.kontak " +
                "FROM log_kerusakan lk " +
                "JOIN cctv c ON lk.id_cctv = c.id_cctv " +
                "JOIN teknisi t ON lk.id_teknisi = t.id_teknisi " +
                "JOIN cctv_models m ON c.id_model = m.id_model " +
                "WHERE lk.id_log = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idLog);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int foundIdLog = rs.getInt("id_log");
                    java.util.Date tanggal = null;
                    Date sqlDate = rs.getDate("tanggal");
                    if (sqlDate != null) {
                        tanggal = new java.util.Date(sqlDate.getTime());
                    }
                    String jam = null;
                    Time sqlTime = rs.getTime("jam");
                    if (sqlTime != null) {
                        jam = sqlTime.toString().substring(0, 5);
                    }
                    String deskripsi = rs.getString("deskripsi_kerusakan");

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

                    return new LogKerusakan(foundIdLog, cctvUnit, teknisi, tanggal, jam, deskripsi);
                }
                return null;
            }
        }
    }

    public boolean updateLogKerusakan(LogKerusakan log) throws SQLException {
        String sql = "UPDATE log_kerusakan SET id_cctv = ?, id_teknisi = ?, tanggal = ?, jam = ?, deskripsi_kerusakan = ? WHERE id_log = ?";
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
                    pstmt.setTime(4, Time.valueOf(log.getJam() + ":00"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Format jam tidak valid (HH:mm): " + log.getJam());
                    pstmt.setNull(4, java.sql.Types.TIME);
                }
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }
            pstmt.setString(5, log.getDeskripsi()); 
            pstmt.setString(6, String.valueOf(log.getIdLog())); 

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate Log Kerusakan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLogKerusakan(String idLog) throws SQLException {
        String sql = "DELETE FROM log_kerusakan WHERE id_log = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idLog);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus Log Kerusakan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}