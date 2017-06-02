package com.mac.mk.campustour.activity.data;

/**
 * Created by mk on 2017. 6. 2..
 */

public class Tour {

    // 작성자
    private String tWriter;
    // 투어 제목
    private String tName;
    // 학교 이름
    private String tSchoolName;
    // 상세 내용
    private String tSpecification;
    // 맛집 이름
    private String tRestaurantName;
    // 맛집 주소
    private String tAddress;
    // 맛집 위도
    private double latitute;
    // 맛집 경도
    private double longitude;
    // 모집 인원
    private int capacity;

    public String gettAddress() {
        return tAddress;
    }

    public void settAddress(String tAddress) {
        this.tAddress = tAddress;
    }

    public String gettWriter() {
        return tWriter;
    }

    public void settWriter(String tWriter) {
        this.tWriter = tWriter;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String gettSchoolName() {
        return tSchoolName;
    }

    public void settSchoolName(String tSchoolName) {
        this.tSchoolName = tSchoolName;
    }

    public String gettSpecification() {
        return tSpecification;
    }

    public void settSpecification(String tSpecification) {
        this.tSpecification = tSpecification;
    }

    public String gettRestaurantName() {
        return tRestaurantName;
    }

    public void settRestaurantName(String tRestaurantName) {
        this.tRestaurantName = tRestaurantName;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
