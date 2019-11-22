package com.g2.runningback;

import java.io.Serializable;



public class Orderlist implements Serializable {

    private int ord_no;
    private String pro_no;
    private String pro_name;
    private int ord_status;
    private String ord_date;
    private String address;
    private String phone;
    private int user_no;
    private String user_name;



    public void setFields(int ord_status,int ord_no) {
        this.ord_status = ord_status;
        this.ord_no=ord_no;
    }

    public Orderlist(int ord_no, String pro_no, String pro_name, int ord_status, String ord_date, String address, String phone, int user_no, String user_name) {
        this.ord_no = ord_no;
        this.pro_no = pro_no;
        this.pro_name = pro_name;
        this.ord_status = ord_status;
        this.ord_date = ord_date;
        this.address = address;
        this.phone = phone;
        this.user_no = user_no;
        this.user_name = user_name;
    }


    public int getOrd_no() {
        return ord_no;
    }

    public String getPro_no() {
        return pro_no;
    }

    public String getPro_name() {
        return pro_name;
    }

    public int getOrd_status() {
        return ord_status;
    }

    public String getOrd_date() {
        return ord_date;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public int getUser_no() {
        return user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setOrd_no(int ord_no) {
        this.ord_no = ord_no;
    }

    public void setPro_no(String pro_no) {
        this.pro_no = pro_no;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public void setOrd_status(int ord_status) {
        this.ord_status = ord_status;
    }

    public void setOrd_date(String ord_date) {
        this.ord_date = ord_date;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getord_statustText(){
        String orderText=null;
        switch (ord_status){
            case 0:
                orderText ="未處理";
                break;
            case 1:
                orderText ="未出貨";
                break;
            case 2:
                orderText ="已出貨";
                break;
            case 3:
                orderText ="未送達";
                break;
            case 4:
                orderText ="已送達";
                break;
        }

        return orderText;
    }







}