package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.rabbit.config.MQConst;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.sms.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName SmsListener * @Description TODO
 * @Author ehdk
 * @Date 23:06 2022/7/31
 * @Version 1.0
 **/
@Component
@Slf4j
public class SmsListener {
    @Autowired
    SmsService smsService;

    @RabbitListener(queues = MQConst.QUEUE_SMS)
    public void receive(SmsVo smsVo ){
        log.info("消费者接收到消息：{}",smsVo);
        smsService.sendMsg(smsVo);
    }
}
