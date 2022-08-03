package com.atguigu.yygh.order.client;

import com.atguigu.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-user")
public interface UserFeignClient {

    @GetMapping("/inner/user/patient/get/{patientId}")
     Patient getPatient(@PathVariable Long patientId);
}
