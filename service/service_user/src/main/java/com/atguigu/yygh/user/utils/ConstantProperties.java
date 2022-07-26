package com.atguigu.yygh.user.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource("classpath:wx.properties") //读取配置文件
@ConfigurationProperties(prefix="wx.open") //读取节点
@Data //使用set方法将wx.ope节点中的值填充到当前类的属性中
public class ConstantProperties {

    private String appId;
    private String appSecret;
    private String redirectUri;
}