package com.atguigu.yygh.rabbit.service;

import com.atguigu.yygh.rabbit.config.MQConst;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.sms.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     *  发送消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        log.info("发送消息...........");
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

    public void sendOrderMqVo(OrderMqVo orderMqVo){
        log.info("生产者同步预约人数信息");
        rabbitTemplate.convertAndSend(MQConst.EXCHANGE_DIRECT_ORDER,MQConst.ROUTING_ORDER,orderMqVo);
    }

    public void sendSmsVo(SmsVo smsVo){
        log.info("生产者发送短信");
        rabbitTemplate.convertAndSend(MQConst.EXCHANGE_DIRECT_SMS,MQConst.ROUTING_SMS,smsVo);
    }

    public void remindPatientTask(String exchange,String routingKey){
        log.info("定时任务开始调用orderservice。。。。");
        rabbitTemplate.convertAndSend(exchange,routingKey,"");
    }

}