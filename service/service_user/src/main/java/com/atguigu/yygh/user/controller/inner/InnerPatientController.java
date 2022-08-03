package com.atguigu.yygh.user.controller.inner;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName InnerPatientController * @Description TODO
 * @Author ehdk
 * @Date 11:15 2022/7/27
 * @Version 1.0
 **/
@RestController
@RequestMapping("/inner/user/patient")
@Api(tags = "就诊人远程接口调用信息")
public class InnerPatientController {
    @Autowired
    PatientService patientService;

    @GetMapping("get/{patientId}")
    @ApiOperation("获取就诊人信息")
    public Patient getPatient(@PathVariable Long patientId){
        return patientService.getById(patientId);
    }

}
