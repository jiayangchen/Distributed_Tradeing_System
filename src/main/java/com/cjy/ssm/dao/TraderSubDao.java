package com.cjy.ssm.dao;

import com.cjy.ssm.model.Trader_Subscribe;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/28.
 */

@Repository
public interface TraderSubDao {
    List<Trader_Subscribe> getTrader_SubscribeListByTId(int tid);
}
