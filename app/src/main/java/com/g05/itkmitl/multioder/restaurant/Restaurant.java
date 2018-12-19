package com.g05.itkmitl.multioder.restaurant;

import com.g05.itkmitl.multioder.map.LatLng;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String url;
    private String address;
    private String telephone;
    private String id;
    private LatLng location;

    public Restaurant(String name, String url, String id, String telephone) {
        this.name = name;
        this.url = url;
        this.id = id;
        this.telephone = telephone;
    }

    public Restaurant(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
