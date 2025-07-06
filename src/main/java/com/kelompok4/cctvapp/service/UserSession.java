package com.kelompok4.cctvapp.service;

import com.kelompok4.cctvapp.User;

/*
 * =================================================================================
 * RINGKASAN METHOD (USER SESSION)
 * =================================================================================
 * Kelas ini mengelola sesi user menggunakan Singleton Pattern. Tujuannya agar
 * data user yang login bisa diakses dari mana saja tanpa membuat objek baru.
 * ---------------------------------------------------------------------------------
 * getInstance()      - Method statis untuk mendapatkan satu-satunya instance dari kelas ini.
 * login(User)        - Memulai sesi dengan menyimpan data user yang login.
 * logout()           - Mengakhiri sesi dengan menghapus data user.
 * getUser()          - Mengambil objek User yang sedang login.
 * getRole()          - Mengambil role (String) dari user yang sedang login.
 * getNamaLengkap()   - Mengambil nama lengkap dari user yang sedang login.
 * isLoggedIn()       - Mengecek apakah ada user yang sedang login (true/false).
 * =================================================================================
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {
        // Constructor privat untuk mencegah instansiasi dari luar
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        System.out.println("SESI DIMULAI: User '" + user.getUsername() + "' dengan role '" + user.getRole() + "' telah login.");
    }

    public void logout() {
        if (isLoggedIn()) {
            System.out.println("SESI BERAKHIR: User '" + this.currentUser.getUsername() + "' telah logout.");
            this.currentUser = null;
        }
    }

    public User getUser() {
        return this.currentUser;
    }

    public String getRole() {
        if (isLoggedIn()) {
            return this.currentUser.getRole();
        }
        return null;
    }

    public String getNamaLengkap() {
        if (isLoggedIn()) {
            return this.currentUser.getNamaLengkap();
        }
        return null;
    }

    public boolean isLoggedIn() {
        return this.currentUser != null;
    }
}