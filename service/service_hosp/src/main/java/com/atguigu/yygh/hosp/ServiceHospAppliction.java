package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName ServiceHospAppliction * @Description TODO
 * @Author ehdk
 * @Date 19:34 2022/7/4
 * @Version 1.0
 **/
@SpringBootApplication
@ComponentScan("com.atguigu.yygh")
public class ServiceHospAppliction {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospAppliction.class,args);
    }
}
