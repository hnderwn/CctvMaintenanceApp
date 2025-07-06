package com.kelompok4.cctvapp;

public class User {
    private int idUser;
    private String username;
    private String passwordHash;
    private String namaLengkap;
    private String role;

    // Konstruktor
    public User() {
    }

    public User(String username, String passwordHash, String namaLengkap, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }
    
    public User(int idUser, String username, String passwordHash, String namaLengkap, String role) {
        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    // Getter dan Setter
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
               "idUser=" + idUser +
               ", username='" + username + '\'' +
               ", namaLengkap='" + namaLengkap + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}