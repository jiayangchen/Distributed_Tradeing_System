package com.cjy.ssm.service.impl;

import com.cjy.ssm.model.Actual_Order;
import com.cjy.ssm.service.ActualOrderService;
import com.cjy.ssm.dao.ActualOrderDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class ActualOrderServiceImpl implements ActualOrderService {

    @Resource
    ActualOrderDao actualOrderDao;

    @Override
    public void insertActualOrder(Actual_Order order) {
        actualOrderDao.addActualOrder(order);
    }

    @Override
    public List<Actual_Order> getAllAOByCId(int cid) {
        return actualOrderDao.getAllAOByCId(cid);
    }

    @Override
    public List<Actual_Order> getAOByCIdandTime(int cid, int year, int month) {
        return actualOrderDao.getAOByCIdandTime(cid,year,month);
    }

    @Override
    public List<Actual_Order> getAllAOByCIdandTime(int cid, int s_year, int s_month, int e_year, int e_month) {
        if(s_year == e_year && s_month <= e_month)
        {
            return actualOrderDao.getAllAOByCIdandTime(cid,s_year,s_month,e_year,e_month);
        }

        else if(s_year < e_year)
        {
            List<Actual_Order> ans = new ArrayList<>();
            List<Actual_Order> cidOrders = actualOrderDao.getAllAOByCId(cid);
            for(Actual_Order ao : cidOrders)
            {
                if(ao.getAo_year() >= s_year && ao.getAo_year() <= e_year)
                {
                    if(((12-s_month) + ao.getAo_month() + (ao.getAo_year()-s_year-1) * 12 >= 0) &&
                            ((12-s_month) + ao.getAo_month() + (ao.getAo_year()-s_year-1) * 12 <= (12-s_month) + 12*(e_year-s_year-1)+e_month))
                    {
                        ans.add(ao);
                    }
                }
            }
            return ans;
        }
        return null;
    }
}
