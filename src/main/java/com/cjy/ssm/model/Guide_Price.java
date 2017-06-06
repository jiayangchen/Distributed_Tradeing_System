package com.cjy.ssm.model;

import java.io.Serializable;

/**
 * Created by ChenJiayang on 2017/6/3.
 */
public class Guide_Price implements Serializable{

    int gp_id;
    int c_id;
    int g_year;
    int g_month;
    double guide_price;

    public int getGp_id() {
        return gp_id;
    }

    public void setGp_id(int gp_id) {
        this.gp_id = gp_id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public int getG_year() {
        return g_year;
    }

    public void setG_year(int g_year) {
        this.g_year = g_year;
    }

    public int getG_month() {
        return g_month;
    }

    public void setG_month(int g_month) {
        this.g_month = g_month;
    }

    public double getGuide_price() {
        return guide_price;
    }

    public void setGuide_price(double guide_price) {
        this.guide_price = guide_price;
    }
}
