package com.atguigu.yygh.hosp.controller.front;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "预约接口")
@RestController
@RequestMapping("/front/hosp/schedule")
public class FrontScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("获取可预约排班日期数据")
    @GetMapping("getBookingScheduleRule/{hoscode}/{depcode}")
    public R getBookingScheduleRule(@PathVariable String hoscode, @PathVariable String depcode) {

        Map<String, Object> map = scheduleService.getBookingScheduleRule(hoscode, depcode);
        return R.ok().data(map);
    }

    @ApiOperation("获取排班数据")
    @GetMapping("getScheduleList/{hoscode}/{depcode}/{workDate}")
    public R getScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate) {
        List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return R.ok().data("scheduleList",scheduleList);
    }

    @ApiOperation("获取预约详情")
    @GetMapping("getScheduleDetail/{id}")
    public R getScheduleDetail(@PathVariable String id) {
        Schedule schedule = scheduleService.getDetailById(id);
        return R.ok().data("schedule",schedule);
    }
}