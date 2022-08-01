package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    DictFeignClient dictFeignClient;

    @Override
    public void savaHospital(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);
        Hospital updateHospital = hospitalRepository.findByHoscode(hospital.getHoscode());
        if (updateHospital!=null){
           hospital.setId(updateHospital.getId());
           hospital.setCreateTime(updateHospital.getCreateTime());
           hospital.setUpdateTime(updateHospital.getUpdateTime());
        }else {
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
        }
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital findByHoscode(String hoscode) {

        return  hospitalRepository.findByHoscode(hoscode);


    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, String hosname) {

        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).
                withIgnoreCase(true);
        Hospital hospital = new Hospital();
        hospital.setHosname(hosname);
        Example<Hospital> example = Example.of(hospital, matcher);
        Sort sort = Sort.by(Sort.Direction.DESC,"creatTime");
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
        Page<Hospital> pageModel = hospitalRepository.findAll(example, pageRequest);
        pageModel.getContent().forEach(h->{
            this.packHosp(h);
        });

        return  pageModel;

    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);


    }

    @Override
    public Hospital show(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        return this.packHosp(hospital);
    }

    @Override
    public List<Hospital> findHospitalInfo(String keywords, String levelId, String areaId) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("hosname", ExampleMatcher.GenericPropertyMatchers.contains())//模糊查询
                .withMatcher("hostype", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("districtCode", ExampleMatcher.GenericPropertyMatchers.exact());
        Hospital hospital = new Hospital();
        hospital.setHosname(keywords);
        hospital.setHostype(levelId);
        hospital.setDistrictCode(areaId);
        hospital.setStatus(1);
        Example<Hospital> example = Example.of(hospital, exampleMatcher);
        List<Hospital> list = hospitalRepository.findAll(example);
        list.forEach(this::packHospInfo);
        return list;
    }

    @Override
    public Hospital getDetail(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        return  this.packHosp(hospital);

    }

    private Hospital packHospInfo(Hospital h) {

            String name = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), h.getHostype());
            h.getParam().put("hostypeString",name);
            return h;
        }

    private Hospital packHosp(Hospital hospital){
          String dictCode = DictEnum.HOSTYPE.getDictCode();
          String name = dictFeignClient.getName(dictCode, hospital.getHostype());
          hospital.getParam().put("hostypeString",name);
          String provinceName = dictFeignClient.getName(null, hospital.getProvinceCode());
          String cityNmae = dictFeignClient.getName(null, hospital.getCityCode());
          String districtName = dictFeignClient.getName(null, hospital.getDistrictCode());
          hospital.getParam().put("fullAddress",provinceName+cityNmae+districtName);
          return hospital;

      }

}