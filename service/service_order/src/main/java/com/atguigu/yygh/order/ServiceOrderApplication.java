package com.atguigu.yygh.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName ServiceOrderApplication * @Description TODO
 * @Author ehdk
 * @Date 11:04 2022/7/27
 * @Version 1.0
 **/
@SpringBootApplication
@EnableFeignClients
@ComponentScan("com.atguigu.yygh")
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class);
    }
}
