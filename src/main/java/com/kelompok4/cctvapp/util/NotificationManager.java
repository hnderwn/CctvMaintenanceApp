// Simpan di: com/kelompok4/cctvapp/util/NotificationManager.java
package com.kelompok4.cctvapp.util;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Kelas 'pembantu' (utility) untuk menampilkan dialog pop-up standar di seluruh aplikasi.
 * Tujuannya untuk standarisasi dan mempermudah pemanggilan.
 */
public final class NotificationManager {

    // Bikin constructor private biar kelas ini gak bisa dibuat objeknya pake 'new'
    private NotificationManager() {}

    /**
     * Menampilkan dialog pop-up untuk pesan SUKSES.
     * @param parentComponent Komponen induk (biasanya 'this' dari panel)
     * @param message Pesan yang ingin ditampilkan.
     */
    public static void showSuccess(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Sukses", // Judul standar
                JOptionPane.INFORMATION_MESSAGE); // Ikon standar (i)
    }

    /**
     * Menampilkan dialog pop-up untuk pesan ERROR.
     * @param parentComponent Komponen induk
     * @param message Pesan error yang ingin ditampilkan.
     */
    public static void showError(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Error", // Judul standar
                JOptionPane.ERROR_MESSAGE); // Ikon standar (X)
    }

    /**
     * Menampilkan dialog pop-up untuk pesan PERINGATAN (Warning).
     * @param parentComponent Komponen induk
     * @param message Pesan peringatan yang ingin ditampilkan.
     */
    public static void showWarning(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(
                parentComponent,
                message,
                "Peringatan", // Judul standar
                JOptionPane.WARNING_MESSAGE); // Ikon standar (!)
    }

    /**
     * Menampilkan dialog pop-up untuk KONFIRMASI (Ya/Tidak).
     * @param parentComponent Komponen induk
     * @param message Pesan konfirmasi (misal: "Yakin ingin menghapus?").
     * @return true jika user menekan YES, false jika tidak.
     */
    public static boolean showConfirm(Component parentComponent, String message) {
        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                message,
                "Konfirmasi", // Judul standar
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}