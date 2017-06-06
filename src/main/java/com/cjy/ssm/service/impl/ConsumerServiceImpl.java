package com.cjy.ssm.service.impl;

import com.cjy.ssm.service.ConsumerService;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by ChenJiayang on 2017/5/20.
 */

public class ConsumerServiceImpl implements ConsumerService {

    private JmsTemplate jmsTemplate;

    /**
     * 接受消息
     */
    public void receive(Destination destination) {
        TextMessage tm = (TextMessage) jmsTemplate.receive(destination);
        try {
            System.out.println("从队列" + destination.toString() + "收到了消息：\t"
                    + tm.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}
