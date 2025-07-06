package com.kelompok4.cctvapp;

import com.kelompok4.cctvapp.ui.FormLogin; 
import javax.swing.SwingUtilities;     

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo; 
import javax.swing.UnsupportedLookAndFeelException;
import com.formdev.flatlaf.FlatLightLaf;

public class CctvMaintenanceApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatLightLaf.setup(); 
                } catch( Exception ex ) {
                System.err.println( "Failed to initialize FlatLaf. Using default." );
                }
                
                FormLogin formLogin = new FormLogin();
                formLogin.setVisible(true);
            }
        });
    }
}