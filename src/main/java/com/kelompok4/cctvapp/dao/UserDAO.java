package com.kelompok4.cctvapp.dao;

import com.kelompok4.cctvapp.User;
import com.kelompok4.cctvapp.util.DbConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Kelas Data Access Object (DAO) untuk mengelola User di database.
 * Menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas Komponen.
 */
public class UserDAO {
    private Connection getConnection() throws SQLException {
        return DbConnector.getNewConnection();
    }

    /**
     * Mencari user berdasarkan username.
     * @param username Username yang dicari.
     * @return Objek User jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi error database.
     */
    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT id_user, username, password_hash, nama_lengkap, role FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setIdUser(rs.getInt("id_user"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setNamaLengkap(rs.getString("nama_lengkap"));
                    user.setRole(rs.getString("role"));
                }
            }
        }
        return user;
    }

    /**
     * Menyimpan user baru ke database.
     * Password sudah harus dalam bentuk hash sebelum dipanggil.
     * @param user Objek User yang akan disimpan.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi error database.
     */
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash()); // Simpan HASH, bukan password asli
            pstmt.setString(3, user.getNamaLengkap());
            pstmt.setString(4, user.getRole());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

}