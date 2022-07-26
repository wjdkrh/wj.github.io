package com.atguigu.yygh.hosp.controller.admin;

/**
 * @ClassName AdminHospitalController * @Description TODO
 * @Author ehdk
 * @Date 16:08 2022/7/15
 * @Version 1.0
 **/

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医院接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class AdminHospitalController {

    //注入service
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public R pageList(
            @PathVariable Integer page, //路径
            @PathVariable Integer limit, //路径
            String hosname /*查询字符串*/) {

        Page<Hospital> pageModel = hospitalService.selectPage(page, limit, hosname);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public R lock(
            @PathVariable String id,
            @PathVariable Integer status){

        if(status != 0 && status != 1){
            return R.error().message("非法数据");
        }

        hospitalService.updateStatus(id, status);
        return R.ok();
    }

    @ApiOperation(value = "获取医院详情")
    @GetMapping("show/{id}")
    public R show(@PathVariable String id) {
        Hospital hospital = hospitalService.show(id);
        return R.ok().data("hospital", hospital);
    }
}