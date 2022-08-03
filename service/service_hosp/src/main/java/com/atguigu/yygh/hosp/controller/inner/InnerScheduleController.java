package com.atguigu.yygh.hosp.controller.inner;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName InnerScheduleController * @Description TODO
 * @Author ehdk
 * @Date 12:49 2022/7/27
 * @Version 1.0
 **/
@RestController
@RequestMapping("/inner/hosp/schedule")
@Api("排班接口信息组装订单数据")
public class InnerScheduleController {

    @Autowired
    ScheduleService scheduleService;


    @GetMapping("get/{scheduleId}")
    @ApiOperation("获取排班信息数据")
    ScheduleOrderVo getScheduleOrderVo(@PathVariable String scheduleId){
        return  scheduleService.getScheduleOrderVo(scheduleId);
    }

}
