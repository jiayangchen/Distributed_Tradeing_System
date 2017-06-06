### Restful Api 文档

URL 前缀都是一样的  http://59.78.45.101:8080/web-ssm/rest/

> * getBrokerUserByCId/{c_id}
> * 参数 int
> * 返回值 JSONObject
> * 返回值示例:

```
{
  "bu_email": "hmcai@sjtu.edu.cn",
  "bu_id": 1,
  "bu_name": "caihongming",
  "bu_password": "caihongming"
}
```

> * getCommodityListByTId/{t_id}
> * 参数 int
> * 返回值 JSONArray
> * 返回值示例:
```
[
  {
    "bu_id": 1,
    "c_id": 1,
    "c_name": "gold",
    "c_price": 100
  },
  {
    "bu_id": 1,
    "c_id": 2,
    "c_name": "corn",
    "c_price": 100
  }
]
```

> * retMarketDepth?cid=x1&year=x2&month=x3&isFloat=x4
> * 参数 均为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "c_id": 1,
  "o_year": 2018,
  "o_month": 6,
  "isFloat": 0,
  "buy": [],
  "sell": []
}
```

> * getAllBrokerUsers
> * 参数 null
> * 返回值 JSONArray
> * 返回值示例:
```
[
  {
    "bu_email": "hmcai@sjtu.edu.cn",
    "bu_id": 1,
    "bu_name": "caihongming"
  }
]
```

> * getAllCommodityByBuId/{bu_id}
> * 参数 int
> * 返回值 JSONArray
> * 返回值示例:
```
[
  {
    "bu_id": 1,
    "c_id": 1,
    "c_name": "gold",
    "c_price": 100
  },
  {
    "bu_id": 1,
    "c_id": 2,
    "c_name": "corn",
    "c_price": 100
  },
  {
    "bu_id": 1,
    "c_id": 3,
    "c_name": "crude",
    "c_price": 100
  }
]
```

> * getGuidePrice?cid=x1&g_year=x2&g_month=x3
> * 参数 均为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "c_id": 1,
  "g_month": 5,
  "g_year": 2018,
  "gp_id": 0,
  "guide_price": 100
}
```

> * query_cid?cid=x1
> * 参数为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "minMarketPrice": 100,
  "maxMarketPrice": 130,
  "minVol": 5,
  "maxVol": 150,
  "actual_orders": [
    {
      "ao_create_time": "2017-06-03 16:23:55",
      "ao_id": 23,
      "ao_month": 5,
      "ao_price": 8000,
      "ao_vol": 100,
      "ao_year": 2018,
      "buy_o_id": 2,
      "c_id": 1,
      "market_price": 100,
      "sell_o_id": 1
    },...
```

> * query_cid_time?cid=x1&s_year=x2&s_month=x3&e_year=x4&e_month=x5
> * 参数均为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "minMarketPrice": 100,
  "maxMarketPrice": 130,
  "minVol": 5,
  "maxVol": 150,
  "actual_orders": [
    {
      "ao_create_time": "2017-06-03 16:23:55",
      "ao_id": 23,
      "ao_month": 5,
      "ao_price": 8000,
      "ao_vol": 100,
      "ao_year": 2018,
      "buy_o_id": 2,
      "c_id": 1,
      "market_price": 100,
      "sell_o_id": 1
    },...
```

> * retStatistic?cid=x1&year=x2&month=x3
> * 参数均为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "cid": 1,
  "year": 2018,
  "month": 5,
  "statistic": [
    {
      "2017-4": 60
    },
    {
      "2017-5": 90
    },
    {
      "2017-6": 50.88
    }
  ]
}
```

> * retCommission?cid=x1&year=x2&month=x3
> * 参数均为 int
> * 返回值 JSONObject
> * 返回值示例:
```
{
  "cid": 1,
  "year": 2018,
  "month": 4,
  "commission": 18233
}
```
