package com.cjy.ssm.dao;

import com.cjy.ssm.model.Guide_Price;
import org.springframework.stereotype.Repository;

/**
 * Created by ChenJiayang on 2017/6/3.
 */

@Repository
public interface GuidePriceDao {
    Guide_Price getGuidePrice(int cid,int year,int month);
}
