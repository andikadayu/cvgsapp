package com.cvgs.cvgsapp.model;

public class DetailPembayaranModel {
    String id_transaksi, image, nominal, tgl_transaksi, status;

    public DetailPembayaranModel(String id_transaksi, String image, String nominal, String tgl_transaksi, String status) {
        this.id_transaksi = id_transaksi;
        this.image = image;
        this.nominal = nominal;
        this.tgl_transaksi = tgl_transaksi;
        this.status = status;
    }

    public String getId_transaksi() {
        return id_transaksi;
    }

    public String getImage() {
        return image;
    }

    public String getNominal() {
        return nominal;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public String getStatus() {
        return status;
    }
}
