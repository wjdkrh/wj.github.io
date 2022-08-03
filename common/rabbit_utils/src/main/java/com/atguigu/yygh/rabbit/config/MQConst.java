package com.atguigu.yygh.rabbit.config;



public class MQConst {
    /**
     * 预约下单/取消订单
     */
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";//交换机
    public static final String ROUTING_ORDER = "routing.order";//路由
    public static final String QUEUE_ORDER  = "queue.order";//消息队列

    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_SMS = "exchange.direct.sms";//交换机
    public static final String ROUTING_SMS = "routing.sms";//路由
    public static final String QUEUE_SMS  = "queue.sms";//消息队列

    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK="exchange.direct.task";
    public static final String ROUTING_TASK="routing.task";
    public static final String QUEUE_TASK="queue.task";


}