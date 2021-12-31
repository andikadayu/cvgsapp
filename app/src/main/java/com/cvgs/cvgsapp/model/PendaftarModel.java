package com.cvgs.cvgsapp.model;

public class PendaftarModel {
    private String id_detail,nama,no_telp,email,alamat;

    public PendaftarModel(String id_detail, String nama, String no_telp, String email, String alamat) {
        this.id_detail = id_detail;
        this.nama = nama;
        this.no_telp = no_telp;
        this.email = email;
        this.alamat = alamat;
    }

    public String getId_detail() {
        return id_detail;
    }

    public String getNama() {
        return nama;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public String getEmail() {
        return email;
    }

    public String getAlamat() {
        return alamat;
    }
}
