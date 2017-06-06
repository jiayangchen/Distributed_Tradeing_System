package com.cjy.ssm.service;

import com.cjy.ssm.model.Working_Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/16.
 */

@Service
public interface WorkingOrderService {
    List<Working_Order> getAllWorkingOrder();
}
