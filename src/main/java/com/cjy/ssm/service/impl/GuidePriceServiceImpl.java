package com.cjy.ssm.service.impl;

import com.cjy.ssm.dao.GuidePriceDao;
import com.cjy.ssm.model.Guide_Price;
import com.cjy.ssm.service.GuidePriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ChenJiayang on 2017/6/3.
 */

@Service
public class GuidePriceServiceImpl implements GuidePriceService {

    @Resource
    GuidePriceDao guidePriceDao;

    @Override
    public Guide_Price getGuidePrice(int cid, int year, int month) {
        return guidePriceDao.getGuidePrice(cid,year,month);
    }
}
