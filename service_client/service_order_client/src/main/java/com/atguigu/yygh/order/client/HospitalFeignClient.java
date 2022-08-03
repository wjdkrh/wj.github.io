package com.atguigu.yygh.order.client;


import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
public interface HospitalFeignClient {

    @GetMapping("/inner/hosp/schedule/get/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(@PathVariable String scheduleId);


    @GetMapping("/inner/hosp/hospitalset/getHospitalSet/{hoscode}")
     HospitalSet getHospitalSet(@PathVariable String hoscode);




}