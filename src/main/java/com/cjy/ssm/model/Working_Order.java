package com.cjy.ssm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ChenJiayang on 2017/5/16.
 */
public class Working_Order implements Serializable {
    int wo_id;
    int o_id;
    double wo_price;
    int wo_vol;
    Timestamp wo_modified_time;

    public int getWo_id() {
        return wo_id;
    }

    public void setWo_id(int wo_id) {
        this.wo_id = wo_id;
    }

    public int getO_id() {
        return o_id;
    }

    public void setO_id(int o_id) {
        this.o_id = o_id;
    }

    public double getWo_price() {
        return wo_price;
    }

    public void setWo_price(double wo_price) {
        this.wo_price = wo_price;
    }

    public int getWo_vol() {
        return wo_vol;
    }

    public void setWo_vol(int wo_vol) {
        this.wo_vol = wo_vol;
    }

    public Timestamp getWo_modified_time() {
        return wo_modified_time;
    }

    public void setWo_modified_time(Timestamp wo_modified_time) {
        this.wo_modified_time = wo_modified_time;
    }
}
