package com.cjy.ssm.dao;

import com.cjy.ssm.model.Commodity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Repository
public interface CommodityDao {
    Commodity getCommodityByCId(int cid);
    void updateCPrice(int cid, double cprice);
    List<Commodity> getAllCommodityByBuId(int buid);
}
