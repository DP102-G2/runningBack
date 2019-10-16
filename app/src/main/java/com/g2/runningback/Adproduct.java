package com.g2.runningback;

import java.io.Serializable;

public class Adproduct implements Serializable {
    private String pro_no;



    public Adproduct(String pro_no) {
        this.pro_no = pro_no;
    }

    public String getPro_no() {
        return pro_no;
    }


    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

}