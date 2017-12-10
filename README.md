# Distributed_Tradeing_System
[![Travis](https://img.shields.io/travis/rust-lang/rust.svg)](https://github.com/jiayangchen/Distributed_Tradeing_System)

### 系统简介
系统分为Trader UI、Trader_Gateway、Broker_Gateway、Broker UI，此项目为Broker_Gateway具体实现

### 项目简介
项目采用 SSM 框架进行开发

### 项目功能
Broker_Gateway 只要负责进行实现复杂订单交易逻辑；支持四种订单类型 Market Order、Stop Order、Limit Order和Cancel Order；维护某种具体商品的Market Depth；采用 ActiveMQ queue 模式创建队列从 Trader_Gateway 接收订单，采用 topic 模型创建成交队列向具体某个公司的所有 Trader User 进行广播； 利用 Redis 缓存可交易的订单信息，定期写回数据库；采用 Websocket 推送 Market Depth 的实时改变到 Broker UI；采用 Nginx 实现多个 Broker_Gateway 之间负载均衡；

### 架构图：
![](http://o9oomuync.bkt.clouddn.com/%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

### License

MIT License



