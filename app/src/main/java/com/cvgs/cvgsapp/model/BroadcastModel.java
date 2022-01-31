package com.cvgs.cvgsapp.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class BroadcastModel implements Serializable {
    private String id_detail, name;

    public BroadcastModel(String id_detail, String name) {
        this.id_detail = id_detail;
        this.name = name;
    }

    public String getId_detail() {
        return id_detail;
    }

    public void setId_detail(String id_detail) {
        this.id_detail = id_detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
