package com.atguigu.yygh.user.controller.front;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/front/user/patient")
public class FrontPatientController {
    @Autowired
    private PatientService patientService;


    @ApiOperation("添加就诊人")
    @PostMapping("auth/save")
    public R savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok().message("添加成功");
    }
    @ApiOperation("修改就诊人")
    @PutMapping("auth/update")
    public R updatePatient(@RequestBody Patient patient, HttpServletRequest request) {
        AuthContextHolder.checkAuth(request);
        patientService.updateById(patient);
        return R.ok().message("修改成功");

    }

    @ApiOperation("根据id获取就诊人信息")
    @GetMapping("auth/get/{id}")
    public R getPatient(@PathVariable Long id, HttpServletRequest request) {
        AuthContextHolder.checkAuth(request);
        Patient patient = patientService.getPatientInfoById(id);
        if (patient!=null){
            return R.ok().data("patient",patient);
        }
        return R.error().message("查询就诊人信息失败");
    }

    @ApiOperation("获取就诊人列表")
    @GetMapping("auth/findAll")
    public R findAll(HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllByUserId(userId);
        return R.ok().data("list",list);
    }

    @ApiOperation("删除就诊人")
    @DeleteMapping("auth/remove/{id}")
    public R removePatient(@PathVariable Long id, HttpServletRequest request) {
        //安全校验
        AuthContextHolder.checkAuth(request);
        patientService.removeById(id);
        return R.ok();
    }

}

