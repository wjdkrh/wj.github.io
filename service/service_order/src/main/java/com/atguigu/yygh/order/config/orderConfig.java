package com.atguigu.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName orderConfig * @Description TODO
 * @Author ehdk
 * @Date 11:05 2022/7/27
 * @Version 1.0
 **/
@Configuration
@MapperScan("com.atguigu.yygh.order.mapper")
@EnableTransactionManagement
public class orderConfig {
}
