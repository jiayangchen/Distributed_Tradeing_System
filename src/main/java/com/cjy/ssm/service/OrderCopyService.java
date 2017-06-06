package com.cjy.ssm.service;

import com.cjy.ssm.model.Order_Copy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Service
public interface OrderCopyService {

    void insertOrderIntoSell(Order_Copy order_copy);
    void insertOrderIntoBuy(Order_Copy order_copy);

    void insertIntoSellFinished(Order_Copy order_copy);
    void insertIntoBuyFinished(Order_Copy order_copy);

    void deleteOrderFromSell(int id);
    void deleteOrderFromBuy(int id);

    void updateOnSell(int id, int value, String status);
    void updateOnBuy(int id, int value, String status);

    List<Order_Copy> getAllSpecificSellOrders(int cId, int tar_year, int tar_month, int isFloat);
    List<Order_Copy> getAllSpecificBuyOrders(int cId, int tar_year, int tar_month, int isFloat);
    List <Order_Copy> getFinishedBuyOrders(int cId, int tar_year, int tar_month);
    List <Order_Copy> getFinishedSellOrders(int cId, int tar_year, int tar_month);

    Order_Copy getBuyCancelOrders(int former_o_id, int cid, int tid, int year, int month);
    Order_Copy getSellCancelOrders(int former_o_id, int cid, int tid, int year, int month);

    List<Order_Copy> marketOrderPriceSets(int cid);

    void updateOrInsertIntoBuy(Order_Copy order_copy);
    void updateOrInsertIntoSell(Order_Copy order_copy);

}
