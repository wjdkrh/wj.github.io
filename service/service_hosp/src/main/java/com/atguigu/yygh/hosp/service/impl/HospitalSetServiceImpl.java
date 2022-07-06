package com.atguigu.yygh.hosp.service.impl;


import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 医院设置表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-04
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Override
    public Page<HospitalSet> seletPage(Long page, Long limit, HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> hospitalSetPage = new Page<>(page,limit);
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        if (hospitalSetQueryVo==null){
            baseMapper.selectPage(hospitalSetPage,null);


        }else {
            String hosname = hospitalSetQueryVo.getHosname();
            hospitalSetQueryWrapper.like(!StringUtils.isEmpty(hosname),"hosname",hosname);
            String hoscode = hospitalSetQueryVo.getHoscode();
            hospitalSetQueryWrapper.like(!StringUtils.isEmpty(hoscode),"hoscode",hoscode);
            baseMapper.selectPage(hospitalSetPage,hospitalSetQueryWrapper);
        }
        return hospitalSetPage;

    }
}
