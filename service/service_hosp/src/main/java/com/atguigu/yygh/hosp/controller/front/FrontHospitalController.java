package com.atguigu.yygh.hosp.controller.front;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName FrontHospitalController * @Description TODO
 * @Author ehdk
 * @Date 21:07 2022/7/18
 * @Version 1.0
 **/
@RestController
@RequestMapping("/front/hosp/hospital")
@Api(tags = "医院接口")
public class FrontHospitalController {
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "根据医院名称、级别和区域查询医院列表")
    @GetMapping("list")
    public R list(String keywords, String levelId, String areaId) {
        List<Hospital> hospitalList = hospitalService.findHospitalInfo(keywords, levelId, areaId);
        return R.ok().data("list", hospitalList);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("show/{hoscode}")
    public R show(@PathVariable String hoscode) {

        Hospital hospital = hospitalService.getDetail(hoscode);
        return R.ok().data("hospital", hospital);
    }

    @Api(tags = "科室接口")
    @RestController
    @RequestMapping("/front/hosp/department")
    @CrossOrigin
    public class FrontDepartmentController {

        @Autowired
        private DepartmentService departmentService;

        @ApiOperation(value = "获取科室列表")
        @GetMapping("getDeptList/{hoscode}")
        public R getDeptList(@PathVariable String hoscode) {
            List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
            return R.ok().data("list", list);
        }

    }
}
