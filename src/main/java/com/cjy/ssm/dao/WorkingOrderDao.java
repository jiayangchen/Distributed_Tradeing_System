package com.cjy.ssm.dao;

import com.cjy.ssm.model.Working_Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/16.
 */

@Repository
public interface WorkingOrderDao {
    List<Working_Order> selectAllWorkingOrder();
}
