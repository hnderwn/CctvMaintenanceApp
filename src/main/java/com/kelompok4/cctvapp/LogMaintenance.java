package com.kelompok4.cctvapp;

import com.kelompok4.cctvapp.CctvUnit;
import com.kelompok4.cctvapp.Teknisi;

import java.util.Date;

/**
 * Kelas untuk merepresentasikan Log Transaksi Maintenance.
 * Sesuai dengan tabel log_maintenance di rancangan database.
 */
public class LogMaintenance {

    private Integer idLog;
    private CctvUnit cctvUnit;
    private Teknisi teknisi;
    private Date tanggal;
    private String jam;
    private String deskripsiLog;

    public LogMaintenance(Integer idLog, CctvUnit cctvUnit, Teknisi teknisi,
                          Date tanggal, String jam,
                          String deskripsiLog) {
        this.idLog = idLog;
        this.cctvUnit = cctvUnit;
        this.teknisi = teknisi;
        this.tanggal = tanggal;
        this.jam = jam;
        this.deskripsiLog = deskripsiLog;
    }
    
    public Integer getIdLog() {
        return idLog;
    }
    public CctvUnit getCctvUnit() { return cctvUnit; }
    public Teknisi getTeknisi() { return teknisi; }
    public Date getTanggal() { return tanggal; }
    public String getJam() { return jam; }
    public String getDeskripsiLog() { return deskripsiLog; }

    public void setCctvUnit(CctvUnit cctvUnit) { this.cctvUnit = cctvUnit; }
    public void setTeknisi(Teknisi teknisi) { this.teknisi = teknisi; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public void setJam(String jam) { this.jam = jam; }
    public void setDeskripsiLog(String deskripsiLog) { this.deskripsiLog = deskripsiLog; }
    public void setIdLog(int idLog) {
    this.idLog = idLog;
}
    
    @Override
    public String toString() {
        String idCctv = (cctvUnit != null) ? cctvUnit.getIdCctv() : "N/A";
        String namaTeknisi = (teknisi != null) ? teknisi.getNamaTeknisi() : "N/A";
        String lokasi = (cctvUnit != null) ? cctvUnit.getLokasi() : "N/A";

        String tglFormatted = (tanggal != null) ? new java.text.SimpleDateFormat("dd MMM yyyy").format(tanggal) : "N/A";

        return "ID: " + idLog + " | " + "CCTV: " + idCctv + " | " + lokasi + " | " + "Teknisi: " + namaTeknisi + " | " + tglFormatted;
    }
}
