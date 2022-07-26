package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository //非必要：避免idea报告无法注入的错误
public interface HospitalRepository extends MongoRepository<Hospital,String> {

       Hospital findByHoscode(String hoscode);
}