package com.atguigu.yygh.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName HospConfig * @Description TODO
 * @Author ehdk
 * @Date 19:21 2022/7/4
 * @Version 1.0
 **/
@SpringBootConfiguration
@EnableTransactionManagement
@MapperScan("com.atguigu.yygh.hosp.mapper")
public class HospConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
