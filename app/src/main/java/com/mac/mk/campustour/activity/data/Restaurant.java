package com.mac.mk.campustour.activity.data;

import java.io.Serializable;

/**
 * Created by mk on 2017. 6. 3..
 */

public class Restaurant implements Serializable{

    // 음식점 이름
    private String name;
    // 음식점 주소
    private String address;
    // 음식점 위도
    private Double latitude;
    // 음식점 경도
    private Double longitude;

    public Restaurant(String name, String address, Double latitude, Double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Restaurant(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
