package com.atguigu.yygh.cmn.mapper;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 组织架构表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2022-07-11
 */
@Repository
public interface DictMapper extends BaseMapper<Dict> {

    void saveBatch(@Param("list") List<Dict> dictList);
}
