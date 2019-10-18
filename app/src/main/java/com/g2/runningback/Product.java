package com.g2.runningback;

import java.io.Serializable;

public class Product implements Serializable {
    private int pro_price;
    private String pro_name;
    private String pro_desc;
    private int pro_image;
    private String pro_no;
    private int pro_stock;

    public Product() {
    }


    public Product(String pro_no, String pro_name, int pro_stock) {
        this.pro_name = pro_name;
        this.pro_stock = pro_stock;
        this.pro_no = pro_no;
    }

    public void setFields(String pro_no, String pro_name, int pro_stock) {
        this.pro_no = pro_no;
        this.pro_name = pro_name;
        this.pro_stock = pro_stock;
    }

    public int getPro_price() {
        return pro_price;
    }

    public String getPro_name() {
        return pro_name;
    }

    public String getPro_desc() {
        return pro_desc;
    }

    public int getPro_image() {
        return pro_image;
    }

    public String getPro_no() {
        return pro_no;
    }

    public int getPro_stock() { return pro_stock; }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public void setPro_desc(String pro_desc) {
        this.pro_desc = pro_desc;
    }

    public void setPro_image(int pro_image) {
        this.pro_image = pro_image;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public void setPro_stock(int pro_stock) { this.pro_stock = pro_stock; }
}
