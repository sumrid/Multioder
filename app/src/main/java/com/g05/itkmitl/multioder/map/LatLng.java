package com.g05.itkmitl.multioder.map;

import java.io.Serializable;

public class LatLng implements Serializable {
    private double latitude;
    private double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng(){
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public com.google.android.gms.maps.model.LatLng getGoogleLatLng() {
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }
}
