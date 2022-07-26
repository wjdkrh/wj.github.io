package com.atguigu.yygh.cmn.controller.front;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/front/cmn/dict")
public class FrontDictController {

    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "findByDictCode/{parentDictCode}")
    public R findByDictCode(@PathVariable String parentDictCode) {
        List<Dict> list = dictService.findByParentDictCode(parentDictCode);
        return R.ok().data("list",list);
    }

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChildDataByParentId(id);
        return R.ok().data("list",list);
    }
}