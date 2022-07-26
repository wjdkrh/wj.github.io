package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.hosp.utils.Result;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
//通过医院端调用平台端接口时接收参数是HttpServletRequest request
//通过request.getParameterMap();参数是map类型《String, String[]》，
//通过把数组的第一个参数取出来放到新map中 map<String,object>,在通过JSONObject转换成String的JSON字符串，
//在通过JSONObject.parse方法把参数传递到对象中
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private HospitalSetService hospitalSetService;

    @PostMapping("saveHospital")
    public Result<Hospital> savaHospital(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        Map<String,Object> map = new HashMap<>();
        entries.forEach(entry->{
            String[] value = entry.getValue();
            map.put(entry.getKey(), value[0]);
        });

        //logoData是一个图片使用base64的格式存储，数据长，在数据传输的时候会把字段中间的加号改成了空格，需要转换
        String logoData = (String)map.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        map.put("logoData",logoData);
        this.signCheck(map);
        hospitalService.savaHospital(map);
        return Result.ok();
    }

    private void signCheck(Map<String,Object> map){
        String sign = (String)map.get("sign");
        String hoscode = (String) map.get("hoscode");
       String signKey = hospitalSetService.getSignKey(hoscode);
       String signEncryMD5 = this.signEncryMD5(map,signKey);
       if (!sign.equalsIgnoreCase(signEncryMD5)){
            throw new YyghException("签名校验失败", ResultCode.ERROR);
       }
    }

    //所有变量值按照参数名（不包含sign参数）升序用|连接，最后连接signKey
    private String signEncryMD5(Map<String, Object> map, String signKey) {
        map.remove("sign");
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        Set<Map.Entry<String, Object>> entries = treeMap.entrySet();
        StringBuilder stringBuilder =new StringBuilder();
        entries.forEach(entry->{
            stringBuilder.append(entry.getValue()).append("|");
        });
        stringBuilder.append(signKey);
        return MD5.encrypt(stringBuilder.toString());
    }

    @PostMapping("hospital/show")
    public Result<Hospital> showHospital(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
       String hoscode = parameterMap.get("hoscode")[0];
       if (!StringUtils.isEmpty(hoscode)) {
           Hospital hospital = hospitalService.findByHoscode(hoscode);
           return Result.ok(hospital);
       }
       return Result.fail();
    }

    @PostMapping("saveDepartment")
    public Result<Department> saveDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        Map<String,Object> map = new HashMap<>();
        entries.forEach(entry->{
            map.put(entry.getKey(),entry.getValue()[0]);
        });
        this.signCheck(map);
        departmentService.saveDepartment(map);
        return Result.ok();
    }

    @PostMapping("department/list")
    public Result<Page<Department>> showDepatmentPage(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        this.signCheck(map);
        Page  page = departmentService.showDepatmentPage(parameterMap);
         return Result.ok(page);
    }

    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        this.signCheck(map);
        String hoscode = (String)map.get("hoscode");
        String depcode = (String)map.get("depcode");
        if (!StringUtils.isEmpty(hoscode) && !StringUtils.isEmpty(depcode)){
            departmentService.removeDepartment(hoscode,depcode);
            return Result.ok();
        }
        return Result.fail();

    }

    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        this.signCheck(map);
        scheduleService.saveSchedule(map);
        return Result.ok();
    }
    @PostMapping("schedule/list")
    public Result showSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        this.signCheck(map);
       Page<Schedule> page = scheduleService.findSchedulePage(map);
       return Result.ok(page);
    }

    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        this.signCheck(map);
        scheduleService.removeSchedule(map);
        return Result.ok();
    }

}