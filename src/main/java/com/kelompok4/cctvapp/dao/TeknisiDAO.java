package com.kelompok4.cctvapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kelompok4.cctvapp.Teknisi;
import com.kelompok4.cctvapp.util.DbConnector;

/**
 * Kelas Data Access Object (DAO) untuk mengelola data Teknisi di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas Komponen.
 */
public class TeknisiDAO {

    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }
    
    public int getTotalTeknisi() throws SQLException {
        String sql = "SELECT COUNT(*) FROM teknisi";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean insertTeknisi(Teknisi teknisi) throws SQLException {
        String sql = "INSERT INTO teknisi (id_teknisi, nama_teknisi, kontak) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teknisi.getIdTeknisi());
            pstmt.setString(2, teknisi.getNamaTeknisi());
            pstmt.setString(3, teknisi.getKontak());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat menyimpan Teknisi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Teknisi> getAllTeknisi() throws SQLException {
        List<Teknisi> teknisiList = new ArrayList<>();
        String sql = "SELECT id_teknisi, nama_teknisi, kontak FROM teknisi";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String idTeknisi = rs.getString("id_teknisi");
                String namaTeknisi = rs.getString("nama_teknisi");
                String kontak = rs.getString("kontak");

                Teknisi teknisi = new Teknisi(idTeknisi, namaTeknisi, kontak);
                teknisiList.add(teknisi);
            }
            return teknisiList;

        }
    }

    public Teknisi findTeknisiById(String idTeknisi) throws SQLException {
        String sql = "SELECT id_teknisi, nama_teknisi, kontak FROM teknisi WHERE id_teknisi = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idTeknisi);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String foundIdTeknisi = rs.getString("id_teknisi");
                    String namaTeknisi = rs.getString("nama_teknisi");
                    String kontak = rs.getString("kontak");

                    Teknisi teknisi = new Teknisi(foundIdTeknisi, namaTeknisi, kontak);
                    return teknisi;
                } else {
                    return null;
                }
            }
        }
    }

      public boolean updateTeknisi(Teknisi teknisi) throws SQLException {
        String sql = "UPDATE teknisi SET nama_teknisi = ?, kontak = ? WHERE id_teknisi = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teknisi.getNamaTeknisi());
            pstmt.setString(2, teknisi.getKontak());
            pstmt.setString(3, teknisi.getIdTeknisi()); 
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate Teknisi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

      
    public boolean deleteTeknisi(String idTeknisi) throws SQLException {
        String sql = "DELETE FROM teknisi WHERE id_teknisi = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idTeknisi);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus Teknisi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}