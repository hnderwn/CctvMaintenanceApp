package com.kelompok4.cctvapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_cctv_maintenance"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 


    public static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}