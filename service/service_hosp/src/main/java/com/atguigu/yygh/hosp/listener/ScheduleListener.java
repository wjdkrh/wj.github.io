package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.rabbit.config.MQConst;
import com.atguigu.yygh.vo.order.OrderMqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName ScheduleListener * @Description TODO
 * @Author ehdk
 * @Date 23:28 2022/7/31
 * @Version 1.0
 **/
@Component
@Slf4j
public class ScheduleListener {
    @Autowired
    ScheduleService scheduleService;

    @RabbitListener(queues = MQConst.QUEUE_ORDER)
    public void Receive(OrderMqVo orderMqVo){
        log.info("消费者Order已接收到消息：{}",orderMqVo);
        scheduleService.updateNumber(orderMqVo);
    }
}
