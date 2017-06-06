package com.cjy.ssm.service;

import com.cjy.ssm.model.Commodity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public interface CommodityService {
    Commodity getCommodityByCId(int cid);
    void updateCPrice(int cid, double cprice);
    List<Commodity> getAllCommodityByBuId(int buid);
}
