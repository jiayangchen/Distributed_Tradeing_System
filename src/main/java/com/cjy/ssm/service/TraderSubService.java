package com.cjy.ssm.service;

import com.cjy.ssm.model.Trader_Subscribe;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Service
public interface TraderSubService {
    List<Trader_Subscribe> getTrader_SubscribeListByTId(int id);
}
