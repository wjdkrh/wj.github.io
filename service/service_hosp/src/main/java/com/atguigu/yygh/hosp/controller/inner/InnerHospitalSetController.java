package com.atguigu.yygh.hosp.controller.inner;

import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName InnerHospitalSetController * @Description TODO
 * @Author ehdk
 * @Date 20:01 2022/7/27
 * @Version 1.0
 **/
@RestController
@RequestMapping("/inner/hosp/hospitalset")
@Api(tags = "获取医院设置接口")
public class InnerHospitalSetController {
    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    @GetMapping("getHospitalSet/{hoscode}")
    @ApiOperation("获取医院设置")
    public HospitalSet getHospitalSet(@PathVariable String hoscode){
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>().eq("hoscode", hoscode));
        return hospitalSet;
    }

}
