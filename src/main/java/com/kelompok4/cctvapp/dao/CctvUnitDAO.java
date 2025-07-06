package com.kelompok4.cctvapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kelompok4.cctvapp.util.DbConnector;

import java.util.ArrayList;
import java.util.List;

import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.CctvModel;


public class CctvUnitDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }

    public int getJumlahByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cctv WHERE status = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public boolean insertCctvUnit(CctvUnit unit) throws SQLException {
        try (Connection conn = getConnection()) {
            return insertCctvUnit(unit, conn);
        }
    }
    
    public boolean insertCctvUnit(CctvUnit unit, Connection conn) throws SQLException {
        String sql = "INSERT INTO cctv (id_cctv, lokasi, status, keterangan, id_model) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, unit.getIdCctv());
            pstmt.setString(2, unit.getLokasi());
            pstmt.setString(3, unit.getStatus());
            pstmt.setString(4, unit.getKeterangan());
            
            if (unit.getModel() != null) {
                pstmt.setString(5, unit.getModel().getIdModel());
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    
    
    public List<CctvUnit> getAllCctvUnits() throws SQLException {
        List<CctvUnit> units = new ArrayList<>();
        String sql = "SELECT " +
                     "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                     "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th " +
                     "FROM cctv c " +
                     "JOIN cctv_models m ON c.id_model = m.id_model";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String idCctv = rs.getString("id_cctv");
                String lokasi = rs.getString("lokasi");
                String status = rs.getString("status");
                String keterangan = rs.getString("keterangan");

                String idModel = rs.getString("id_model");
                String namaModel = rs.getString("nama_model");
                String manufaktur = rs.getString("manufaktur");
                String spesifikasi = rs.getString("spesifikasi");
                int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                CctvModel model = new CctvModel(idModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);

                CctvUnit unit = new CctvUnit(idCctv, lokasi, status, keterangan, model);
                units.add(unit);
            }
            return units;

        }
    }

    public CctvUnit findCctvUnitById(String idCctv) throws SQLException {
        String sql = "SELECT " +
                     "c.id_cctv, c.lokasi, c.status, c.keterangan, " +
                     "m.id_model, m.nama_model, m.manufaktur, m.spesifikasi, m.umur_ekonomis_th " +
                     "FROM cctv c " +
                     "JOIN cctv_models m ON c.id_model = m.id_model " +
                     "WHERE c.id_cctv = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idCctv);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String foundIdCctv = rs.getString("id_cctv");
                    String lokasi = rs.getString("lokasi");
                    String status = rs.getString("status");
                    String keterangan = rs.getString("keterangan");

                    String idModel = rs.getString("id_model");
                    String namaModel = rs.getString("nama_model");
                    String manufaktur = rs.getString("manufaktur");
                    String spesifikasi = rs.getString("spesifikasi");
                    int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                    CctvModel model = new CctvModel(idModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);

                    CctvUnit unit = new CctvUnit(foundIdCctv, lokasi, status, keterangan, model);

                    return unit;
                } else {
                    return null;
                }
            }
        }
    }
    
    public boolean updateCctvUnit(CctvUnit unit) throws SQLException {
        System.out.println("DAO: updateCctvUnit (versi mandiri) dipanggil.");
        try (Connection conn = getConnection()) {
            return updateCctvUnit(unit, conn);
        }
    }
    
    public boolean updateCctvUnit(CctvUnit unit, Connection conn) throws SQLException {
        String sql = "UPDATE cctv SET lokasi = ?, status = ?, keterangan = ?, id_model = ? WHERE id_cctv = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit.getLokasi());
            pstmt.setString(2, unit.getStatus());
            pstmt.setString(3, unit.getKeterangan());
            pstmt.setString(4, unit.getModel().getIdModel());
            pstmt.setString(5, unit.getIdCctv());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteCctvUnit(String idCctv) throws SQLException {
        String sql = "DELETE FROM cctv WHERE id_cctv = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idCctv);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus CctvUnit: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
