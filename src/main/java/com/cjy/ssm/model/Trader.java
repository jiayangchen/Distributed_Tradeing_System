package com.cjy.ssm.model;

import java.io.Serializable;

/**
 * Created by ChenJiayang on 2017/5/16.
 */
public class Trader implements Serializable{

    int t_id;
    String t_name;

    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }
}
