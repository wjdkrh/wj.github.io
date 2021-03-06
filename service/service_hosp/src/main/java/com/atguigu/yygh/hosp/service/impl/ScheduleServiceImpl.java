package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.DateUtil;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ScheduleServiceImpl * @Description TODO
 * @Author ehdk
 * @Date 20:06 2022/7/13
 * @Version 1.0
 **/
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Override
    public void saveSchedule(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        Schedule existSchedule = scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if (existSchedule!=null){
            schedule.setId(existSchedule.getId());
            schedule.setCreateTime(existSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
        }
        scheduleRepository.save(schedule);
    }

    @Override
    public Page<Schedule> findSchedulePage(Map<String, Object> map) {
        String hoscode =(String) map.get("hoscode");
        String page =(String) map.get("page");
        String limit =(String) map.get("limit");
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        Example<Schedule> example = Example.of(schedule);
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit));
        return scheduleRepository.findAll(example, pageable);
    }

    @Override
    public void removeSchedule(Map<String, Object> map) {
        String hoscode = (String)map.get("hoscode");
        String hosScheduleId =(String) map.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        scheduleRepository.delete(schedule);
    }

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber")
                        .count().as("docCount"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "workDate")),
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit));
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        mappedResults.forEach(r->{
            String dayOfWeek = DateUtil.getDayOfWeek(new DateTime(r.getWorkDate()));
            r.setDayOfWeek(dayOfWeek);
        });

        Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate"));
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
       /* List<BookingScheduleRuleVo> list = totalAggResults.getMappedResults();
        log.info("???????????????list??????->{}",list);
        Query query = new Query(criteria);
        List<Schedule> schedules = mongoTemplate.find(query, Schedule.class);
        log.info("schedules->{}",schedules);*/
        HashMap<String, Object> map = new HashMap<>();
        map.put("list",mappedResults);
        map.put("total",total);
        return map;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        Date date = new DateTime(workDate).toDate();
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").is(date);
        Query query = new Query(criteria);
        return  mongoTemplate.find(query, Schedule.class);
    }


    private List<Date> getDateList(BookingRule bookingRule) {
        //TODO 1.????????????????????????????????????????????????BookingRule????????? ????????????5
        Integer cycle = bookingRule.getCycle();
        //TODO 2.?????????????????????????????????????????????????????????ReleaseTime???
        String releaseTimeStr = bookingRule.getReleaseTime();
        DateTime dateTime = this.getDateTime(new DateTime(), releaseTimeStr);
        //TODO 3.???????????????????????????????????????????????????????????????????????????????????????1.
        if (dateTime.isBeforeNow()){
            cycle+=1;
        }
        //TODO 4.??????????????????
        ArrayList<Date> dateArrayList = new ArrayList<>();
        DateTime date = new DateTime();
        for (int i = 0; i <cycle; i++) {
            DateTime dateTimePlus = date.plusDays(i);
            String dateStr = dateTimePlus.toString("yyyy-MM-dd");
            dateArrayList.add(new DateTime(dateStr).toDate());
        }
        return dateArrayList;
    }
        //????????????????????????????????????
    private DateTime getDateTime(DateTime dateTime ,String hm){
        String dateTimeSrt = dateTime.toString("yyyy-MM-dd");
        String date = dateTimeSrt+" "+hm;
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(date);
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(String hoscode, String depcode) {
        //TODO 1.???????????????????????????????????????????????????????????????????????????
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        BookingRule bookingRule = hospital.getBookingRule();
        List<Date> dateList = this.getDateList(bookingRule);
        //TODO 2.?????????????????????code?????????dateList?????????????????????????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"));
            //TODO 3.??????????????????????????????????????????????????????????????????vo?????????????????????????????????????????????????????????????????????
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();
        //TODO 4.?????????key,??????????????????VO???????????????????????????????????????????????????????????????VO?????????;
        Map<Date, BookingScheduleRuleVo> voMap = bookingScheduleRuleVoList.stream().collect(Collectors.toMap(b -> b.getWorkDate(), b -> b));
        ArrayList<BookingScheduleRuleVo> result = new ArrayList<>();
        //TODO 5.???????????????????????????????????????????????????
        for (Date d : dateList) {
            BookingScheduleRuleVo bookingScheduleRuleVo = voMap.get(d);
            if (bookingScheduleRuleVo==null){
                bookingScheduleRuleVo =new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(d);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setStatus(0);
            String dayOfWeek = DateUtil.getDayOfWeek(new DateTime(d));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            result.add(bookingScheduleRuleVo);
        }
        //???????????????????????????????????????
        result.get(result.size()-1).setStatus(1);
        //???????????????????????????????????????????????????????????????????????????????????????
        String stopTimeStr = bookingRule.getStopTime();
        DateTime stopTime = this.getDateTime(new DateTime(), stopTimeStr);
        if (stopTime.isBefore(new DateTime())){
            result.get(0).setStatus(-1);
        }
        //??????????????????
        Map<String, String> info = new HashMap<>();
        //????????????
        info.put("hosname", hospital.getHosname());
        //??????
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        //???????????????
        info.put("bigname", department.getBigname());
        //????????????
        info.put("depname", department.getDepname());
        //????????????
        info.put("workDateString", new DateTime().toString("yyyy???MM???"));
        //????????????
        info.put("releaseTime", bookingRule.getReleaseTime());
        //????????????
        info.put("stopTime", bookingRule.getStopTime());

        Map<String, Object> bookingScheduleInfo = new HashMap<>();
        //?????????????????????
        bookingScheduleInfo.put("bookingScheduleList", result);//??????????????????
        bookingScheduleInfo.put("info", info);//??????????????????
        return bookingScheduleInfo;
    }

    @Override
    public Schedule getDetailById(String id) {
        Optional<Schedule> optionalSchedule= scheduleRepository.findById(id);
        Schedule schedule = optionalSchedule.get();
        Hospital hospital = hospitalRepository.findByHoscode(schedule.getHoscode());
        Department department = departmentRepository.findByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode());
        String dayOfWeek = DateUtil.getDayOfWeek(new DateTime(schedule.getWorkDate()));
        schedule.getParam().put("hosname",hospital.getHosname());
        schedule.getParam().put("depname",department.getDepname());
        schedule.getParam().put("dayOfWeek",dayOfWeek);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {

        Schedule schedule = this.getDetailById(scheduleId);
        //????????????
        Date workDate = schedule.getWorkDate();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        scheduleOrderVo.setHosname((String) schedule.getParam().get("hosname"));
        scheduleOrderVo.setDepname((String) schedule.getParam().get("depname"));
        scheduleOrderVo.setReserveDate(workDate);
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //TODO ?????????????????????????????????????????????????????????????????????????????????????????????
        Hospital hospital = hospitalRepository.findByHoscode(schedule.getHoscode());
        BookingRule bookingRule = hospital.getBookingRule();
        Integer cycle = bookingRule.getCycle();
        String releaseTime = bookingRule.getReleaseTime();
        String stopTime = bookingRule.getStopTime();
        Integer quitDay = bookingRule.getQuitDay();
        String quitTime = bookingRule.getQuitTime();
        //?????????????????????
        DateTime quitDateTime = this.getDateTime(new DateTime(workDate).plusDays(quitDay), quitTime);
        scheduleOrderVo.setQuitTime(quitDateTime.toDate());
        //???????????????????????????
        scheduleOrderVo.setStartTime(this.getDateTime(new DateTime(),releaseTime).toDate());
        //???????????????????????????
        scheduleOrderVo.setStopTime(this.getDateTime(new DateTime(),stopTime).toDate());
        //?????????????????? ????????????????????????????????????????????????
        scheduleOrderVo.setEndTime(this.getDateTime(new DateTime().plusDays(cycle-1),stopTime).toDate());
        return scheduleOrderVo;
    }

    @Override
    public void updateNumber(OrderMqVo orderMqVo) {
        Schedule schedule = scheduleRepository.findById(orderMqVo.getScheduleId()).get();
        schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
        schedule.setReservedNumber(orderMqVo.getReservedNumber());
        scheduleRepository.save(schedule);
    }
}
