package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DicrtListener * @Description TODO
 * @Author ehdk
 * @Date 19:45 2022/7/11
 * @Version 1.0
 **/
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DictListener extends AnalysisEventListener<DictEeVo> {
    private DictMapper dictMapper;
    private static final Integer size=3000;
    private List<Dict> dictList = new ArrayList<>();

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

 /*    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(dictEeVo));
        //调用方法添加数据库
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        log.info("开始存储数据库！");
        dictMapper.insert(dict);
        log.info("存储数据库成功！");
    }*/

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(dictEeVo));
        //调用方法添加数据库

        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictList.add(dict);
        if (dictList.size()>=size){
            saveData();
            dictList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！");
        saveData(); //!!!一定要注意如果不满足3000这个大小时，剩下的list集合数据最后执行 也要存入数据库
    }

    public void saveData(){
        dictMapper.saveBatch(dictList);
    }
}
