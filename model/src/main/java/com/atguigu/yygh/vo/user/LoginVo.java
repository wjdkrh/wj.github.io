package com.atguigu.yygh.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="登录对象")
public class LoginVo {

    @ApiModelProperty(value = "openid")
    private String openid; //微信唯一标识

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    private String code;
}
