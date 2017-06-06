package com.cjy.ssm.service.impl;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.MessageListener;

/**
 * Created by ChenJiayang on 2017/5/20.
 */
public class TopicMessageListener implements MessageListener {
    public void onMessage(Message message) {
        TextMessage tm = (TextMessage) message;
        try {
            System.out.println("TopicMessageListener \t" + tm.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
