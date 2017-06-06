package com.cjy.ssm.service;

import com.cjy.ssm.model.Guide_Price;
import org.springframework.stereotype.Service;

/**
 * Created by ChenJiayang on 2017/6/3.
 */

@Service
public interface GuidePriceService {
    Guide_Price getGuidePrice(int cid, int year, int month);
}
