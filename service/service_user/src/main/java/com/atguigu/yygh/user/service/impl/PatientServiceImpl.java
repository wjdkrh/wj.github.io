package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-19
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    DictFeignClient dictFeignClient;

    @Override
    public Patient getPatientInfoById(Long id) {
        Patient patient = baseMapper.selectById(id);
        String provinceCode = patient.getProvinceCode();
        String cityCode = patient.getCityCode();
        String districtCode = patient.getDistrictCode();
        String certificatesName = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        String provinceName = dictFeignClient.getName(null, provinceCode);
        String cityName = dictFeignClient.getName(null, cityCode);
        String districtName = dictFeignClient.getName(null, districtCode);
        Map<String, Object> param = patient.getParam();
        param.put("certificatesTypeString", certificatesName);
        param.put("provinceString", provinceName);
        param.put("cityString", cityName);
        param.put("districtString", districtName);
        param.put("fullAddress", provinceName + cityName + districtName + patient.getAddress());
        patient.setParam(param);

        return patient;
    }

    @Override
    public List<Patient> findAllByUserId(Long userId) {
        List<Patient> patientList = baseMapper.selectList(new QueryWrapper<Patient>().eq("user_id", userId));
        patientList.stream().forEach(patient->{
            Patient patient1 = this.getPatientInfoById(patient.getId());
            patient.setParam(patient1.getParam());
        });
        return patientList;
    }
}
