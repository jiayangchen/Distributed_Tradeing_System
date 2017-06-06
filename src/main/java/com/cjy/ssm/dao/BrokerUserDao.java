package com.cjy.ssm.dao;

import com.cjy.ssm.model.Broker_User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Repository
public interface BrokerUserDao {
    Broker_User getBrokerUserById(int id);
    List<Broker_User> getAllBrokerUsers();
}
