package com.cjy.ssm.dao;

import com.cjy.ssm.model.Order_Copy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/22.
 */

@Repository
public interface OrderCopyDao {

    void addOrderIntoSell(Order_Copy order_copy);
    void addOrderIntoBuy(Order_Copy order_copy);

    void insertIntoSellFinished(Order_Copy order_copy);
    void insertIntoBuyFinished(Order_Copy order_copy);

    void deleteFromSell(int id);
    void deleteFromBuy(int id);

    void updateOnSell(int id, int value, String status);
    void updateOnBuy(int id, int value, String status);

    List <Order_Copy> getAllSpecificSellOrders(int cId, int tar_year, int tar_month, int isFloat);
    List <Order_Copy> getAllSpecificBuyOrders(int cId, int tar_year, int tar_month, int isFloat);
    List <Order_Copy> getFinishedBuyOrders(int cId, int tar_year, int tar_month);
    List <Order_Copy> getFinishedSellOrders(int cId, int tar_year, int tar_month);

    Order_Copy getBuyCancelOrders(int former_o_id, int cid, int tid, int year, int month);
    Order_Copy getSellCancelOrders(int former_o_id, int cid, int tid, int year, int month);

    List<Order_Copy> marketPriceSets(int cid);

    Order_Copy selectSellOrderById (int id);
    Order_Copy selectBuyOrderById (int id);
}
