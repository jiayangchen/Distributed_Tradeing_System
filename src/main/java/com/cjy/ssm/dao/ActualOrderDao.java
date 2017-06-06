package com.cjy.ssm.dao;

import com.cjy.ssm.model.Actual_Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Repository
public interface ActualOrderDao {
    void addActualOrder(Actual_Order order);
    List<Actual_Order> getAllAOByCId(int cid);
    List<Actual_Order> getAllAOByCIdandTime(int cid,int s_year,int s_month,int e_year,int e_month);
    List<Actual_Order> getAOByCIdandTime(int cid,int year,int month);
}
