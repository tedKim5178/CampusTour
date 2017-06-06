package com.mac.mk.campustour.activity.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mk on 2017. 6. 2..
 */

public class Tour implements Serializable{

    // 작성자
    private String tWriter;
    // 투어 제목
    private String tName;
    // 학교 이름
    private String tSchoolName;
    // 학교 Key
    private String tKey;
    // 상세 내용
    private String tSpecification;
    // 맛집 리스트
    private ArrayList<Restaurant> restaurants;
    // 연락처
    private String tContact;
    // 찼는지 안찼는지
    private boolean occupied = false;

    public String gettContact() {
        return tContact;
    }

    public void settContact(String tContact) {
        this.tContact = tContact;
    }

    public String gettKey() {
        return tKey;
    }

    public void settKey(String tKey) {
        this.tKey = tKey;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public Tour(){

    }
    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    // 모집 인원
    private int capacity;


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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
