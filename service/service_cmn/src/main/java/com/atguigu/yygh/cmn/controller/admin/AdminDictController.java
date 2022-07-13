package com.atguigu.yygh.cmn.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-07-11
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class AdminDictController {

    @Autowired
    private DictService dictService;

    @Autowired
    private RedisTemplate redisTemplate;



    @ApiOperation(value = "测试redis")
    @PostMapping("testRedis")
    public R test(){
        redisTemplate.opsForValue().set("a", "hello");
        redisTemplate.opsForValue().set("dict", new Dict());
        return R.ok();
    }



    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id) {
        List<Dict>  list = dictService.findChildData(id);
        return R.ok().data("list",list);
    }

    @ApiOperation(value="导出")
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response) {
        try {
            List<DictEeVo> dictVoList = dictService.findExportData();

            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);

        } catch (IOException e) {
            throw new YyghException("数据导出错误", ResultCode.ERROR_EXPORT, e);
        }
    }
    @ApiOperation(value = "导入")
    @PostMapping("importData")
    public R importData(MultipartFile file){
       dictService.importDictData(file);
        return R.ok().message("数据导入成功");
    }

    @ApiOperation(value = "测试CacheManager保存数据")
    @PostMapping("testCacheManagerSave")
    public R testCacheManagerSave(){
        Dict dict = new Dict();
        dict.setId(999L);
        dict.setName("test");
        dictService.saveDictWithCacheManager(dict);
        return R.ok();
    }
    @ApiOperation(value = "测试CacheManager查找数据")
    @GetMapping("testCacheManagerGet/{id}")
    public R testCacheManagerGet(@PathVariable Long id){
        Dict dict = dictService.getDictWithCacheManager(id);
        return R.ok().data("dict", dict);
    }
    @ApiOperation(value = "测试CacheManager删除数据")
    @DeleteMapping("testCacheManagerDelete/{id}")
    public R testCacheManagerDelete(@PathVariable Long id){

        dictService.deleteDictWithCacheManager(id);
        return R.ok();
    }
}

