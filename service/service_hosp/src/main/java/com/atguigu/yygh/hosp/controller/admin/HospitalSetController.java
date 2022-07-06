package com.atguigu.yygh.hosp.controller.admin;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-07-04
 */
@RestController
@RequestMapping("/hosp/hospitalSet")
@Api(tags = "医院设置接口")
public class HospitalSetController {
    @Autowired
    HospitalSetService hospitalSetService;



    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAll() {
        try{
           /* System.out.println(1 / 0);*/
            List<HospitalSet> list = hospitalSetService.list();
            return R.ok().data("list",list);
        }catch (Exception e){
            throw new YyghException("查询异常", ResultCode.ERROR_SELECT,e);
        }

    }

    @ApiOperation(value = "分页医院设置列表")
    @GetMapping("{page}/{limit}")
    public R findAll(@ApiParam(value = "当前页码",required = true)
                        @PathVariable Long page,
                        @ApiParam(value = "每页记录数",required = true)
                        @PathVariable Long limit,
                        @ApiParam(value="查询分页条件")
                        @RequestBody HospitalSetQueryVo hospitalSetQueryVo
                     ){
        Page<HospitalSet> hospitalSetPage = hospitalSetService.seletPage(page,limit,hospitalSetQueryVo);
        long total = hospitalSetPage.getTotal();
        List<HospitalSet> hospitalSetList = hospitalSetPage.getRecords();
        return R.ok().data("total",total).data("rows",hospitalSetList);
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("saveHospset")
    public R save(
            @ApiParam(value = "医院设置对象",required=true)
            @RequestBody HospitalSet hospitalSet){
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+new Random().nextInt(1000)));
        boolean result = hospitalSetService.save(hospitalSet);
        if (result){
            return R.ok().message("添加成功");
        }else {
            return R.error().message("添加失败");
        }
    }

    @ApiOperation(value = "根据ID删除医院设置")
    @DeleteMapping("{id}")
    public R delete(@ApiParam(value = "删除ID",required = true)
                    @PathVariable Long  id){
        boolean result = hospitalSetService.removeById(id);
        if (result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }


    }
    @ApiOperation(value = "根据ID修改医院设置")
    @PutMapping("updateHospSet")
    public R update(
            @ApiParam(value = "医院设置对象",required = true)
            @RequestBody HospitalSet hospitalSet){
        boolean result = hospitalSetService.updateById(hospitalSet);
        if (result){
            return R.ok().message("修改成功");

        }else {
            return R.error().message("修改失败");
        }
    }
    @ApiOperation(value = "根据ID查询医院设置")
    @GetMapping("getHospitalSet/{id}")
    public R select(
            @ApiParam(value = "医院设置ID")
            @PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet!=null){
            return R.ok().data("hospitalSet",hospitalSet);
        }else {
            return R.error().message("查询失败");
        }
    }
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public R batchRemove(@ApiParam(value = "删除医院设置列表")
                         @RequestBody List<Long> idList){
        boolean result = hospitalSetService.removeByIds(idList);
        if (result){
            return R.ok().message("批量删除成功");
        }else {
            return R.error().message("批量删除失败");
        }

    }
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHosptalSet/{id}/{status}")
    public R lockHosptalSet(@ApiParam(value = "医院设置ID")
                            @PathVariable Long id,
                            @ApiParam(value = "医院设置状态")
                            @PathVariable Integer status){
        if (status!=0&&status!=1){
            return R.error().message("status为非法字符");
        }
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        boolean result = hospitalSetService.updateById(hospitalSet);
        if (result){
            return R.ok().message(status==1? "解锁成功":"锁定成功");
        }else {
            return R.error().message("操作失败");
        }
    }
}

