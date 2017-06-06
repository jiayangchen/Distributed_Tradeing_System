package com.cjy.ssm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ChenJiayang on 2017/5/22.
 */
public class Order_Copy implements Serializable {

    int o_id;
    int t_id;
    int c_id;
    double o_price;
    int o_vol;
    String o_type;
    String o_status;
    Timestamp o_create_time;
    int o_year;
    int o_month;
    double o_limit_value;
    double o_stop_value;
    int o_is_buy;
    int former_o_id;
    int isFloat;
    double stop_or_limit_value;

    public int getO_id() {
        return o_id;
    }

    public void setO_id(int o_id) {
        this.o_id = o_id;
    }

    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public double getO_price() {
        return o_price;
    }

    public void setO_price(double o_price) {
        this.o_price = o_price;
    }

    public int getO_vol() {
        return o_vol;
    }

    public void setO_vol(int o_vol) {
        this.o_vol = o_vol;
    }

    public String getO_type() {
        return o_type;
    }

    public void setO_type(String o_type) {
        this.o_type = o_type;
    }

    public String getO_status() {
        return o_status;
    }

    public void setO_status(String o_status) {
        this.o_status = o_status;
    }

    public Timestamp getO_create_time() {
        return o_create_time;
    }

    public void setO_create_time(Timestamp o_create_time) {
        this.o_create_time = o_create_time;
    }

    public int getO_year() {
        return o_year;
    }

    public void setO_year(int o_year) {
        this.o_year = o_year;
    }

    public int getO_month() {
        return o_month;
    }

    public void setO_month(int o_month) {
        this.o_month = o_month;
    }

    public double getO_limit_value() {
        return o_limit_value;
    }

    public void setO_limit_value(double o_limit_value) {
        this.o_limit_value = o_limit_value;
    }

    public double getO_stop_value() {
        return o_stop_value;
    }

    public void setO_stop_value(double o_stop_value) {
        this.o_stop_value = o_stop_value;
    }

    public int getO_is_buy() {
        return o_is_buy;
    }

    public void setO_is_buy(int o_is_buy) {
        this.o_is_buy = o_is_buy;
    }

    public int getFormer_o_id() {
        return former_o_id;
    }

    public void setFormer_o_id(int former_o_id) {
        this.former_o_id = former_o_id;
    }

    public int getIsFloat() {
        return isFloat;
    }

    public void setIsFloat(int isFloat) {
        this.isFloat = isFloat;
    }

    public double getStop_or_limit_value() {
        return stop_or_limit_value;
    }

    public void setStop_or_limit_value(double stop_or_limit_value) {
        this.stop_or_limit_value = stop_or_limit_value;
    }
}
