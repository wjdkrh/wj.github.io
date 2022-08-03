package com.atguigu.yygh.task.schedule;

import com.atguigu.yygh.rabbit.config.MQConst;
import com.atguigu.yygh.rabbit.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName ScheduledTask * @Description TODO
 * @Author ehdk
 * @Date 15:58 2022/8/1
 * @Version 1.0
 **/
@EnableScheduling
@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    RabbitService rabbitService;

    @Scheduled(cron ="0 52 20 * * ?" )
    public void remindTask(){
        log.info("定时任务running...");
        rabbitService.remindPatientTask(MQConst.EXCHANGE_DIRECT_TASK,MQConst.ROUTING_TASK);
    }
}
