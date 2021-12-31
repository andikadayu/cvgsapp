package com.cvgs.cvgsapp.model;

public class BrosurModel {
    String id_brosur,tgl_brosur;

    public BrosurModel(String id_brosur,  String tgl_brosur) {
        this.id_brosur = id_brosur;
        this.tgl_brosur = tgl_brosur;
    }

    public String getId_brosur() {
        return id_brosur;
    }

    public void setId_brosur(String id_brosur) {
        this.id_brosur = id_brosur;
    }

    public String getTgl_brosur() {
        return tgl_brosur;
    }

    public void setTgl_brosur(String tgl_brosur) {
        this.tgl_brosur = tgl_brosur;
    }
}
