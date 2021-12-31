package com.cvgs.cvgsapp.model;

public class ExploreModel {

    String id_explore,detail_explore,isi_explore,image_explore,color_explore;

    public ExploreModel(String id_explore, String detail_explore,String isi_explore, String image_explore, String color_explore) {
        this.id_explore = id_explore;
        this.detail_explore = detail_explore;
        this.isi_explore = isi_explore;
        this.image_explore = image_explore;
        this.color_explore = color_explore;
    }

    public String getId_explore() {
        return id_explore;
    }

    public void setId_explore(String id_explore) {
        this.id_explore = id_explore;
    }

    public String getDetail_explore() {
        return detail_explore;
    }

    public void setDetail_explore(String detail_explore) {
        this.detail_explore = detail_explore;
    }

    public String getImage_explore() {
        return image_explore;
    }

    public void setImage_explore(String image_explore) {
        this.image_explore = image_explore;
    }

    public String getColor_explore() {
        return color_explore;
    }

    public void setColor_explore(String color_explore) {
        this.color_explore = color_explore;
    }

    public String getIsi_explore() {
        return isi_explore;
    }

    public void setIsi_explore(String isi_explore) {
        this.isi_explore = isi_explore;
    }
}
