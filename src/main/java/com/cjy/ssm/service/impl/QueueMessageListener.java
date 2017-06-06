package com.cjy.ssm.service.impl;

import com.cjy.ssm.controller.ChatController;
import com.cjy.ssm.model.Actual_Order;
import com.cjy.ssm.model.Guide_Price;
import com.cjy.ssm.model.Order_Copy;
import com.cjy.ssm.service.*;
import com.cjy.ssm.utils.DateJsonValueProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.jms.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by ChenJiayang on 2017/5/20.
 */

public class QueueMessageListener implements MessageListener {

    static int BUID = 1;

    @Autowired
    CommodityService commodityService;

    @Autowired
    OrderCopyService orderCopyService;

    @Autowired
    ActualOrderService actualOrderService;

    @Autowired
    ProducerService producerService;

    @Autowired
    GuidePriceService guidePriceService;

    @Autowired
    @Qualifier("queueDestination3")
    private Destination queue2;

    @Autowired
    @Qualifier("queueDestination4")
    private Destination queue3;

    @Autowired
    private TopicProvider topicProvider;

    @Autowired
    @Qualifier("topicDestination0")
    private Destination gold_destination;

    @Autowired
    @Qualifier("topicDestination1")
    private Destination corn_destination;

    @Autowired
    @Qualifier("topicDestination2")
    private Destination crude_destination;

    @Autowired
    private ChatController chatController;

    //当收到消息时，自动调用该方法。
    public void onMessage(Message message) {
        TextMessage tm = (TextMessage) message;
        Timestamp tp = new Timestamp(new Date().getTime());

        try {
            String content = tm.getText();
            System.out.println("订单信息为：" + content);

            //接收到order
            JSONObject json = JSONObject.fromObject(content);

            if (json.has("o_status"))
            {

                json.put("o_status", "Placed");
                json.put("o_create_time", tp.toString());

                //返回消息给trader gateway
                switch (json.getInt("c_id")) {
                    case 1:
                        topicProvider.publish(gold_destination, json.toString());
                        break;
                    case 2:
                        topicProvider.publish(corn_destination, json.toString());
                        break;
                    case 3:
                        topicProvider.publish(crude_destination, json.toString());
                        break;
                    default:
                        break;
                }

                final Order_Copy order_copy = new Order_Copy();

                order_copy.setC_id(json.getInt("c_id"));
                order_copy.setT_id(json.getInt("t_id"));
                order_copy.setO_create_time(tp); //以接收到的时间为订单创建时间
                order_copy.setO_price(json.getDouble("o_price"));
                order_copy.setO_vol(json.getInt("o_vol"));

                String orderType = json.getString("o_type");
                order_copy.setO_type(orderType);
                order_copy.setO_status("Placed"); //设置状态为Placed

                if (orderType.equals("Market")) {
                    order_copy.setO_limit_value(-1d);
                    order_copy.setO_stop_value(-1d);
                    order_copy.setIsFloat(0);
                    order_copy.setStop_or_limit_value(-1d);
                } else if (orderType.equals("Limit")) {
                    String limit_value = json.getString("o_limit_value");
                    order_copy.setO_stop_value(-1d);

                    //判断是不是float形式的数值
                    if (limit_value.contains("%")) {
                        Guide_Price guide_price = guidePriceService.getGuidePrice(json.getInt("c_id"),
                                json.getInt("o_year"),
                                json.getInt("o_month"));
                        double guidePrice = guide_price.getGuide_price();
                        order_copy.setIsFloat(1);
                        //根据指导价来将float转成fixed
                        switch (json.getInt("c_id")) {
                            case 1:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                break;
                            case 2:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                break;
                            case 3:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(limit_value.substring(0, limit_value.length() - 1))));
                                break;
                            default:
                                break;
                        }
                    } else {
                        order_copy.setIsFloat(0);
                        order_copy.setO_limit_value(Double.parseDouble(limit_value));
                        order_copy.setStop_or_limit_value(Double.parseDouble(limit_value));
                    }

                } else if (orderType.equals("Stop")) {
                    String stop_value = json.getString("o_stop_value");
                    order_copy.setO_limit_value(-1d);
                    if (stop_value.contains("%")) {
                        Guide_Price guide_price = guidePriceService.getGuidePrice(json.getInt("c_id"),
                                json.getInt("o_year"),
                                json.getInt("o_month"));
                        double guidePrice = guide_price.getGuide_price();
                        order_copy.setIsFloat(1);
                        //根据指导价来将float转成fixed
                        switch (json.getInt("c_id")) {
                            case 1:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                break;
                            case 2:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                break;
                            case 3:
                                order_copy.setO_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                order_copy.setStop_or_limit_value(guidePrice * (1.0 + 0.01 * Double.parseDouble(stop_value.substring(0, stop_value.length() - 1))));
                                break;
                            default:
                                break;
                        }
                    } else {
                        order_copy.setIsFloat(0);
                        order_copy.setO_stop_value(Double.parseDouble(stop_value));
                        order_copy.setStop_or_limit_value(Double.parseDouble(stop_value));
                    }
                }


                order_copy.setO_year(json.getInt("o_year"));
                order_copy.setO_month(json.getInt("o_month"));
                order_copy.setO_is_buy(json.getInt("o_is_buy"));
                order_copy.setFormer_o_id(json.getInt("o_id"));

                double market_price = returnMarketPrice(json.getInt("c_id"));
                commodityService.updateCPrice(order_copy.getC_id(), market_price);
                System.out.println("市场价为：" + market_price);

                //这是一个Sell订单
                if (order_copy.getO_is_buy() == 0) {
                    //取出目前跟这个Sell可交易的所有Buy Orders
                    List<Order_Copy> now_trading_buy_orders = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),
                            order_copy.getO_year(), order_copy.getO_month(), order_copy.getIsFloat());

                    switch (order_copy.getO_type()) {
                        //如果Sell是一个Market Order
                        case "Market":
                            System.out.println("这是个Sell的Market订单");
                            //遍历Buy的集合
                            //o : order_buy
                            for (Order_Copy o : now_trading_buy_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        sell_market_to_buy_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        sell_market_to_buy_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        sell_market_to_buy_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }
                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoSell(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());

                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id()) {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "Limit":
                            System.out.println("这是个Sell的Limit订单");
                            for (Order_Copy o : now_trading_buy_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        sell_limit_to_buy_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        sell_limit_to_buy_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        sell_limit_to_buy_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }
                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoSell(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());

                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id()) {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "Stop":
                            System.out.println("这是个Sell的Stop订单");
                            for (Order_Copy o : now_trading_buy_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        sell_stop_to_buy_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        sell_stop_to_buy_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        sell_stop_to_buy_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }
                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoSell(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());

                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                            /*mdJSON.put("buy",orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                                    order_copy.getO_month(),order_copy.getIsFloat()));
                            mdJSON.put("sell",orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                                    order_copy.getO_month(),order_copy.getIsFloat()));*/

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id()) {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                    }
                }

                //如果是一个Buy订单
                else if (order_copy.getO_is_buy() == 1) {
                    //取出跟这个Buy的订单可交易的所有Sell订单的集合
                    List<Order_Copy> now_trading_sell_orders = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),
                            order_copy.getO_year(), order_copy.getO_month(), order_copy.getIsFloat());

                    switch (order_copy.getO_type()) {
                        //如果Buy的订单是个Market Order
                        case "Market":
                            System.out.println("这是个Buy的Market订单");
                            //遍历Sell的集合
                            for (Order_Copy o : now_trading_sell_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        buy_market_to_sell_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        buy_market_to_sell_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        buy_market_to_sell_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }

                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoBuy(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());

                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id()) {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;

                        case "Limit":
                            System.out.println("这是个Buy的Limit订单");
                            for (Order_Copy o : now_trading_sell_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        buy_limit_to_sell_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        buy_limit_to_sell_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        buy_limit_to_sell_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }
                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoBuy(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());


                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells) {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id()) {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "Stop":
                            System.out.println("这是个Buy的Stop订单");
                            for (Order_Copy o : now_trading_sell_orders) {
                                market_price = returnMarketPrice(o.getC_id());
                                switch (o.getO_type()) {
                                    case "Market":
                                        buy_stop_to_sell_market(order_copy, o, market_price, json);
                                        break;
                                    case "Limit":
                                        buy_stop_to_sell_limit(order_copy, o, market_price, json);
                                        break;
                                    case "Stop":
                                        buy_stop_to_sell_stop(order_copy, o, market_price, json);
                                        break;
                                    default:
                                        break;
                                }
                                if (order_copy.getO_vol() == 0) {
                                    break;
                                }
                            }
                            if (order_copy.getO_vol() != 0) {
                                orderCopyService.insertOrderIntoBuy(order_copy);
                                JSONObject mdJSON = new JSONObject();
                                mdJSON.put("c_id", order_copy.getC_id());
                                mdJSON.put("o_year", order_copy.getO_year());
                                mdJSON.put("o_month", order_copy.getO_month());
                                mdJSON.put("isFloat", order_copy.getIsFloat());


                                JSONArray buy_ja = new JSONArray();
                                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : buys)
                                {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    buy_ja.add(jo);
                                }
                                mdJSON.put("buy", buy_ja);

                                JSONArray sell_ja = new JSONArray();
                                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(), order_copy.getO_year(),
                                        order_copy.getO_month(), order_copy.getIsFloat());
                                for (Order_Copy oc : sells)
                                {
                                    JsonConfig config = new JsonConfig();
                                    config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                                    JSONObject jo = JSONObject.fromObject(oc, config);
                                    sell_ja.add(jo);
                                }
                                mdJSON.put("sell", sell_ja);

                                JSONObject ret = new JSONObject();
                                ret.put("actual_order", "");
                                ret.put("market_depth", mdJSON);

                                //websocket
                                chatController.sendMessage(ret);

                                switch (order_copy.getC_id())
                                {
                                    case 1:
                                        topicProvider.publish(gold_destination, ret.toString());
                                        break;
                                    case 2:
                                        topicProvider.publish(corn_destination, ret.toString());
                                        break;
                                    case 3:
                                        topicProvider.publish(crude_destination, ret.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                    }
                }
            }
            else
            {
                int oid = json.getInt("o_id");
                int tid = json.getInt("t_id");
                int c_id = json.getInt("c_id");
                int is_buy = json.getInt("o_is_buy");
                int year = json.getInt("o_year");
                int month = json.getInt("o_month");
                int isFloat = 0;
                JSONObject jsonObject = new JSONObject();
                switch (is_buy)
                {
                    case 0:
                        Order_Copy cancel_sell_order = orderCopyService.getSellCancelOrders(oid, c_id, tid, year,month);
                        if(cancel_sell_order == null){
                            break;
                        }
                        isFloat = cancel_sell_order.getIsFloat();
                        switch (cancel_sell_order.getO_status())
                        {
                            case "Placed":
                                cancel_sell_order.setO_status("Canceled");
                                break;
                            case "Part_Completed":
                                cancel_sell_order.setO_status("Part_Canceled");
                                break;
                            default:
                                cancel_sell_order.setO_status("Canceled");
                                break;
                        }
                        JsonConfig config = new JsonConfig();
                        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                        jsonObject = JSONObject.fromObject(cancel_sell_order,config);
                        jsonObject.put("o_id",cancel_sell_order.getFormer_o_id());
                        orderCopyService.insertIntoSellFinished(cancel_sell_order);
                        orderCopyService.deleteOrderFromSell(cancel_sell_order.getO_id());
                        break;
                    case 1:
                        Order_Copy cancel_buy_order = orderCopyService.getBuyCancelOrders(oid,
                                c_id, tid, year,month);
                        if(cancel_buy_order == null){
                            break;
                        }
                        isFloat = cancel_buy_order.getIsFloat();
                        switch (cancel_buy_order.getO_status())
                        {
                            case "Placed":
                                cancel_buy_order.setO_status("Canceled");
                                break;
                            case "Part_Completed":
                                cancel_buy_order.setO_status("Part_Canceled");
                                break;
                            default:
                                cancel_buy_order.setO_status("Canceled");
                                break;
                        }

                        JsonConfig configg = new JsonConfig();
                        configg.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                        jsonObject = JSONObject.fromObject(cancel_buy_order,configg);
                        jsonObject.put("o_id",cancel_buy_order.getFormer_o_id());
                        orderCopyService.insertIntoBuyFinished(cancel_buy_order);
                        orderCopyService.deleteOrderFromBuy(cancel_buy_order.getO_id());
                        break;
                }

                switch (json.getInt("c_id"))
                {
                    case 1:
                        topicProvider.publish(gold_destination, jsonObject.toString());
                        break;
                    case 2:
                        topicProvider.publish(corn_destination, jsonObject.toString());
                        break;
                    case 3:
                        topicProvider.publish(crude_destination, jsonObject.toString());
                        break;
                    default:
                        break;
                }

                JSONObject mdJSON = new JSONObject();
                mdJSON.put("c_id", c_id);
                mdJSON.put("o_year", year);
                mdJSON.put("o_month", month);
                mdJSON.put("isFloat", isFloat);

                JSONArray buy_ja = new JSONArray();
                List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(c_id, year,
                        month, isFloat);
                if(!buys.isEmpty())
                {
                    for (Order_Copy oc : buys)
                    {
                        JsonConfig config = new JsonConfig();
                        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                        JSONObject jo = JSONObject.fromObject(oc, config);
                        buy_ja.add(jo);
                    }
                    mdJSON.put("buy", buy_ja);
                }
                else
                {
                    mdJSON.put("buy", buy_ja);
                }

                JSONArray sell_ja = new JSONArray();
                List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(c_id, year,
                        month, isFloat);
                if(!sells.isEmpty())
                {
                    for (Order_Copy oc : sells) {
                        JsonConfig config = new JsonConfig();
                        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                        JSONObject jo = JSONObject.fromObject(oc, config);
                        sell_ja.add(jo);
                    }
                    mdJSON.put("sell", sell_ja);
                }
                else
                {
                    mdJSON.put("sell", sell_ja);
                }

                JSONObject ret = new JSONObject();
                ret.put("actual_order", "");
                ret.put("market_depth", mdJSON);

                //websocket
                chatController.sendMessage(ret);

                switch (c_id)
                {
                    case 1:
                        topicProvider.publish(gold_destination, ret.toString());
                        break;
                    case 2:
                        topicProvider.publish(corn_destination, ret.toString());
                        break;
                    case 3:
                        topicProvider.publish(crude_destination, ret.toString());
                        break;
                    default:
                        break;
                }
            }
        }catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sell_stop_to_buy_stop(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        if(market_price >= order_buy.getO_stop_value() && market_price <= order_sell.getO_stop_value()){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_stop_to_buy_limit(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        if(market_price <= order_sell.getO_stop_value() && market_price <= order_buy.getO_limit_value()){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_stop_to_buy_market(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        if(order_sell.getO_stop_value() >= market_price){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_limit_to_buy_stop(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        if(market_price >= order_sell.getO_limit_value() && market_price >= order_buy.getO_stop_value()){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_limit_to_buy_limit(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        if(market_price >= order_sell.getO_limit_value() && market_price <= order_buy.getO_limit_value()){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_limit_to_buy_market(Order_Copy order_sell, Order_Copy order_buy, double market_price, JSONObject json){
        double limit_value = order_sell.getO_limit_value();
        if(market_price >= limit_value){
            sell_market_to_buy_market(order_sell,order_buy,market_price,json);
        }
    }

    private void sell_market_to_buy_stop(Order_Copy order_copy, Order_Copy o, double market_price, JSONObject json){
        double stop_value = o.getO_stop_value();
        if(stop_value <= market_price){
            sell_market_to_buy_market(order_copy,o,market_price,json);
        }
    }

    private void sell_market_to_buy_limit(Order_Copy order_copy, Order_Copy o, double market_price, JSONObject json){
        double limit_value = o.getO_limit_value();
        if(limit_value >= market_price){
            sell_market_to_buy_market(order_copy,o,market_price,json);
        }
    }

    private void buy_stop_to_sell_stop(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price >= order_buy.getO_stop_value() && market_price <= order_sell.getO_stop_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_stop_to_sell_limit(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price >= order_buy.getO_stop_value() && market_price >= order_sell.getO_limit_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_stop_to_sell_market(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price >= order_buy.getO_stop_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_limit_to_sell_stop(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price <= order_buy.getO_limit_value() && market_price <= order_sell.getO_stop_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_limit_to_sell_limit(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price <= order_buy.getO_limit_value() && market_price >= order_sell.getO_limit_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_limit_to_sell_market(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price <= order_buy.getO_limit_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_market_to_sell_stop(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price <= order_sell.getO_stop_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_market_to_sell_limit(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        if(market_price >= order_sell.getO_limit_value()){
            buy_market_to_sell_market(order_buy,order_sell,market_price,json);
        }
    }

    private void buy_market_to_sell_market(Order_Copy order_buy, Order_Copy order_sell, double market_price, JSONObject json){
        int vol_sell = order_sell.getO_vol();
        if(vol_sell > order_buy.getO_vol()){
            //Buy可以成交
            //生成actual order
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Actual_Order actual_order = new Actual_Order();

            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(order_buy.getO_vol());
            actual_order.setAo_price(market_price*order_buy.getO_vol());
            actual_order.setBuy_o_id(order_buy.getFormer_o_id());
            actual_order.setSell_o_id(order_sell.getFormer_o_id());
            actual_order.setC_id(order_buy.getC_id());
            actual_order.setAo_year(order_buy.getO_year());
            actual_order.setAo_month(order_buy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_buy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }

            actual_order.setBu_id(BUID);

            //插入actual_order表
            actualOrderService.insertActualOrder(actual_order);

            //更新Sell vol
            int result = vol_sell - order_buy.getO_vol();
            orderCopyService.updateOnSell(order_sell.getO_id(),result,"Part_Completed");

            //消息队列操作
            order_sell.setO_vol(result);
            JSONObject jsonOrderSell = JSONObject.fromObject(order_sell);
            jsonOrderSell.put("o_status","Part_Completed");
            //producerService.sendMessage(queue2,jsonOrderSell.toString());

            json.put("o_status","Completed");
            //producerService.sendMessage(queue2,json.toString());

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",order_buy.getO_vol());
            ao.put("ao_price",market_price*order_buy.getO_vol());
            ao.put("buy_o_id",order_buy.getFormer_o_id());
            ao.put("sell_o_id",order_sell.getFormer_o_id());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_buy.getC_id());
            mdJSON.put("o_year",order_buy.getO_year());
            mdJSON.put("o_month",order_buy.getO_month());
            mdJSON.put("isFloat",order_buy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());
            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderSell.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderSell.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderSell.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }

            order_buy.setO_status("Completed");
            orderCopyService.insertIntoBuyFinished(order_buy);
            order_buy.setO_vol(0);
        }

        else if(vol_sell == order_buy.getO_vol())
        {
            //buy 成交
            //删掉sell
            //生成actual order
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Actual_Order actual_order = new Actual_Order();
            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(order_buy.getO_vol());
            actual_order.setAo_price(market_price*order_buy.getO_vol());
            actual_order.setBuy_o_id(order_buy.getFormer_o_id());
            actual_order.setSell_o_id(order_sell.getFormer_o_id());
            actual_order.setC_id(order_buy.getC_id());
            actual_order.setAo_year(order_buy.getO_year());
            actual_order.setAo_month(order_buy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_buy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }
            actual_order.setBu_id(BUID);

            //插入order完成表
            actualOrderService.insertActualOrder(actual_order);

            //消息队列操作
            JSONObject jsonOrderSell = JSONObject.fromObject(order_sell);
            jsonOrderSell.put("o_status","Completed");
            //producerService.sendMessage(queue2,jsonOrderSell.toString());
            order_sell.setO_status("Completed");
            orderCopyService.insertIntoSellFinished(order_sell);
            order_sell.setO_vol(0);
            orderCopyService.deleteOrderFromSell(order_sell.getO_id());

            json.put("o_status","Completed");
            //producerService.sendMessage(queue2,json.toString());

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",order_buy.getO_vol());
            ao.put("ao_price",market_price*order_buy.getO_vol());
            ao.put("buy_o_id",order_buy.getFormer_o_id());
            ao.put("sell_o_id",order_sell.getFormer_o_id());

            //将actual order返给queue3
            //producerService.sendMessage(queue3, ao.toString());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_buy.getC_id());
            mdJSON.put("o_year",order_buy.getO_year());
            mdJSON.put("o_month",order_buy.getO_month());
            mdJSON.put("isFloat",order_buy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);
            /*mdJSON.put("buy",orderCopyService.getAllSpecificBuyOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat()));
            mdJSON.put("sell",orderCopyService.getAllSpecificSellOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat()));*/

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());

            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderSell.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderSell.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderSell.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }

            order_buy.setO_status("Completed");
            orderCopyService.insertIntoBuyFinished(order_buy);
            order_buy.setO_vol(0);
        }

        else{
            //部分成交
            //删掉sell，插入buy
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Actual_Order actual_order = new Actual_Order();
            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(order_buy.getO_vol());
            actual_order.setAo_price(market_price*order_buy.getO_vol());
            actual_order.setBuy_o_id(order_buy.getFormer_o_id());
            actual_order.setSell_o_id(order_sell.getFormer_o_id());
            actual_order.setC_id(order_buy.getC_id());
            actual_order.setAo_year(order_buy.getO_year());
            actual_order.setAo_month(order_buy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_buy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_buy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }
            actual_order.setBu_id(BUID);

            //插入order完成表
            actualOrderService.insertActualOrder(actual_order);

            //消息队列操作
            JSONObject jsonOrderSell = JSONObject.fromObject(order_sell);
            jsonOrderSell.put("o_status","Completed");
            //producerService.sendMessage(queue2,jsonOrderSell.toString());
            order_sell.setO_status("Completed");
            orderCopyService.insertIntoSellFinished(order_sell);
            order_sell.setO_vol(0);
            orderCopyService.deleteOrderFromSell(order_sell.getO_id());

            json.put("o_status","Part_Completed");
            //producerService.sendMessage(queue2,json.toString());
            order_buy.setO_vol(order_buy.getO_vol() - vol_sell);
            order_buy.setO_status("Part_Completed");
            //orderCopyService.updateOrInsertIntoBuy(order_buy);

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",order_buy.getO_vol());
            ao.put("ao_price",market_price*order_buy.getO_vol());
            ao.put("buy_o_id",order_buy.getFormer_o_id());
            ao.put("sell_o_id",order_sell.getFormer_o_id());

            //将actual order返给queue3
            //producerService.sendMessage(queue3, ao.toString());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_buy.getC_id());
            mdJSON.put("o_year",order_buy.getO_year());
            mdJSON.put("o_month",order_buy.getO_month());
            mdJSON.put("isFloat",order_buy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);
            /*mdJSON.put("buy",orderCopyService.getAllSpecificBuyOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat()));
            mdJSON.put("sell",orderCopyService.getAllSpecificSellOrders(order_buy.getC_id(),order_buy.getO_year(),
                    order_buy.getO_month(),order_buy.getIsFloat()));*/

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());

            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderSell.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderSell.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderSell.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }
        }
    }

    private void sell_market_to_buy_market(Order_Copy order_copy, Order_Copy o, double market_price, JSONObject json){
        //o : order_buy
        int vol_buy = o.getO_vol();
        if (vol_buy > order_copy.getO_vol()){
            //sell 成交了
            //删掉sell 表里的订单，放到actual order里
            //更新order_buy里的vol值，减去成交量
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Actual_Order actual_order = new Actual_Order();

            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(order_copy.getO_vol());
            actual_order.setAo_price(market_price*order_copy.getO_vol());
            actual_order.setBuy_o_id(o.getO_id());
            actual_order.setSell_o_id(order_copy.getO_id());
            actual_order.setC_id(order_copy.getC_id());
            actual_order.setAo_year(order_copy.getO_year());
            actual_order.setAo_month(order_copy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_copy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }
            actual_order.setBu_id(BUID);

            //插入actual_order表
            actualOrderService.insertActualOrder(actual_order);

            //删掉原本的sell表记录
            //即不把结果插进sell表
            int result = vol_buy-order_copy.getO_vol();
            orderCopyService.updateOnBuy(o.getO_id(),result,o.getO_status());

            //消息队列操作
            o.setO_vol(result);
            JSONObject jsonOrderBuy = JSONObject.fromObject(o);
            jsonOrderBuy.put("o_status","Part_Completed");
            //producerService.sendMessage(queue2,jsonOrderBuy.toString());

            json.put("o_status","Completed");
            //producerService.sendMessage(queue2,json.toString());

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",order_copy.getO_vol());
            ao.put("ao_price",market_price*order_copy.getO_vol());
            ao.put("buy_o_id",o.getFormer_o_id());
            ao.put("sell_o_id",order_copy.getFormer_o_id());

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ao.toString());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_copy.getC_id());
            mdJSON.put("o_year",order_copy.getO_year());
            mdJSON.put("o_month",order_copy.getO_month());
            mdJSON.put("isFloat",order_copy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());

            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderBuy.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderBuy.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderBuy.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }

            order_copy.setO_status("Completed");
            orderCopyService.insertIntoSellFinished(order_copy);
            order_copy.setO_vol(0);
        }

        else if(vol_buy == order_copy.getO_vol()){
            //sell 成交了
            Timestamp timestamp = new Timestamp(new Date().getTime());

            //放到actual order里
            Actual_Order actual_order = new Actual_Order();

            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(vol_buy);
            actual_order.setAo_price(market_price*vol_buy);
            actual_order.setBuy_o_id(o.getO_id());
            actual_order.setSell_o_id(order_copy.getO_id());
            actual_order.setC_id(order_copy.getC_id());
            actual_order.setAo_year(order_copy.getO_year());
            actual_order.setAo_month(order_copy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_copy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }
            actual_order.setBu_id(BUID);

            //插入order完成表
            actualOrderService.insertActualOrder(actual_order);

            //消息队列操作
            JSONObject jsonOrderBuy = JSONObject.fromObject(o);
            jsonOrderBuy.put("o_status","Completed");
            //producerService.sendMessage(queue2,jsonOrderBuy.toString());
            o.setO_status("Completed");
            orderCopyService.insertIntoBuyFinished(o);
            o.setO_vol(0);
            orderCopyService.deleteOrderFromBuy(o.getO_id());

            json.put("o_status","Completed");
            //producerService.sendMessage(queue2,json.toString());

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",order_copy.getO_vol());
            ao.put("ao_price",market_price*order_copy.getO_vol());
            ao.put("buy_o_id",o.getFormer_o_id());
            ao.put("sell_o_id",order_copy.getFormer_o_id());

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ao.toString());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_copy.getC_id());
            mdJSON.put("o_year",order_copy.getO_year());
            mdJSON.put("o_month",order_copy.getO_month());
            mdJSON.put("isFloat",order_copy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);
            /*mdJSON.put("buy",orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat()));
            mdJSON.put("sell",orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat()));*/

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());

            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderBuy.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderBuy.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderBuy.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }

            order_copy.setO_status("Completed");
            orderCopyService.insertIntoSellFinished(order_copy);
            order_copy.setO_vol(0);
        }
        else {
            //sell 成交一部分
            Timestamp timestamp = new Timestamp(new Date().getTime());

            //放进actual order
            //放到actual order里
            Actual_Order actual_order = new Actual_Order();

            //生成actual order
            actual_order.setAo_create_time(timestamp);
            actual_order.setAo_vol(vol_buy);
            actual_order.setAo_price(market_price*vol_buy);
            actual_order.setBuy_o_id(o.getFormer_o_id());
            actual_order.setSell_o_id(order_copy.getFormer_o_id());
            actual_order.setC_id(order_copy.getC_id());
            actual_order.setAo_year(order_copy.getO_year());
            actual_order.setAo_month(order_copy.getO_month());
            actual_order.setMarket_price(market_price);
            switch (order_copy.getO_type())
            {
                case "Market":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.05d);
                    break;
                case "Limit":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.08d);
                    break;
                case "Stop":
                    actual_order.setCommission(market_price*order_copy.getO_vol()*0.1d);
                    break;
                default:
                    break;
            }
            actual_order.setBu_id(BUID);

            //插入order完成表
            actualOrderService.insertActualOrder(actual_order);

            //消息队列操作
            JSONObject jsonOrderBuy = JSONObject.fromObject(o);
            jsonOrderBuy.put("o_status","Completed");
            //producerService.sendMessage(queue2,jsonOrderBuy.toString());
            o.setO_status("Completed");
            orderCopyService.insertIntoBuyFinished(o);
            o.setO_vol(0);
            orderCopyService.deleteOrderFromBuy(o.getO_id());

            json.put("o_status","Part_Completed");
            //producerService.sendMessage(queue2,json.toString());
            int result = order_copy.getO_vol() - vol_buy;
            order_copy.setO_vol(result);
            order_copy.setO_status("Part_Completed");
            //orderCopyService.updateOrInsertIntoSell(order_copy);

            JSONObject ao = new JSONObject();
            ao.put("ao_create_time",timestamp.toString());
            ao.put("ao_vol",o.getO_vol());
            ao.put("ao_price",market_price*o.getO_vol());
            ao.put("buy_o_id",o.getFormer_o_id());
            ao.put("sell_o_id",order_copy.getFormer_o_id());

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ao.toString());

            //绑定market depth
            JSONObject mdJSON = new JSONObject();
            mdJSON.put("c_id",order_copy.getC_id());
            mdJSON.put("o_year",order_copy.getO_year());
            mdJSON.put("o_month",order_copy.getO_month());
            mdJSON.put("isFloat",order_copy.getIsFloat());

            List<Order_Copy> buys = orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray buy_ja = new JSONArray();
            for(Order_Copy oc : buys)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                buy_ja.add(jo);
            }
            mdJSON.put("buy",buy_ja);

            List<Order_Copy> sells = orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat());
            JSONArray sell_ja = new JSONArray();
            for(Order_Copy oc : sells)
            {
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
                JSONObject jo = JSONObject.fromObject(oc, config);
                sell_ja.add(jo);
            }
            mdJSON.put("sell",sell_ja);
            /*mdJSON.put("buy",orderCopyService.getAllSpecificBuyOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat()));
            mdJSON.put("sell",orderCopyService.getAllSpecificSellOrders(order_copy.getC_id(),order_copy.getO_year(),
                    order_copy.getO_month(),order_copy.getIsFloat()));*/

            JSONObject ret = new JSONObject();
            ret.put("actual_order",ao);
            ret.put("market_depth",mdJSON);

            //将actual order返给queue3
            //producerService.sendMessage(queue3,ret.toString());

            //websocket
            chatController.sendMessage(ret);

            switch (json.getInt("c_id"))
            {
                case 1:
                    topicProvider.publish(gold_destination,json.toString());
                    topicProvider.publish(gold_destination,jsonOrderBuy.toString());
                    topicProvider.publish(gold_destination,ret.toString());
                    break;
                case 2:
                    topicProvider.publish(corn_destination,json.toString());
                    topicProvider.publish(corn_destination,jsonOrderBuy.toString());
                    topicProvider.publish(corn_destination,ret.toString());
                    break;
                case 3:
                    topicProvider.publish(crude_destination,json.toString());
                    topicProvider.publish(crude_destination,jsonOrderBuy.toString());
                    topicProvider.publish(crude_destination,ret.toString());
                    break;
                default:
                    break;
            }
        }
    }

    private double returnMarketPrice(int cid){
        List<Order_Copy> sell_Limit_or_Stop = orderCopyService.marketOrderPriceSets(cid);
        if(sell_Limit_or_Stop.isEmpty()){
            return 150.0d;
        }

        double ans = Double.MAX_VALUE;
        for(Order_Copy o : sell_Limit_or_Stop){
            double price = o.getO_limit_value();
            if(price < ans){
                ans = price;
            }
        }
        return ans;
    }
}
