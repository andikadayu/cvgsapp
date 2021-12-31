package com.cvgs.cvgsapp.model;

public class PembayaranModel {
    private String id_daftar,logo,judul,detail,sisa;

    public PembayaranModel(String id_daftar, String logo, String judul, String detail, String sisa) {
        this.id_daftar = id_daftar;
        this.logo = logo;
        this.judul = judul;
        this.detail = detail;
        this.sisa = sisa;
    }

    public String getId_daftar() {
        return id_daftar;
    }

    public String getLogo() {
        return logo;
    }

    public String getJudul() {
        return judul;
    }

    public String getDetail() {
        return detail;
    }

    public String getSisa() {
        return sisa;
    }
}
