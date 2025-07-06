package com.kelompok4.cctvapp.util;

import org.mindrot.jbcrypt.BCrypt;


// Cuma buat bikin password hash user baru
public class BuatUser {

    public static void main(String[] args) {
        String passwordAsli = "hendi"; // Ganti ini dengan password yang lu mau

        String hashedPassword = BCrypt.hashpw(passwordAsli, BCrypt.gensalt());


        System.out.println("==============================================");
        System.out.println("BCrypt Hash Generator");
        System.out.println("==============================================");
        System.out.println("Password Asli : " + passwordAsli);
        System.out.println("Hasil Hash    : " + hashedPassword);
        System.out.println("\n--> COPY SEMUA HASIL HASH DI ATAS (mulai dari $2a...)");
        System.out.println("==============================================");
    }
}