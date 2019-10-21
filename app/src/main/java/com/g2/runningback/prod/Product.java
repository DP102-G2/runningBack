package com.g2.runningback.prod;

import java.io.Serializable;

public class Product implements Serializable {
    String pro_no;
    String cat_no;
    String pro_name;
    String pro_desc;
    int pro_price;
    int pro_stock;
    int pro_Sale;
    String pro_info;
    byte[]pro_image;
    byte[] pro_image2;
    byte[] pro_image3;


    public void Clear(){
        pro_no="";
        cat_no="";
        pro_name="";
        pro_desc="";
        pro_price=0;
        pro_Sale=0;
        pro_stock=0;
        pro_info="";
        pro_image3=null;
        pro_image2=null;
        pro_image=null;
    }

    public Product(String pro_no, String cat_no, String pro_name, String pro_desc) {
        this.pro_no = pro_no;
        this.cat_no = cat_no;
        this.pro_name = pro_name;
        this.pro_desc = pro_desc;
    }

    public void savePage2(int pro_price, int pro_stock, int pro_Sale, String pro_info) {
        this.pro_price = pro_price;
        this.pro_stock = pro_stock;
        this.pro_Sale = pro_Sale;
        this.pro_info = pro_info;
    }

    public void clearImage(){
        pro_image=null;
        pro_image2=null;
        pro_image3=null;
    }

    public String getPro_no() {
        return pro_no;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public String getCat_no() {
        return cat_no;
    }

    public void setCat_no(String cat_no) {
        this.cat_no = cat_no;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getPro_desc() {
        return pro_desc;
    }

    public void setPro_desc(String pro_desc) {
        this.pro_desc = pro_desc;
    }

    public int getPro_price() {
        return pro_price;
    }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public int getPro_stock() {
        return pro_stock;
    }

    public void setPro_stock(int pro_stock) {
        this.pro_stock = pro_stock;
    }

    public int getPro_Sale() {
        return pro_Sale;
    }

    public void setPro_Sale(int pro_Sale) {
        this.pro_Sale = pro_Sale;
    }

    public String getPro_info() {
        return pro_info;
    }

    public void setPro_info(String pro_info) {
        this.pro_info = pro_info;
    }

    public byte[] getPro_image() {
        return pro_image;
    }

    public void setPro_image(byte[] pro_image) {
        this.pro_image = pro_image;
    }

    public byte[] getPro_image2() {
        return pro_image2;
    }

    public void setPro_image2(byte[] pro_image2) {
        this.pro_image2 = pro_image2;
    }

    public byte[] getPro_image3() {
        return pro_image3;
    }

    public void setPro_image3(byte[] pro_image3) {
        this.pro_image3 = pro_image3;
    }

    public Product(String pro_no, String cat_no, String pro_name, String pro_desc, int pro_price, int pro_stock, int pro_Sale, String pro_info, byte[] pro_image, byte[] pro_image2, byte[] pro_image3) {
        this.pro_no = pro_no;
        this.cat_no = cat_no;
        this.pro_name = pro_name;
        this.pro_desc = pro_desc;
        this.pro_price = pro_price;
        this.pro_stock = pro_stock;
        this.pro_Sale = pro_Sale;
        this.pro_info = pro_info;
        this.pro_image = pro_image;
        this.pro_image2 = pro_image2;
        this.pro_image3 = pro_image3;
    }

    public Product(String pro_no, String cat_no, String pro_name, String pro_desc, int pro_price, int pro_stock,
                   int pro_Sale, String pro_info) {
        super();
        this.pro_no = pro_no;
        this.cat_no = cat_no;
        this.pro_name = pro_name;
        this.pro_desc = pro_desc;
        this.pro_price = pro_price;
        this.pro_stock = pro_stock;
        this.pro_Sale = pro_Sale;
        this.pro_info = pro_info;
    }

}
