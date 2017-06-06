package com.cjy.ssm.model;

/**
 * Created by ChenJiayang on 2017/5/30.
 */
public class BrokerUIQuery {

    int c_id;
    int t_id;
    double start_price;
    double end_price;
    int start_year;
    int start_month;
    int end_year;
    int end_month;

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public double getStart_price() {
        return start_price;
    }

    public void setStart_price(double start_price) {
        this.start_price = start_price;
    }

    public double getEnd_price() {
        return end_price;
    }

    public void setEnd_price(double end_price) {
        this.end_price = end_price;
    }

    public int getStart_year() {
        return start_year;
    }

    public void setStart_year(int start_year) {
        this.start_year = start_year;
    }

    public int getStart_month() {
        return start_month;
    }

    public void setStart_month(int start_month) {
        this.start_month = start_month;
    }

    public int getEnd_year() {
        return end_year;
    }

    public void setEnd_year(int end_year) {
        this.end_year = end_year;
    }

    public int getEnd_month() {
        return end_month;
    }

    public void setEnd_month(int end_month) {
        this.end_month = end_month;
    }
}
