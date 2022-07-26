package com.atguigu.yygh.oss.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConstantProperties * @Description TODO
 * @Author ehdk
 * @Date 12:30 2022/7/25
 * @Version 1.0
 **/
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class ConstantProperties {
    private String endpoint;
    private String keyId;
    private String keySecret;
    private String bucketName;
}
