package com.kelompok4.cctvapp;

/**
 * Kelas untuk merepresentasikan Model CCTV.
 * Sesuai dengan tabel cctv_models di rancangan database.
 */
public class CctvModel {

    private String idModel;
    private String namaModel;
    private String manufaktur;
    private String spesifikasi;
    private int umurEkonomisTh;

    public CctvModel(String idModel, String namaModel, String manufaktur,
                     String spesifikasi, int umurEkonomisTh) {
        this.idModel = idModel;
        this.namaModel = namaModel;
        this.manufaktur = manufaktur;
        this.spesifikasi = spesifikasi;
        this.umurEkonomisTh = umurEkonomisTh;
    }

    public String getIdModel() { return idModel; }
    public String getNamaModel() { return namaModel; }
    public String getManufaktur() { return manufaktur; }
    public String getSpesifikasi() { return spesifikasi; }
    public int getUmurEkonomisTh() { return umurEkonomisTh; }

    public void setNamaModel(String namaModel) { this.namaModel = namaModel; }
    public void setManufaktur(String manufaktur) { this.manufaktur = manufaktur; }
    public void setSpesifikasi(String spesifikasi) { this.spesifikasi = spesifikasi; }
    public void setUmurEkonomisTh(int umurEkonomisTh) { this.umurEkonomisTh = umurEkonomisTh; }

    @Override
    public String toString() {
        return idModel + " - " + namaModel + " (" + manufaktur + ")";
}
}