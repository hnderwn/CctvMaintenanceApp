package com.kelompok4.cctvapp.dao;

import com.kelompok4.cctvapp.Komponen;
import com.kelompok4.cctvapp.util.DbConnector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Data Access Object (DAO) untuk mengelola data Komponen di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas Komponen.
 */
public class KomponenDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }

    public boolean insertKomponen(Komponen komponen) {
        String sql = "INSERT INTO komponen (id_komponen, nama_komponen, satuan, stok, harga_satuan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, komponen.getIdKomponen());
            pstmt.setString(2, komponen.getNamaKomponen());
            pstmt.setString(3, komponen.getSatuan());
            pstmt.setInt(4, komponen.getStok());
            pstmt.setBigDecimal(5, komponen.getHargaSatuan());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat menyimpan Komponen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Komponen> getAllKomponen() throws SQLException {
        List<Komponen> komponenList = new ArrayList<>();
        String sql = "SELECT id_komponen, nama_komponen, satuan, stok, harga_satuan FROM komponen";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String idKomponen = rs.getString("id_komponen");
                String namaKomponen = rs.getString("nama_komponen");
                String satuan = rs.getString("satuan");
                int stok = rs.getInt("stok");
                BigDecimal hargaSatuan = rs.getBigDecimal("harga_satuan");

                Komponen komponen = new Komponen(idKomponen, namaKomponen, satuan, stok, hargaSatuan);
                komponenList.add(komponen);
            }
        }
        return komponenList;
    }

    public Komponen findKomponenById(String idKomponen) throws SQLException {
        String sql = "SELECT id_komponen, nama_komponen, satuan, stok, harga_satuan FROM komponen WHERE id_komponen = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idKomponen);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String foundIdKomponen = rs.getString("id_komponen");
                    String namaKomponen = rs.getString("nama_komponen");
                    String satuan = rs.getString("satuan");
                    int stok = rs.getInt("stok");
                    BigDecimal hargaSatuan = rs.getBigDecimal("harga_satuan");

                    return new Komponen(foundIdKomponen, namaKomponen, satuan, stok, hargaSatuan);
                } else {
                    return null;
                }
            }
        }
    }

    public boolean updateKomponen(Komponen komponen) {
        String sql = "UPDATE komponen SET nama_komponen = ?, satuan = ?, stok = ?, harga_satuan = ? WHERE id_komponen = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, komponen.getNamaKomponen());
            pstmt.setString(2, komponen.getSatuan());
            pstmt.setInt(3, komponen.getStok());
            pstmt.setBigDecimal(4, komponen.getHargaSatuan());
            pstmt.setString(5, komponen.getIdKomponen());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate Komponen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteKomponen(String idKomponen) {
        String sql = "DELETE FROM komponen WHERE id_komponen = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idKomponen);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat menghapus Komponen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}