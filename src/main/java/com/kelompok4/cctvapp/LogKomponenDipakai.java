package com.kelompok4.cctvapp;

import java.math.BigDecimal;

public class LogKomponenDipakai {
    private Integer id;
    private LogMaintenance logMaintenance;
    private LogKerusakan logKerusakan;
    private Komponen komponen;
    private int jumlahDipakai;
    private BigDecimal biaya;

    public LogKomponenDipakai() {
    }

    public LogKomponenDipakai(Integer id, LogMaintenance logMaintenance, LogKerusakan logKerusakan, Komponen komponen, int jumlahDipakai, BigDecimal biaya) {
        this.id = id;
        this.logMaintenance = logMaintenance;
        this.logKerusakan = logKerusakan;
        this.komponen = komponen;
        this.jumlahDipakai = jumlahDipakai;
        this.biaya = biaya;
    }

    // Getter dan Setter untuk semua atribut

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LogMaintenance getLogMaintenance() {
        return logMaintenance;
    }

    public void setLogMaintenance(LogMaintenance logMaintenance) {
        this.logMaintenance = logMaintenance;
    }

    public LogKerusakan getLogKerusakan() {
        return logKerusakan;
    }

    public void setLogKerusakan(LogKerusakan logKerusakan) {
        this.logKerusakan = logKerusakan;
    }

    public Komponen getKomponen() {
        return komponen;
    }

    public void setKomponen(Komponen komponen) {
        this.komponen = komponen;
    }

    public int getJumlahDipakai() {
        return jumlahDipakai;
    }

    public void setJumlahDipakai(int jumlahDipakai) {
        this.jumlahDipakai = jumlahDipakai;
    }
    
    public BigDecimal getBiaya() {
        return biaya;
    }
    
    public void setBiaya(BigDecimal biaya) {
        this.biaya = biaya;
    }

    @Override
    public String toString() {
        return "ID: " + id +
               ", Log Maintenance: " + (logMaintenance != null ? logMaintenance.getIdLog() : "null") +
               ", Log Kerusakan: " + (logKerusakan != null ? logKerusakan.getIdLog() : "null") +
               ", Komponen: " + (komponen != null ? komponen.getNamaKomponen() : "null") +
               ", Jumlah: " + jumlahDipakai;
    }
}   