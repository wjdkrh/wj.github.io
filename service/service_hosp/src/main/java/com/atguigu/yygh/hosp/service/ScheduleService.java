package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> findSchedulePage(Map<String, Object> map);

    void removeSchedule(Map<String, Object> map);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(String hoscode, String depcode);

    Schedule getDetailById(String id);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    void updateNumber(OrderMqVo orderMqVo);
}
