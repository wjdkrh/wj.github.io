package com.atguigu.yygh.order.listener;

import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.rabbit.config.MQConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderInfoListener * @Description TODO
 * @Author ehdk
 * @Date 18:48 2022/8/1
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderInfoListener {
    @Autowired
    OrderInfoService orderInfoService;

    @RabbitListener( bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_TASK,durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_TASK ),
            key = {MQConst.ROUTING_TASK}
    ))
    public void remindPatient(){
        log.info("已开始发就用提醒了。。。");
        orderInfoService.remindPatient();
    }
}
