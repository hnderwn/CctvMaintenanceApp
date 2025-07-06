package com.kelompok4.cctvapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import com.kelompok4.cctvapp.CctvModel;
import com.kelompok4.cctvapp.util.DbConnector;


/**
 * Kelas Data Access Object (DAO) untuk mengelola data CctvModel di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas CctvModel.
 */
public class CctvModelDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }

    public boolean insertCctvModel(CctvModel model) throws SQLException {
        String sql = "INSERT INTO cctv_models (id_model, nama_model, manufaktur, spesifikasi, umur_ekonomis_th) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, model.getIdModel());
            pstmt.setString(2, model.getNamaModel());
            pstmt.setString(3, model.getManufaktur());
            pstmt.setString(4, model.getSpesifikasi());
            pstmt.setInt(5, model.getUmurEkonomisTh());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan CctvModel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<CctvModel> getAllCctvModels() throws SQLException {
        List<CctvModel> models = new ArrayList<>();
        String sql = "SELECT id_model, nama_model, manufaktur, spesifikasi, umur_ekonomis_th FROM cctv_models";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String idModel = rs.getString("id_model");
                String namaModel = rs.getString("nama_model");
                String manufaktur = rs.getString("manufaktur");
                String spesifikasi = rs.getString("spesifikasi");
                int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                CctvModel model = new CctvModel(idModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);
                models.add(model);
            }
            return models;
        }
    }

    public CctvModel findCctvModelById(String idModel) throws SQLException {
         String sql = "SELECT id_model, nama_model, manufaktur, spesifikasi, umur_ekonomis_th FROM cctv_models WHERE id_model = ?";
         try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, idModel);
             try (ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     String foundIdModel = rs.getString("id_model");
                     String namaModel = rs.getString("nama_model");
                     String manufaktur = rs.getString("manufaktur");
                     String spesifikasi = rs.getString("spesifikasi");
                     int umurEkonomisTh = rs.getInt("umur_ekonomis_th");
                     CctvModel model = new CctvModel(foundIdModel, namaModel, manufaktur, spesifikasi, umurEkonomisTh);
                     return model;
                 } else {
                     return null;
                 }
             }
         }
     }

    public boolean updateCctvModel(CctvModel model) throws SQLException {
        String sql = "UPDATE cctv_models SET nama_model = ?, manufaktur = ?, spesifikasi = ?, umur_ekonomis_th = ? WHERE id_model = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, model.getNamaModel());
            pstmt.setString(2, model.getManufaktur());
            pstmt.setString(3, model.getSpesifikasi());
            pstmt.setInt(4, model.getUmurEkonomisTh());
            pstmt.setString(5, model.getIdModel());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate CctvModel: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteCctvModel(String idModel) throws SQLException { 
    String sql = "DELETE FROM cctv_models WHERE id_model = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, idModel);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        System.err.println("Error saat menghapus CctvModel: " + e.getMessage());
        e.printStackTrace();
        throw e; 
    }
}
}