package com.cjy.ssm.service.impl;

import com.cjy.ssm.model.Trader_Subscribe;
import com.cjy.ssm.service.TraderSubService;
import com.cjy.ssm.dao.TraderSubDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public class TraderSubServiceImpl implements TraderSubService {
    @Resource
    TraderSubDao traderSubDao;

    @Override
    public List<Trader_Subscribe> getTrader_SubscribeListByTId(int id) {
        return traderSubDao.getTrader_SubscribeListByTId(id);
    }
}
