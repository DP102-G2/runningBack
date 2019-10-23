package com.g2.runningback.User;

import java.io.Serializable;

public class User implements Serializable {

    private String user_no;
    private String user_name;
    private String user_id;
    private String user_pw;
    private String user_email;
    private String user_regtime;

    public User(String user_no,String user_name,String user_id,String user_pw,String user_email,String user_regtime){
        this.user_no = user_no;
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.user_email = user_email;
        this.user_regtime = user_regtime;
    }

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_regtime() {
        return user_regtime;
    }

    public void setUser_regtime(String user_regtime) {
        this.user_regtime = user_regtime;
    }
}
