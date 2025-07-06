package com.kelompok4.cctvapp;

import com.kelompok4.cctvapp.CctvModel;

/**
 * Kelas untuk merepresentasikan Unit CCTV fisik yang terpasang.
 * Sesuai dengan tabel cctv di rancangan database.
 */
public class CctvUnit {

    private String idCctv;
    private String lokasi;
    private String status;
    private String keterangan;
    private CctvModel model;

    public CctvUnit(String idCctv, String lokasi, String status,
                    String keterangan, CctvModel model) {
        this.idCctv = idCctv;
        this.lokasi = lokasi;
        this.status = status;
        this.keterangan = keterangan;
        this.model = model;
    }

    public String getIdCctv() { return idCctv; }
    public String getLokasi() { return lokasi; }
    public String getStatus() { return status; }
    public String getKeterangan() { return keterangan; }
    public CctvModel getModel() { return model; }

    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setStatus(String status) { this.status = status; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public void setModel(CctvModel model) { this.model = model; }
    
    @Override // Ini menandakan kita menimpa method dari Object class
    public String toString() {
        return idCctv + " - " + model + " - " + lokasi;
    }
}