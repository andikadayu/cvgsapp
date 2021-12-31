package com.cvgs.cvgsapp.model;

public class OpenModel {

    public String namePackage,linkPackage,licensePackage;

    public OpenModel(String namePackage, String linkPackage, String licensePackage) {
        this.namePackage = namePackage;
        this.linkPackage = linkPackage;
        this.licensePackage = licensePackage;
    }

    public String getNamePackage() {
        return namePackage;
    }

    public void setNamePackage(String namePackage) {
        this.namePackage = namePackage;
    }

    public String getLinkPackage() {
        return linkPackage;
    }

    public void setLinkPackage(String linkPackage) {
        this.linkPackage = linkPackage;
    }

    public String getLicensePackage() {
        return licensePackage;
    }

    public void setLicensePackage(String licensePackage) {
        this.licensePackage = licensePackage;
    }
}
