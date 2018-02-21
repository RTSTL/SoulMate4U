package com.rtstl.soulmate4u;

/**
 * Created by RTSTL17 on 18-01-2018.
 */

public class RestaurantList {

    String id = "", name = "";
    double latitude = 0.0, longitude = 0.0;
    String address = "";
    boolean isOpened = true;
    String icon = "";
    double rating = 0.0f;
    int distance = 0;

    public RestaurantList(String id, String name, double latitude, double longitude, String address, boolean isOpened, String icon, double rating, int distance) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.isOpened = isOpened;
        this.icon = icon;
        this.rating = rating;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
