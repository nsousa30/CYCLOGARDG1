package com.example.cyclogard;

public class Base {
    private int id;
    private String data;
    private String latitude;
    private String longitude;

    public Base(int id, String data, String latitude, String longitude) {
        this.id = id;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Ocorrência nº" + id +
                " | " + data + " | " + latitude + " " + longitude ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
