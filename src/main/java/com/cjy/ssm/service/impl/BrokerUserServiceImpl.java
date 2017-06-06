package com.cjy.ssm.service.impl;

import com.cjy.ssm.model.Broker_User;
import com.cjy.ssm.dao.BrokerUserDao;
import com.cjy.ssm.service.BrokerUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public class BrokerUserServiceImpl implements BrokerUserService {

    @Resource
    BrokerUserDao brokerUserDao;

    @Override
    public Broker_User getBrokerUserById(int id) {
        return brokerUserDao.getBrokerUserById(id);
    }

    @Override
    public List<Broker_User> getAllBrokerUsers() {
        return brokerUserDao.getAllBrokerUsers();
    }
}
