package com.cjy.ssm.service.impl;

import com.cjy.ssm.dao.WorkingOrderDao;
import com.cjy.ssm.model.Working_Order;
import com.cjy.ssm.service.WorkingOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/16.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class WorkingOrderServiceImpl implements WorkingOrderService {

    @Resource
    WorkingOrderDao working_order_dao;

    public List<Working_Order> getAllWorkingOrder() {
        return working_order_dao.selectAllWorkingOrder();
    }
}
