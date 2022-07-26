package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DepartmentServiceImpl * @Description TODO
 * @Author ehdk
 * @Date 18:44 2022/7/13
 * @Version 1.0
 **/
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;


    @Override
    public void saveDepartment(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        Department existDepartment = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (existDepartment!=null){
            department.setId(existDepartment.getId());
            department.setCreateTime(existDepartment.getCreateTime());
            department.setUpdateTime(new Date());
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
        }
        departmentRepository.save(department);
    }

    @Override
    public Page showDepatmentPage(Map<String, String[]> parameterMap) {
        String hoscode = parameterMap.get("hoscode")[0];
        Integer page = Integer.parseInt(parameterMap.get("page")[0]);
        Integer limit = Integer.parseInt(parameterMap.get("limit")[0]);
        Pageable pageable =  PageRequest.of(page,limit);
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void removeDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        departmentRepository.delete(department);
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> departmentList = departmentRepository.findAll(example);
        Map<String, List<Department>> departmentsMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigname));
        ArrayList<DepartmentVo> result = new ArrayList<>();
        for (Map.Entry<String, List<Department>> entry : departmentsMap.entrySet()) {
            DepartmentVo departmentVo = new DepartmentVo();
            List<Department> departments = entry.getValue();
            departmentVo.setDepcode(departments.get(0).getDepcode());
            departmentVo.setDepname(entry.getKey());
            ArrayList<DepartmentVo> children = new ArrayList<>();
            departments.forEach( d->{
                DepartmentVo subDepartment = new DepartmentVo();
                subDepartment.setDepcode(d.getDepcode());
                subDepartment.setDepname(d.getDepname());
                children.add(subDepartment);
            });
            departmentVo.setChildren(children);
           result.add(departmentVo);
        }
        return result;
    }


}
