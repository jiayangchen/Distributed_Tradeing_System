package com.cjy.ssm.service;

import com.cjy.ssm.model.Actual_Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Service
public interface ActualOrderService {
    void insertActualOrder(Actual_Order order);
    List<Actual_Order> getAllAOByCId(int cid);
    List<Actual_Order> getAllAOByCIdandTime(int cid,int s_year,int s_month,int e_year,int e_month);
    List<Actual_Order> getAOByCIdandTime(int cid,int year,int month);
}
