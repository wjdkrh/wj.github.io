package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void savaHospital(Map<String, Object> map);

    Hospital findByHoscode(String hoscode);

    Page<Hospital> selectPage(Integer page, Integer limit, String hosname);

    void updateStatus(String id, Integer status);

    Hospital show(String id);

    List<Hospital> findHospitalInfo(String keywords, String levelId, String areaId);

    Hospital getDetail(String hoscode);
}