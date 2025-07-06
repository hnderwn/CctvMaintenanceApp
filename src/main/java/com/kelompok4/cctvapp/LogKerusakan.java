package com.kelompok4.cctvapp;

import java.util.Date;

public class LogKerusakan {
    private Integer idLog;
    private CctvUnit cctvUnit;
    private Teknisi teknisi;
    private Date tanggal;
    private String jam;
    private String deskripsi;

    public LogKerusakan(Integer idLog, CctvUnit cctvUnit, Teknisi teknisi, Date tanggal, String jam, String deskripsi) {
        this.idLog = idLog;
        this.cctvUnit = cctvUnit;
        this.teknisi = teknisi;
        this.tanggal = tanggal;
        this.jam = jam;
        this.deskripsi = deskripsi;
    }

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public CctvUnit getCctvUnit() {
        return cctvUnit;
    }

    public void setCctvUnit(CctvUnit cctvUnit) {
        this.cctvUnit = cctvUnit;
    }

    public Teknisi getTeknisi() {
        return teknisi;
    }

    public void setTeknisi(Teknisi teknisi) {
        this.teknisi = teknisi;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
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