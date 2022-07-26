package com.atguigu.yygh.cmn.service;


import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-11
 */
public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);

    List<DictEeVo> findExportData();

    void importDictData(MultipartFile file);

    Dict saveDictWithCacheManager(Dict dict);

    Dict getDictWithCacheManager(Long id);

    void deleteDictWithCacheManager(Long id);

    String getName(String parentDictCode, String value);

    List<Dict> findByParentDictCode(String parentDictCode);

    List<Dict> findChildDataByParentId(Long id);
}
