package com.g2.runningback.Login;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Login implements Serializable {
    private String password,id;
    private int job_no;
    public Login(String id, String password, int job_no) {
        super();
        this.password = password;
        this.id = id ;
        this.job_no = job_no;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "\nid" + id +  "\npassword: " + password  +"\njob_no: " + job_no;
        return text;
    }

    public int getJob_no() {
        return job_no;
    }

    public void setJob_no(int job_no) {
        this.job_no = job_no;
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