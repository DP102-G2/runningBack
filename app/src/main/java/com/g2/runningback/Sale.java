package com.g2.runningback;

import java.io.Serializable;
import java.sql.Timestamp;

public class Sale implements Serializable {

    int sumPrice;
    int cat_no;
    public Sale(int sumPrice, int cat_no){
        this.sumPrice = sumPrice;
        this.cat_no = cat_no;
    }

    public int getCat_no() {
        return cat_no;
    }

    public void setCat_no(int cat_no) {
        this.cat_no = cat_no;
    }

    public int getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(int sumPrice) {
        this.sumPrice = sumPrice;
    }

}



