package com.g2.runningback.User;

import java.io.Serializable;

public class Order implements Serializable {
    private String ord_no;
    private String ord_status;

    public Order(String ord_no, String ord_status) {
        super();
        this.ord_no = ord_no;
        this.ord_status = ord_status;
    }

    public String getOrd_no() {
        return ord_no;
    }

    public void setOrd_no(String ord_no) {
        this.ord_no = ord_no;
    }

    public String getOrd_status() {
        return ord_status;
    }

    public void setOrd_status(String ord_status) {
        this.ord_status = ord_status;
    }

}
