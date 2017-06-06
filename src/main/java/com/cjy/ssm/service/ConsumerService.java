package com.cjy.ssm.service;

import javax.jms.Destination;

/**
 * Created by ChenJiayang on 2017/5/20.
 */
public interface ConsumerService {
    void receive(Destination destination);
}
