package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void saveDepartment(Map<String, Object> map);

    Page showDepatmentPage(Map<String, String[]> parameterMap);

    void removeDepartment(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);
}
