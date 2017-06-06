package com.cjy.ssm.controller;

import com.cjy.ssm.model.*;
import com.cjy.ssm.service.*;
import com.cjy.ssm.utils.DateJsonValueProcessor;
import com.cjy.ssm.utils.MyHttpHeader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.ws.rs.GET;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by ChenJiayang on 2017/5/23.
 */

@CrossOrigin
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest")
public class RestController {

    @Autowired
    CommodityService commodityService;

    @Autowired
    BrokerUserService brokerUserService;

    @Autowired
    TraderSubService traderSubService;

    @Autowired
    OrderCopyService orderCopyService;

    @Autowired
    GuidePriceService guidePriceService;

    @Autowired
    ActualOrderService actualOrderService;

    @RequestMapping(value="/getBrokerUserByCId/{id}",method= RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> getBrokerUserByCId(@PathVariable int id)
    {
        Commodity commodity = commodityService.getCommodityByCId(id);
        int buId = commodity.getBu_id();
        Broker_User broker_user = brokerUserService.getBrokerUserById(buId);
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        JSONObject ret = new JSONObject();
        if(broker_user != null)
        {
            ret = JSONObject.fromObject(broker_user);
        }
        else
        {
            ret.put("message","No Such BrokerUser");
        }
        return new ResponseEntity<>(ret,headers, HttpStatus.OK);
    }

    @RequestMapping(value="/getCommodityListByTId/{id}",method= RequestMethod.GET)
    public ResponseEntity<JSONArray> getCommodityListByTId(@PathVariable int id)
    {

        List<Trader_Subscribe> traderSubList = traderSubService.getTrader_SubscribeListByTId(id);
        JSONArray jsonArray = new JSONArray();
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        for(Trader_Subscribe t : traderSubList)
        {
            int c_id = t.getC_id();
            Commodity c = commodityService.getCommodityByCId(c_id);
            jsonArray.add(JSONObject.fromObject(c));
        }
        if(jsonArray.isEmpty())
        {
            return null;
        }
        else
        {
            return new ResponseEntity<>(jsonArray,headers,HttpStatus.OK);
        }
    }

    @RequestMapping(value="/retMarketDepth",method= RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> retMarketDepth(@RequestParam("cid") int cid,
                                                     @RequestParam("year") int year,
                                                     @RequestParam("month") int month,
                                                     @RequestParam("isFloat") int isFloat)
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();


        JSONArray buy_ja = new JSONArray();
        JSONArray sell_ja = new JSONArray();

        List<Order_Copy> buy_orders = orderCopyService.getAllSpecificBuyOrders(cid,year,month,isFloat);
        List<Order_Copy> sell_orders = orderCopyService.getAllSpecificSellOrders(cid,year,month,isFloat);

        for(Order_Copy oc : buy_orders)
        {
            JsonConfig config = new JsonConfig();
            config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
            JSONObject jo = JSONObject.fromObject(oc, config);
            buy_ja.add(jo);
        }

        for(Order_Copy oc : sell_orders)
        {
            JsonConfig config = new JsonConfig();
            config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
            JSONObject jo = JSONObject.fromObject(oc, config);
            sell_ja.add(jo);
        }

        JSONObject json = new JSONObject();
        json.put("c_id",cid);
        json.put("o_year",year);
        json.put("o_month",month);
        json.put("isFloat",isFloat);
        json.put("buy",buy_ja);
        json.put("sell",sell_ja);

        return new ResponseEntity<>(json,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllBrokerUsers", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONArray> getAllBrokerUsers()
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        List<Broker_User> allBrokerUsers = brokerUserService.getAllBrokerUsers();
        JSONArray jsonArray = JSONArray.fromObject(allBrokerUsers);
        for(int i=0; i<jsonArray.size(); i++)
        {
            JSONObject json = jsonArray.getJSONObject(i);
            json.put("bu_password",null);
        }
        return new ResponseEntity<>(jsonArray,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllCommodityByBuId/{buid}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONArray> getAllCommodityByBuId(@PathVariable int buid)
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        List<Commodity> allCommodityInBuId = commodityService.getAllCommodityByBuId(buid);
        JSONArray jsonArray = JSONArray.fromObject(allCommodityInBuId);
        return new ResponseEntity<>(jsonArray,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "/getGuidePrice", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> getGuidePrice(@RequestParam("cid") int cid,
                                                    @RequestParam("g_year") int g_year,
                                                    @RequestParam("g_month") int g_month)
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        Guide_Price guide_price = guidePriceService.getGuidePrice(cid,g_year,g_month);
        JSONObject jsonObject = JSONObject.fromObject(guide_price);
        return new ResponseEntity<>(jsonObject,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "query_cid", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> retPriceTrend(@RequestParam("cid") int cid)
    {
        List<Actual_Order> AOByCId = actualOrderService.getAllAOByCId(cid);
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        JSONArray jsonArray = new JSONArray();
        ArrayList<Integer> vol = new ArrayList<>();
        ArrayList<Double> price = new ArrayList<>();

        for(Actual_Order ao : AOByCId)
        {
            vol.add(ao.getAo_vol());
            price.add(ao.getMarket_price());
            JsonConfig config = new JsonConfig();
            config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
            JSONObject jo = JSONObject.fromObject(ao, config);
            jsonArray.add(jo);
        }

        JSONObject json = new JSONObject();
        json.put("minMarketPrice", Collections.min(price));
        json.put("maxMarketPrice", Collections.max(price));
        json.put("minVol", Collections.min(vol));
        json.put("maxVol", Collections.max(vol));
        json.put("actual_orders",jsonArray);

        return new ResponseEntity<>(json,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "query_cid_time", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> retPriceTrend(@RequestParam("cid") int cid,
                                                   @RequestParam("s_year") int s_year,
                                                   @RequestParam("s_month") int s_month,
                                                   @RequestParam("e_year") int e_year,
                                                   @RequestParam("e_month") int e_month)
    {
        List<Actual_Order> AOByCId = actualOrderService.getAllAOByCIdandTime(cid,s_year,s_month,e_year,e_month);
        ArrayList<Integer> vol = new ArrayList<>();
        ArrayList<Double> price = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for(Actual_Order ao : AOByCId)
        {
            vol.add(ao.getAo_vol());
            price.add(ao.getMarket_price());
            JsonConfig config = new JsonConfig();
            config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
            JSONObject jo = JSONObject.fromObject(ao, config);
            jsonArray.add(jo);
        }

        HttpHeaders headers = MyHttpHeader.getHttpHeaders();

        JSONObject json = new JSONObject();
        json.put("minMarketPrice", Collections.min(price));
        json.put("maxMarketPrice", Collections.max(price));
        json.put("minVol", Collections.min(vol));
        json.put("maxVol", Collections.max(vol));
        json.put("actual_orders",jsonArray);

        return new ResponseEntity<>(json,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "retStatistic", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> retStatistic(@RequestParam("cid") int cid,
                                                  @RequestParam("year") int year,
                                                  @RequestParam("month") int month)
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        List<Actual_Order> list = actualOrderService.getAOByCIdandTime(cid,year,month);
        List<Double> tmp = new ArrayList<>();

        if(list.size() == 0){
            JSONObject json = new JSONObject();
            JSONArray stat = new JSONArray();
            json.put("cid",cid);
            json.put("year",year);
            json.put("month",month);
            json.put("statistic",stat);
            return new ResponseEntity<>(json,headers,HttpStatus.OK);
        }

        Timestamp tp = list.get(0).getAo_create_time();
        Calendar c = Calendar.getInstance();
        c.setTime(tp);
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        JSONObject json = new JSONObject();
        JSONArray stat = new JSONArray();
        json.put("cid",cid);
        json.put("year",year);
        json.put("month",month);


        for(int j=0; j<list.size(); j++)
        {
            Actual_Order ao = list.get(j);
            Timestamp timestamp = ao.getAo_create_time();
            Calendar cc = Calendar.getInstance();
            cc.setTime(timestamp);
            int taryy = cc.get(Calendar.YEAR);
            int tarmm = cc.get(Calendar.MONTH);
            if(taryy == yy && tarmm == mm)
            {
                tmp.add(ao.getMarket_price());
                yy = taryy;
                mm = tarmm;
                if(j == list.size()-1)
                {
                    JSONObject t_v = new JSONObject();
                    double ans = 0.0;
                    for(double i : tmp)
                    {
                        ans+=i;
                    }
                    t_v.put(yy+"-"+(mm+1),ans/tmp.size());
                    stat.add(t_v);
                }
            }
            else
            {
                JSONObject t_v = new JSONObject();
                double ans = 0.0;
                for(double i : tmp)
                {
                    ans+=i;
                }
                t_v.put(yy+"-"+(mm+1),ans/tmp.size());
                stat.add(t_v);
                tmp.clear();
                tmp.add(ao.getMarket_price());
                yy = taryy;
                mm = tarmm;

                if(j == list.size()-1)
                {
                    JSONObject tt = new JSONObject();
                    tt.put(yy+"-"+(mm+1),ao.getMarket_price());
                    stat.add(tt);
                }
            }
        }

        json.put("statistic",stat);
        return new ResponseEntity<>(json,headers,HttpStatus.OK);
    }

    @RequestMapping(value = "/retCommission",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<JSONObject> retCommission(@RequestParam("cid") int cid,
                                                    @RequestParam("year") int year,
                                                    @RequestParam("month") int month)
    {
        HttpHeaders headers = MyHttpHeader.getHttpHeaders();
        List<Actual_Order> list = actualOrderService.getAOByCIdandTime(cid,year,month);
        double ans = 0.0d;
        for(Actual_Order ao : list)
        {
            ans += ao.getCommission();
        }
        JSONObject json = new JSONObject();
        json.put("cid",cid);
        json.put("year",year);
        json.put("month",month);
        json.put("commission",ans);
        return new ResponseEntity<>(json,headers,HttpStatus.OK);
    }
}
