package com.cjy.ssm.model;

import java.io.Serializable;

/**
 * Created by ChenJiayang on 2017/5/16.
 */
public class Commodity implements Serializable {
    int c_id;
    int bu_id;
    String c_name;
    double c_price;

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public int getBu_id() {
        return bu_id;
    }

    public void setBu_id(int bu_id) {
        this.bu_id = bu_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public double getC_price() {
        return c_price;
    }

    public void setC_price(double c_price) {
        this.c_price = c_price;
    }
}
