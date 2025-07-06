package com.kelompok4.cctvapp;

import java.math.BigDecimal;

/**
 * Kelas untuk merepresentasikan Komponen atau Sparepart CCTV.
 * Sesuai dengan tabel komponen di rancangan database.
 */
public class Komponen {

    private String idKomponen;
    private String namaKomponen;
    private String satuan;
    private int stok;
    private BigDecimal hargaSatuan;

    public Komponen(String idKomponen, String namaKomponen, String satuan,
                    int stok, BigDecimal hargaSatuan) {
        this.idKomponen = idKomponen;
        this.namaKomponen = namaKomponen;
        this.satuan = satuan;
        this.stok = stok;
        this.hargaSatuan = hargaSatuan;
    }

    public String getIdKomponen() { return idKomponen; }
    public String getNamaKomponen() { return namaKomponen; }
    public String getSatuan() { return satuan; }
    public int getStok() { return stok; }
    public BigDecimal getHargaSatuan() { return hargaSatuan; }

    public void setNamaKomponen(String namaKomponen) { this.namaKomponen = namaKomponen; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public void setStok(int stok) { this.stok = stok; }
    public void setHargaSatuan(BigDecimal hargaSatuan) { this.hargaSatuan = hargaSatuan; }
    
    @Override // Ini menandakan kita menimpa method dari Object class
    public String toString() {
        return idKomponen +  " - " + namaKomponen +  " - " + "Stok: " + stok + " - " + " Rp." + hargaSatuan;
    }
}