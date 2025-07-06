package com.kelompok4.cctvapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.Time;
import com.kelompok4.cctvapp.LogKomponenDipakai;
import com.kelompok4.cctvapp.LogMaintenance;
import com.kelompok4.cctvapp.LogKerusakan;
import com.kelompok4.cctvapp.Komponen;
import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.CctvModel;
import com.kelompok4.cctvapp.util.DbConnector;
import java.math.BigDecimal;


/**
 * Kelas Data Access Object (DAO) untuk mengelola data Log Komponen Dipakai di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas CctvModel.
 */
public class LogKomponenDipakaiDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }

    public int insertLogKomponenDipakai(LogKomponenDipakai logKomponen) throws SQLException {
        String sql = "INSERT INTO log_komponen_dipakai (id_log_maintenance, id_log_kerusakan, id_komponen, jumlah_dipakai, biaya) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (logKomponen.getLogMaintenance() != null) {
                pstmt.setString(1, String.valueOf(logKomponen.getLogMaintenance().getIdLog()));
            } else {
                pstmt.setNull(1, java.sql.Types.VARCHAR);
            }

            if (logKomponen.getLogKerusakan() != null && logKomponen.getLogKerusakan().getIdLog() != null) {
                pstmt.setString(2, String.valueOf(logKomponen.getLogKerusakan().getIdLog()));
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setString(3, logKomponen.getKomponen().getIdKomponen());
            pstmt.setInt(4, logKomponen.getJumlahDipakai());
            pstmt.setBigDecimal(5, logKomponen.getBiaya());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Error saat menyimpan LogKomponenDipakai: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public List<LogKomponenDipakai> getAllLogKomponenDipakai() throws SQLException {
        List<LogKomponenDipakai> logList = new ArrayList<>();
        String sql = "SELECT " +
                "lkd.id, lkd.jumlah_dipakai, lkd.biaya, " +
                "lm.id_log AS lm_id_log, lm.tanggal AS lm_tanggal, lm.jam AS lm_jam, lm.deskripsi_log AS lm_deskripsi_log, " +
                "lk.id_log AS lk_id_log, lk.tanggal AS lk_tanggal, lk.jam AS lk_jam, lk.deskripsi_kerusakan AS lk_deskripsi_kerusakan, " +
                "k.id_komponen, k.nama_komponen, k.satuan, k.stok, k.harga_satuan, " +
                "c_lm.id_cctv AS cctv_lm_id, c_lm.lokasi AS cctv_lm_lokasi, c_lm.status AS cctv_lm_status, c_lm.keterangan AS cctv_lm_keterangan, " +
                "m_lm.id_model AS model_lm_id, m_lm.nama_model AS model_lm_nama, m_lm.manufaktur AS model_lm_manufaktur, m_lm.spesifikasi AS model_lm_spesifikasi, m_lm.umur_ekonomis_th AS model_lm_umur_ekonomis, " +
                "t_lm.id_teknisi AS teknisi_lm_id, t_lm.nama_teknisi AS teknisi_lm_nama, t_lm.kontak AS teknisi_lm_kontak, " +
                "c_lk.id_cctv AS cctv_lk_id, c_lk.lokasi AS cctv_lk_lokasi, c_lk.status AS cctv_lk_status, c_lk.keterangan AS cctv_lk_keterangan, " +
                "m_lk.id_model AS model_lk_id, m_lk.nama_model AS model_lk_nama, m_lk.manufaktur AS model_lk_manufaktur, m_lk.spesifikasi AS model_lk_spesifikasi, m_lk.umur_ekonomis_th AS model_lk_umur_ekonomis, " +
                "t_lk.id_teknisi AS teknisi_lk_id, t_lk.nama_teknisi AS teknisi_lk_nama, t_lk.kontak AS teknisi_lk_kontak " +
                "FROM log_komponen_dipakai lkd " +
                "LEFT JOIN log_maintenance lm ON lkd.id_log_maintenance = lm.id_log " +
                "LEFT JOIN cctv c_lm ON lm.id_cctv = c_lm.id_cctv " +
                "LEFT JOIN teknisi t_lm ON lm.id_teknisi = t_lm.id_teknisi " +
                "LEFT JOIN cctv_models m_lm ON c_lm.id_model = m_lm.id_model " +
                "LEFT JOIN log_kerusakan lk ON lkd.id_log_kerusakan = lk.id_log " +
                "LEFT JOIN cctv c_lk ON lk.id_cctv = c_lk.id_cctv " +
                "LEFT JOIN teknisi t_lk ON lk.id_teknisi = t_lk.id_teknisi " +
                "LEFT JOIN cctv_models m_lk ON c_lk.id_model = m_lk.id_model " +
                "JOIN komponen k ON lkd.id_komponen = k.id_komponen";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int jumlahDipakai = rs.getInt("jumlah_dipakai");
                BigDecimal biaya = rs.getBigDecimal("biaya");

                LogMaintenance logMaintenance = null;
                Integer lm_id_log = rs.getInt("lm_id_log");
                if (!rs.wasNull()) {
                    Date lm_tanggal = rs.getDate("lm_tanggal");
                    Time lm_jam = rs.getTime("lm_jam");
                    String lm_deskripsi_log = rs.getString("lm_deskripsi_log");

                    CctvModel cctvModel_lm = new CctvModel(rs.getString("model_lm_id"), rs.getString("model_lm_nama"), rs.getString("model_lm_manufaktur"), rs.getString("model_lm_spesifikasi"), rs.getInt("model_lm_umur_ekonomis"));
                    CctvUnit cctvUnit_lm = new CctvUnit(rs.getString("cctv_lm_id"), rs.getString("cctv_lm_lokasi"), rs.getString("cctv_lm_status"), rs.getString("cctv_lm_keterangan"), cctvModel_lm);
                    Teknisi teknisi_lm = new Teknisi(rs.getString("teknisi_lm_id"), rs.getString("teknisi_lm_nama"), rs.getString("teknisi_lm_kontak"));

                    logMaintenance = new LogMaintenance(lm_id_log, cctvUnit_lm, teknisi_lm, lm_tanggal, (lm_jam != null) ? lm_jam.toString() : null, lm_deskripsi_log);
                }

                LogKerusakan logKerusakan = null;
                Integer lk_id_log = rs.getInt("lk_id_log");
                if (!rs.wasNull()) {
                    Date lk_tanggal = rs.getDate("lk_tanggal");
                    Time lk_jam = rs.getTime("lk_jam");
                    String lk_deskripsi_kerusakan = rs.getString("lk_deskripsi_kerusakan");

                    CctvModel cctvModel_lk = new CctvModel(rs.getString("model_lk_id"), rs.getString("model_lk_nama"), rs.getString("model_lk_manufaktur"), rs.getString("model_lk_spesifikasi"), rs.getInt("model_lk_umur_ekonomis"));
                    CctvUnit cctvUnit_lk = new CctvUnit(rs.getString("cctv_lk_id"), rs.getString("cctv_lk_lokasi"), rs.getString("cctv_lk_status"), rs.getString("cctv_lk_keterangan"), cctvModel_lk);
                    Teknisi teknisi_lk = new Teknisi(rs.getString("teknisi_lk_id"), rs.getString("teknisi_lk_nama"), rs.getString("teknisi_lk_kontak"));

                    logKerusakan = new LogKerusakan(lk_id_log, cctvUnit_lk, teknisi_lk, lk_tanggal, (lk_jam != null) ? lk_jam.toString() : null, lk_deskripsi_kerusakan);
                }

                String idKomponen = rs.getString("id_komponen");
                String namaKomponen = rs.getString("nama_komponen");
                String satuan = rs.getString("satuan");
                int stok = rs.getInt("stok");
                BigDecimal hargaSatuan = rs.getBigDecimal("harga_satuan");
                Komponen komponen = new Komponen(idKomponen, namaKomponen, satuan, stok, hargaSatuan);

                LogKomponenDipakai logKomponenDipakai = new LogKomponenDipakai(id, logMaintenance, logKerusakan, komponen, jumlahDipakai, biaya);
                logList.add(logKomponenDipakai);
            }
            return logList;
        }
    }


    public LogKomponenDipakai findLogKomponenDipakaiById(int id) throws SQLException {
        String sql = "SELECT " +
                "lkd.id, lkd.jumlah_dipakai, lkd.biaya, " +
                "lm.id_log AS lm_id_log, lm.tanggal AS lm_tanggal, lm.jam AS lm_jam, lm.deskripsi_log AS lm_deskripsi_log, " +
                "lk.id_log AS lk_id_log, lk.tanggal AS lk_tanggal, lk.jam AS lk_jam, lk.deskripsi_kerusakan AS lk_deskripsi_kerusakan, " +
                "k.id_komponen, k.nama_komponen, k.satuan, k.stok, k.harga_satuan, " +
                "c_lm.id_cctv AS cctv_lm_id, c_lm.lokasi AS cctv_lm_lokasi, c_lm.status AS cctv_lm_status, c_lm.keterangan AS cctv_lm_keterangan, " +
                "m_lm.id_model AS model_lm_id, m_lm.nama_model AS model_lm_nama, m_lm.manufaktur AS model_lm_manufaktur, m_lm.spesifikasi AS model_lm_spesifikasi, m_lm.umur_ekonomis_th AS model_lm_umur_ekonomis, " +
                "t_lm.id_teknisi AS teknisi_lm_id, t_lm.nama_teknisi AS teknisi_lm_nama, t_lm.kontak AS teknisi_lm_kontak, " +
                "c_lk.id_cctv AS cctv_lk_id, c_lk.lokasi AS cctv_lk_lokasi, c_lk.status AS cctv_lk_status, c_lk.keterangan AS cctv_lk_keterangan, " +
                "m_lk.id_model AS model_lk_id, m_lk.nama_model AS model_lk_nama, m_lk.manufaktur AS model_lk_manufaktur, m_lk.spesifikasi AS model_lk_spesifikasi, m_lk.umur_ekonomis_th AS model_lk_umur_ekonomis, " +
                "t_lk.id_teknisi AS teknisi_lk_id, t_lk.nama_teknisi AS teknisi_lk_nama, t_lk.kontak AS teknisi_lk_kontak " +
                "FROM log_komponen_dipakai lkd " +
                "LEFT JOIN log_maintenance lm ON lkd.id_log_maintenance = lm.id_log " +
                "LEFT JOIN cctv c_lm ON lm.id_cctv = c_lm.id_cctv " +
                "LEFT JOIN teknisi t_lm ON lm.id_teknisi = t_lm.id_teknisi " +
                "LEFT JOIN cctv_models m_lm ON c_lm.id_model = m_lm.id_model " +
                "LEFT JOIN log_kerusakan lk ON lkd.id_log_kerusakan = lk.id_log " +
                "LEFT JOIN cctv c_lk ON lk.id_cctv = c_lk.id_cctv " +
                "LEFT JOIN teknisi t_lk ON lk.id_teknisi = t_lk.id_teknisi " +
                "LEFT JOIN cctv_models m_lk ON c_lk.id_model = m_lk.id_model " +
                "JOIN komponen k ON lkd.id_komponen = k.id_komponen " +
                "WHERE lkd.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int foundId = rs.getInt("id");
                    int jumlahDipakai = rs.getInt("jumlah_dipakai");
                    BigDecimal biaya = rs.getBigDecimal("biaya");

                    LogMaintenance logMaintenance = null;
                    Integer lm_id_log = rs.getInt("lm_id_log");
                    if (!rs.wasNull()) {
                        Date lm_tanggal = rs.getDate("lm_tanggal");
                        Time lm_jam = rs.getTime("lm_jam");
                        String lm_deskripsi_log = rs.getString("lm_deskripsi_log");

                        CctvModel cctvModel_lm = new CctvModel(rs.getString("model_lm_id"), rs.getString("model_lm_nama"), rs.getString("model_lm_manufaktur"), rs.getString("model_lm_spesifikasi"), rs.getInt("model_lm_umur_ekonomis"));
                        CctvUnit cctvUnit_lm = new CctvUnit(rs.getString("cctv_lm_id"), rs.getString("cctv_lm_lokasi"), rs.getString("cctv_lm_status"), rs.getString("cctv_lm_keterangan"), cctvModel_lm);
                        Teknisi teknisi_lm = new Teknisi(rs.getString("teknisi_lm_id"), rs.getString("teknisi_lm_nama"), rs.getString("teknisi_lm_kontak"));

                        logMaintenance = new LogMaintenance(lm_id_log, cctvUnit_lm, teknisi_lm, lm_tanggal, (lm_jam != null) ? lm_jam.toString() : null, lm_deskripsi_log);
                    }

                    LogKerusakan logKerusakan = null;
                    Integer lk_id_log = rs.getInt("lk_id_log");
                    if (!rs.wasNull()) {
                        Date lk_tanggal = rs.getDate("lk_tanggal");
                        Time lk_jam = rs.getTime("lk_jam");
                        String lk_deskripsi_kerusakan = rs.getString("lk_deskripsi_kerusakan");

                        CctvModel cctvModel_lk = new CctvModel(rs.getString("model_lk_id"), rs.getString("model_lk_nama"), rs.getString("model_lk_manufaktur"), rs.getString("model_lk_spesifikasi"), rs.getInt("model_lk_umur_ekonomis"));
                        CctvUnit cctvUnit_lk = new CctvUnit(rs.getString("cctv_lk_id"), rs.getString("cctv_lk_lokasi"), rs.getString("cctv_lk_status"), rs.getString("cctv_lk_keterangan"), cctvModel_lk);
                        Teknisi teknisi_lk = new Teknisi(rs.getString("teknisi_lk_id"), rs.getString("teknisi_lk_nama"), rs.getString("teknisi_lk_kontak"));

                        logKerusakan = new LogKerusakan(lk_id_log, cctvUnit_lk, teknisi_lk, lk_tanggal, (lk_jam != null) ? lk_jam.toString() : null, lk_deskripsi_kerusakan);
                    }

                    String idKomponen = rs.getString("id_komponen");
                    String namaKomponen = rs.getString("nama_komponen");
                    String satuan = rs.getString("satuan");
                    int stok = rs.getInt("stok");
                    BigDecimal hargaSatuan = rs.getBigDecimal("harga_satuan");
                    Komponen komponen = new Komponen(idKomponen, namaKomponen, satuan, stok, hargaSatuan);

                    return new LogKomponenDipakai(foundId, logMaintenance, logKerusakan, komponen, jumlahDipakai, biaya);

                } else {
                    return null;
                }
            }
        }
    }
    
    
    public boolean updateLogKomponenDipakai(LogKomponenDipakai logKomponen) throws SQLException {
        String sql = "UPDATE log_komponen_dipakai SET id_log_maintenance = ?, id_log_kerusakan = ?, id_komponen = ?, jumlah_dipakai = ?, biaya = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (logKomponen.getLogMaintenance() != null) {
                pstmt.setString(1, String.valueOf(logKomponen.getLogMaintenance().getIdLog()));
            } else {
                pstmt.setNull(1, java.sql.Types.VARCHAR);
            }

            if (logKomponen.getLogKerusakan() != null && logKomponen.getLogKerusakan().getIdLog() != null) {
                pstmt.setString(2, String.valueOf(logKomponen.getLogKerusakan().getIdLog()));
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setString(3, logKomponen.getKomponen().getIdKomponen());
            pstmt.setInt(4, logKomponen.getJumlahDipakai());
            pstmt.setBigDecimal(5, logKomponen.getBiaya());
            pstmt.setInt(6, logKomponen.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat mengupdate LogKomponenDipakai: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLogKomponenDipakai(int id) throws SQLException {
        String sql = "DELETE FROM log_komponen_dipakai WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus LogKomponenDipakai: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
