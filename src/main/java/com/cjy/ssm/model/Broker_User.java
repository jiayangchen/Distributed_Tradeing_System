package com.cjy.ssm.model;

import java.io.Serializable;

/**
 * Created by ChenJiayang on 2017/5/16.
 */

public class Broker_User implements Serializable {
    int bu_id;
    String bu_name;
    String bu_email;
    String bu_password;

    public int getBu_id() {
        return bu_id;
    }

    public void setBu_id(int bu_id) {
        this.bu_id = bu_id;
    }

    public String getBu_name() {
        return bu_name;
    }

    public void setBu_name(String bu_name) {
        this.bu_name = bu_name;
    }

    public String getBu_email() {
        return bu_email;
    }

    public void setBu_email(String bu_email) {
        this.bu_email = bu_email;
    }

    public String getBu_password() {
        return bu_password;
    }

    public void setBu_password(String bu_password) {
        this.bu_password = bu_password;
    }
}
