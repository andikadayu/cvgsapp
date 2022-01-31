package com.cvgs.cvgsapp.model;

public class PaketModel {
    String id_paket, id_layanan, nama_paket, isi_paket, harga_paket, logo_paket, color_paket;

    public PaketModel(String id_paket, String id_layanan, String nama_paket, String isi_paket, String harga_paket, String logo_paket, String color_paket) {
        this.id_paket = id_paket;
        this.id_layanan = id_layanan;
        this.nama_paket = nama_paket;
        this.isi_paket = isi_paket;
        this.harga_paket = harga_paket;
        this.logo_paket = logo_paket;
        this.color_paket = color_paket;
    }

    public String getId_paket() {
        return id_paket;
    }

    public String getId_layanan() {
        return id_layanan;
    }

    public String getNama_paket() {
        return nama_paket;
    }

    public String getIsi_paket() {
        return isi_paket;
    }

    public String getHarga_paket() {
        return harga_paket;
    }

    public String getLogo_paket() {
        return logo_paket;
    }

    public String getColor_paket() {
        return color_paket;
    }
}
