package com.kelompok4.cctvapp;

/**
 * Kelas untuk merepresentasikan Teknisi.
 * Sesuai dengan tabel teknisi di rancangan database.
 */
public class Teknisi {

    private String idTeknisi;
    private String namaTeknisi;
    private String kontak;

    public Teknisi(String idTeknisi, String namaTeknisi, String kontak) {
        this.idTeknisi = idTeknisi;
        this.namaTeknisi = namaTeknisi;
        this.kontak = kontak;
    }

    public String getIdTeknisi() { return idTeknisi; }
    public String getNamaTeknisi() { return namaTeknisi; }
    public String getKontak() { return kontak; }

    public void setNamaTeknisi(String namaTeknisi) { this.namaTeknisi = namaTeknisi; }
    public void setKontak(String kontak) { this.kontak = kontak; }
    
    @Override
    public String toString() {
        return idTeknisi + " | " + namaTeknisi + " | " + kontak;
    }
}