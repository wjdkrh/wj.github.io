package com.atguigu.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.atguigu.yygh"))
                //只显示admin路径下的页面
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档描述了后台管理系统微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "123@qq.com"))
                .build();
    }

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-API文档")
                .description("本文档描述了网站微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "123@qq.com"))
                .build();
    }
    @Bean
    public Docket frontApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("frontApi")
                .apiInfo(frontApiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/front/.*")))
                .build();
    }

    private ApiInfo frontApiInfo(){
        return new ApiInfoBuilder()
                .title("前端页面-API文档")
                .description("本文档描述了前端页面展示服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "123@qq.com"))
                .build();
    }

    @Bean
    public Docket QRCApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("QRCApi")
                .apiInfo(QRCApiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo QRCApiInfo(){
        return new ApiInfoBuilder()
                .title("微信扫码登录功能")
                .description("微信扫码登录")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "123@qq.com"))
                .build();
    }
    @Bean
    public Docket OssApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("OssApi")
                .apiInfo(OssApiInfo())
                .select()
                //只显示api路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.atguigu.yygh.oss"))
                .build();
    }

    private ApiInfo OssApiInfo(){
        return new ApiInfoBuilder()
                .title("阿里云文件上传")
                .description("文件上传")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "123@qq.com"))
                .build();
    }
}