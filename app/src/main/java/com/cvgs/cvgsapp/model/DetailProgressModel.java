package com.cvgs.cvgsapp.model;

public class DetailProgressModel {
    String id_progress, id_daftar, progress, isi_progress, tgl_progress, screenshot, video;

    public DetailProgressModel(String id_progress, String id_daftar, String progress, String isi_progress, String tgl_progress, String screenshot, String video) {
        this.id_progress = id_progress;
        this.id_daftar = id_daftar;
        this.progress = progress;
        this.isi_progress = isi_progress;
        this.tgl_progress = tgl_progress;
        this.screenshot = screenshot;
        this.video = video;
    }

    public String getId_progress() {
        return id_progress;
    }

    public String getId_daftar() {
        return id_daftar;
    }

    public String getProgress() {
        return progress;
    }

    public String getIsi_progress() {
        return isi_progress;
    }

    public String getTgl_progress() {
        return tgl_progress;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public String getVideo() {
        return video;
    }
}
