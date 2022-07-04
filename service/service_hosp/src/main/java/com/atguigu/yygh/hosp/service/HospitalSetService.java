package com.atguigu.yygh.hosp.service;


import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-04
 */
public interface HospitalSetService extends IService<HospitalSet> {

    Page<HospitalSet> seletPage(Long page, Long limit, HospitalSetQueryVo hospitalSetQueryVo);
}
