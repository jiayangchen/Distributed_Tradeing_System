package com.cjy.ssm.service.impl;

import com.cjy.ssm.service.OrderCopyService;
import com.cjy.ssm.dao.OrderCopyDao;
import com.cjy.ssm.model.Order_Copy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderCopyServiceImpl implements OrderCopyService {

    @Resource
    OrderCopyDao orderCopyDao;

    public void insertOrderIntoBuy(Order_Copy order_copy) {
        orderCopyDao.addOrderIntoBuy(order_copy);
    }

    public void insertOrderIntoSell(Order_Copy order_copy) {
        orderCopyDao.addOrderIntoSell(order_copy);
    }

    public List<Order_Copy> getAllSpecificBuyOrders(int cId, int tar_year, int tar_month, int isFloat) {
        return orderCopyDao.getAllSpecificBuyOrders(cId,tar_year,tar_month, isFloat);
    }

    public List<Order_Copy> getAllSpecificSellOrders(int cId, int tar_year, int tar_month, int isFloat) {
        return orderCopyDao.getAllSpecificSellOrders(cId,tar_year,tar_month, isFloat);
    }

    @Override
    public List<Order_Copy> marketOrderPriceSets(int cid) {
        return orderCopyDao.marketPriceSets(cid);
    }

    @Override
    public void deleteOrderFromSell(int id) {
        orderCopyDao.deleteFromSell(id);
    }

    @Override
    public void deleteOrderFromBuy(int id) {
        orderCopyDao.deleteFromBuy(id);
    }

    @Override
    public void updateOnBuy(int id, int value, String status) {
        orderCopyDao.updateOnBuy(id,value,status);
    }

    @Override
    public void updateOnSell(int id, int value, String status) {
        orderCopyDao.updateOnSell(id,value,status);
    }

    @Override
    public void updateOrInsertIntoBuy(Order_Copy order_copy) {
        Order_Copy tar_order = orderCopyDao.selectBuyOrderById(order_copy.getO_id());
        if(tar_order == null){
            orderCopyDao.addOrderIntoBuy(order_copy);
        }else{
            orderCopyDao.updateOnBuy(tar_order.getO_id(),order_copy.getO_vol(),order_copy.getO_status());
        }
    }

    @Override
    public void updateOrInsertIntoSell(Order_Copy order_copy) {
        Order_Copy tar_order = orderCopyDao.selectSellOrderById(order_copy.getO_id());
        if(tar_order == null){
            orderCopyDao.addOrderIntoSell(order_copy);
        }else{
            orderCopyDao.updateOnSell(tar_order.getO_id(),order_copy.getO_vol(),order_copy.getO_status());
        }
    }

    @Override
    public void insertIntoBuyFinished(Order_Copy order_copy) {
        orderCopyDao.insertIntoBuyFinished(order_copy);
    }

    @Override
    public void insertIntoSellFinished(Order_Copy order_copy) {
        orderCopyDao.insertIntoSellFinished(order_copy);
    }

    @Override
    public List<Order_Copy> getFinishedBuyOrders(int cId, int tar_year, int tar_month) {
        return orderCopyDao.getFinishedBuyOrders(cId,tar_year,tar_month);
    }

    @Override
    public List<Order_Copy> getFinishedSellOrders(int cId, int tar_year, int tar_month) {
        return orderCopyDao.getFinishedSellOrders(cId,tar_year,tar_month);
    }

    @Override
    public Order_Copy getBuyCancelOrders(int former_o_id, int cid, int tid, int year, int month) {
        return orderCopyDao.getBuyCancelOrders(former_o_id,cid,tid,year,month);
    }

    @Override
    public Order_Copy getSellCancelOrders(int former_o_id, int cid, int tid, int year, int month) {
        return orderCopyDao.getSellCancelOrders(former_o_id,cid,tid,year,month);
    }
}
