package net.mednikov.BikeShare.bikes;

public class Bike {

    private String bikeId;
    private double lon;
    private double lat;
    private boolean busy;

    public Bike(){}

    public Bike(String bikeId, double lon, double lat, boolean busy) {
        this.bikeId = bikeId;
        this.lon = lon;
        this.lat = lat;
        this.busy = busy;
    }

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
