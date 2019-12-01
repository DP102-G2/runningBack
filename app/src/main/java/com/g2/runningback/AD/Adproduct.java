package com.g2.runningback.AD;

import java.io.Serializable;

public class Adproduct implements Serializable {
    private String pro_no;
    private int ad_no;

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public void setFields(String pro_no,int ad_no) {
        this.pro_no = pro_no;
        this.ad_no = ad_no;

    }

    public Adproduct(String pro_no) {
        this.pro_no = pro_no;
    }

    public String getPro_no() {
        return pro_no;
    }

    public int getAd_no() {
        return ad_no;
    }

    public void setAd_no(int ad_no) {
        this.ad_no = ad_no;
    }
}