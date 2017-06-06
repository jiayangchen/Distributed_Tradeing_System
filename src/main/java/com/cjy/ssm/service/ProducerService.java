package com.cjy.ssm.service;

import javax.jms.Destination;

/**
 * Created by ChenJiayang on 2017/5/20.
 */
public interface ProducerService {
    void sendMessage(Destination destination, final String msg);
    void sendMessage(final String msg);
}
