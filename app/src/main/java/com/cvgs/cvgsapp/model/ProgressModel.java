package com.cvgs.cvgsapp.model;

public class ProgressModel {
    String id_daftar, judul, detail, logo, progress;

    public ProgressModel(String id_daftar, String judul, String detail, String logo, String progress) {
        this.id_daftar = id_daftar;
        this.judul = judul;
        this.detail = detail;
        this.logo = logo;
        this.progress = progress;
    }

    public String getId_daftar() {
        return id_daftar;
    }

    public String getJudul() {
        return judul;
    }

    public String getDetail() {
        return detail;
    }

    public String getLogo() {
        return logo;
    }

    public String getProgress() {
        return progress;
    }

}
