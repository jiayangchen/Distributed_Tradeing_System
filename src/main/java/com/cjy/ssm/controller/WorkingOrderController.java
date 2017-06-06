package com.cjy.ssm.controller;

import com.cjy.ssm.model.Actual_Order;
import com.cjy.ssm.model.Working_Order;
import com.cjy.ssm.service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.Destination;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by ChenJiayang on 2017/5/16.
 */

@Controller
@RequestMapping("/broker")
public class WorkingOrderController {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private TopicProvider topicProvider;

    @Autowired
    @Qualifier("queueDestination")
    private Destination destination;

    @Autowired
    @Qualifier("queueDestination2")
    private Destination destination2;

    @Autowired
    @Qualifier("topicDestination0")
    private Destination destination3;

    @Autowired
    private WorkingOrderService workingOrderService;

    @Autowired
    private ActualOrderService actualOrderService;

    @RequestMapping("/workingorder")
    public @ResponseBody String getWorkingOrder(){

        //List<Working_Order> wo_list = workingOrderService.getAllWorkingOrder();
        JSONArray jsonArray = new JSONArray();

        List<Working_Order> bookList = workingOrderService.getAllWorkingOrder();

        for (Working_Order b : bookList) {

            JSONObject json = new JSONObject();
            json.put("o_id",b.getO_id());
            json.put("wo_id",b.getWo_id());
            json.put("wo_modified_time",b.getWo_modified_time());
            json.put("wo_price",b.getWo_price());
            json.put("wo_vol",b.getWo_vol());
            jsonArray.put(json);

        }

        if(jsonArray.length()!=0){
            return jsonArray.toString();
        }else{
            return null;
        }
    }

    @RequestMapping("produceorder")
    public String produceOrder(){

        JSONObject order_copy_json = new JSONObject();
        order_copy_json.put("o_id",643);
        order_copy_json.put("t_id", 1);
        order_copy_json.put("c_id", 1);
        /*order_copy_json.put("o_price", -1);
        order_copy_json.put("o_vol", 100);
        order_copy_json.put("o_type", "Market");
        order_copy_json.put("o_status", "New");*/
        order_copy_json.put("o_year", 2018);
        order_copy_json.put("o_month", 5);
        /*order_copy_json.put("o_stop_value", -1);
        order_copy_json.put("o_limit_value", -1);*/
        order_copy_json.put("o_is_buy", 0);
        order_copy_json.put("isFloat", 0);

        producerService.sendMessage(destination2, order_copy_json.toString());

        /*Random random = new Random();
        List<Actual_Order> list = actualOrderService.getAOByCIdandTime(3,2018,5);
        for(int i=0; i<list.size(); i++)
        {
            Actual_Order ao = list.get(i);
            int vol = random.nextInt(150) % (150 - 80 + 1) + 80;
            double market_price = random.nextInt(120) % (120 - 80 + 1) + 80;
            ao.setAo_id(210+i);
            ao.setAo_vol(vol);
            ao.setAo_price(market_price*vol);
            ao.setMarket_price(market_price);
            ao.setCommission(market_price * vol * 0.05d);
            ao.setAo_month(4);
            actualOrderService.insertActualOrder(ao);
        }*/
        /*for(int i=0; i<18; i++) {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Actual_Order actual_order = new Actual_Order();
            //生成actual order
            Random random = new Random();
            int vol = random.nextInt(150) % (150 - 80 + 1) + 80;
            double market_price = random.nextInt(120) % (120 - 80 + 1) + 80;
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(vol);
            actual_order.setAo_price(market_price * vol);
            actual_order.setBuy_o_id(70+i);
            actual_order.setSell_o_id(50+i);
            actual_order.setC_id(3);
            actual_order.setAo_year(2018);
            actual_order.setAo_month(4);
            actual_order.setMarket_price(market_price);
            actual_order.setCommission(market_price * vol * 0.05d);
            actual_order.setBu_id(1);

            actualOrderService.insertActualOrder(actual_order);
        }*/
        return "index";
    }

    @RequestMapping("receiveorder")
    public String receiveOrder(){
        for (int i=0; i<10; i++) {
            consumerService.receive(destination);
        }
        return "index";
    }

    @RequestMapping("producetopic")
    public String produceTopic(){
        for (int i=0; i<10; i++) {
            topicProvider.publish(destination3, "Hello,destination1, message:" + (i+1));
        }

        return "index";
    }
}
