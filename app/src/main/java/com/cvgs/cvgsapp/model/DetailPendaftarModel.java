package com.cvgs.cvgsapp.model;

public class DetailPendaftarModel {
    String id_daftar,logo,judul ,tool,status;

    public DetailPendaftarModel(String id_daftar, String logo, String judul, String tool, String status) {
        this.id_daftar = id_daftar;
        this.logo = logo;
        this.judul = judul;
        this.tool = tool;
        this.status = status;
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

    public String getTool() {
        return tool;
    }

    public String getStatus() {
        return status;
    }
}
