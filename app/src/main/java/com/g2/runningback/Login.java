package com.g2.runningback;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Login implements Serializable {
    private String password,id;

    public Login(String id, String password) {
        super();
        this.password = password;
        this.id = id ;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "\nid" + id +  "\npassword: " + password  ;
        return text;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

}