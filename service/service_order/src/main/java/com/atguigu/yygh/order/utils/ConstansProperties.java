package com.atguigu.yygh.order.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ConstansProperties * @Description TODO
 * @Author ehdk
 * @Date 12:35 2022/7/29
 * @Version 1.0
 **/
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
@Data
public class ConstansProperties {
    private String appId;
    private String partner;
    private String partnerKey;
    private String notifyUrl;
    private String cert;

}
