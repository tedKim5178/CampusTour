package com.mac.mk.campustour.activity.data;

import java.io.Serializable;

/**
 * Created by mk on 2017. 6. 2..
 */

public class User implements Serializable{

    public User(){

    }

    public User(String email, String name, int type){
        this.email = email;
        this.name = name;
        this.type = type;
    }
    // 이메일
    private String email;
    // 이름
    private String name;
    // 고등학생 or 대학생
    private int type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
