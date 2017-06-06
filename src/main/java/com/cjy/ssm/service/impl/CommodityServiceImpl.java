package com.cjy.ssm.service.impl;

import com.cjy.ssm.dao.CommodityDao;
import com.cjy.ssm.model.Commodity;
import com.cjy.ssm.service.CommodityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public class CommodityServiceImpl implements CommodityService {

    @Resource
    CommodityDao commodityDao;

    @Override
    public Commodity getCommodityByCId(int cid) {
        return commodityDao.getCommodityByCId(cid);
    }

    @Override
    public void updateCPrice(int cid, double cprice) {
        commodityDao.updateCPrice(cid,cprice);
    }

    @Override
    public List<Commodity> getAllCommodityByBuId(int buid) {
        return commodityDao.getAllCommodityByBuId(buid);
    }
}
