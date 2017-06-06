package com.cjy.ssm.service;

import com.cjy.ssm.model.Broker_User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public interface BrokerUserService {
    Broker_User getBrokerUserById(int id);
    List<Broker_User> getAllBrokerUsers();
}
