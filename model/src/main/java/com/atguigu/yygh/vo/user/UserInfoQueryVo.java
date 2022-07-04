package com.atguigu.yygh.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="会员搜索对象")
public class UserInfoQueryVo {

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "创建时间开始")
    private String createTimeBegin;

    @ApiModelProperty(value = "创建时间结束")
    private String createTimeEnd;

}
