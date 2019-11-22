package com.g2.runningback;

import java.io.Serializable;

public class Promotion implements Serializable {

    private String pro_no;
    private int prom_no;

    public Promotion(String pro_no, int prom_no) {
        this.pro_no = pro_no;
        this.prom_no = prom_no;
    }


    public void setFields(String pro_no,int prom_no) {
        this.pro_no = pro_no;
        this.prom_no = prom_no;

    }

    public String getPro_no() {
        return pro_no;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public int getProm_no() {
        return prom_no;
    }

    public void setProm_no(int prom_no) {
        this.prom_no = prom_no;
    }
}