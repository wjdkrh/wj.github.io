package com.atguigu.yygh.cmn.controller.inner;

import com.atguigu.yygh.cmn.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName InnerDictController * @Description TODO
 * @Author ehdk
 * @Date 18:55 2022/7/15
 * @Version 1.0
 **/
@RestController
@Api("数据字典查询分类接口")
@RequestMapping("/inner/cmn/dict")
public class InnerDictController {
    @Autowired
    private DictService dictService;

    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "getName")
    public String getName(
            String parentDictCode,
            String value) {

        return dictService.getName(parentDictCode, value);
    }


}
