package com.mac.mk.campustour.activity.data;

import java.io.Serializable;

/**
 * Created by mk on 2017. 6. 2..
 */

public class User implements Serializable{

    public User(){

    }

    public User(String email, String name, int type){
        this.Email = email;
        this.Name = name;
        this.type = type;
    }
    // 이메일
    private String Email;
    // 이름
    private String Name;
    // 고등학생 or 대학생
    private int type;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
