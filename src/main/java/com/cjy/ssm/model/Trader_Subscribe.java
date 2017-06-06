package com.cjy.ssm.model;

import java.io.Serializable;

/**
 * Created by ChenJiayang on 2017/5/16.
 */
public class Trader_Subscribe implements Serializable {
    int ts_id;
    int t_id;
    int c_id;

    public int getTs_id() {
        return ts_id;
    }

    public void setTs_id(int ts_id) {
        this.ts_id = ts_id;
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
}
