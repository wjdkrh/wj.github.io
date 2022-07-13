package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-11
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    RedisTemplate redisTemplate;

   /* @Override
    public List<Dict> findChildData(Long id) {

        List<Dict> dictList =null;
        try{
            dictList =(List<Dict>) redisTemplate.opsForValue().get("dictList:" + id);
            if (dictList!=null){
                return dictList;
            }
        }catch (Exception e){
            log.error("redis服务器异常：get dictList");
        }
        QueryWrapper<Dict> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        dictList = baseMapper.selectList(queryWrapper);
        for (Dict dict : dictList) {
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        }
        try {
            redisTemplate.opsForValue().set("dictList:" + id, dictList, 5, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("redis服务器异常：get dictList");
        }
        return dictList;
    }*/
    @Override
    @Cacheable(value = "dictList",key="#id",unless = "#result==null")
    public List<Dict> findChildData(Long id) {
        List<Dict> dictList =null;
        QueryWrapper<Dict> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        dictList = baseMapper.selectList(queryWrapper);
        for (Dict dict : dictList) {
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        }
        return dictList;
    }

    @Override
    public List<DictEeVo> findExportData() {
        List<Dict> dictList = baseMapper.selectList(null);
        ArrayList<DictEeVo> dictEeVoArrayList = new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoArrayList.add(dictEeVo);
        }
        return dictEeVoArrayList;
    }

    @Override
    public void importDictData(MultipartFile file) {
        try {
            long b = System.currentTimeMillis();
            EasyExcel.read(file.getInputStream(),DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
            long e = System.currentTimeMillis();
            log.info("完成时间是:{}",e-b);
        } catch (IOException e) {
            throw new YyghException("文件导入失败", ResultCode.ERROR_IMPORT,e);
        }
    }

    @Override
    @CachePut(value = "dictTest",key="#dict.id")  /// 在redis中 key的值为dictTest::999 value 是 dict类
    public Dict saveDictWithCacheManager(Dict dict) {
        baseMapper.insert(dict);
        return dict;
    }

    @Override
    @Cacheable(value = "dictTest", key = "#id", unless="#result == null") //
    public Dict getDictWithCacheManager(Long id) {
        Dict dict = baseMapper.selectById(id);
        return dict;
    }

    @Override
    @CacheEvict(value = "dictTest",key="#id")
    public void deleteDictWithCacheManager(Long id) {
        baseMapper.deleteById(id);
    }
  /*  @CachePut
    使用该注解的方法，每次都会执行，并将结果存在指定的缓存中，其他方法可以直接从响应的缓存中读取数据，而不需要再去查询数据，一般用在新增方法上。
    value	缓存名，必填，制定了缓存放在那块命名空间
    cacheNames	与value差不多，二选一即可
    keyGenerator	指定key生成的策略，和key只能使用一个
    key	缓存的key，默认为空，可以使用#参数 将参数作为缓存的key值，和keyGenerator只能使用一个*/

    private boolean hasChildren(Long id){
        QueryWrapper<Dict> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count>0;
    }
}
