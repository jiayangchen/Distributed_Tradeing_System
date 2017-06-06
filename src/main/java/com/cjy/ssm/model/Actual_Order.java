package com.cjy.ssm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ChenJiayang on 2017/5/16.
 */
public class Actual_Order implements Serializable {
    int ao_id;
    int sell_o_id;
    int buy_o_id;
    double ao_price;
    int ao_vol;
    Timestamp ao_create_time;
    int c_id;
    int ao_year;
    int ao_month;
    double market_price;
    double commission;
    int bu_id;

    public int getAo_id() {
        return ao_id;
    }

    public void setAo_id(int ao_id) {
        this.ao_id = ao_id;
    }

    public int getSell_o_id() {
        return sell_o_id;
    }

    public void setSell_o_id(int sell_o_id) {
        this.sell_o_id = sell_o_id;
    }

    public int getBuy_o_id() {
        return buy_o_id;
    }

    public void setBuy_o_id(int buy_o_id) {
        this.buy_o_id = buy_o_id;
    }

    public double getAo_price() {
        return ao_price;
    }

    public void setAo_price(double ao_price) {
        this.ao_price = ao_price;
    }

    public int getAo_vol() {
        return ao_vol;
    }

    public void setAo_vol(int ao_vol) {
        this.ao_vol = ao_vol;
    }

    public Timestamp getAo_create_time() {
        return ao_create_time;
    }

    public void setAo_create_time(Timestamp ao_create_time) {
        this.ao_create_time = ao_create_time;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public int getAo_year() {
        return ao_year;
    }

    public void setAo_year(int ao_year) {
        this.ao_year = ao_year;
    }

    public int getAo_month() {
        return ao_month;
    }

    public void setAo_month(int ao_month) {
        this.ao_month = ao_month;
    }

    public double getMarket_price() {
        return market_price;
    }

    public void setMarket_price(double market_price) {
        this.market_price = market_price;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public int getBu_id() {
        return bu_id;
    }

    public void setBu_id(int bu_id) {
        this.bu_id = bu_id;
    }
}
