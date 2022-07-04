package com.atguigu.yygh.model.hosp;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * HospitalSet
 * </p>
 *
 * @author qy
 */
@Data
@ApiModel(description = "医院设置")
@TableName("hospital_set")
public class HospitalSet extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "医院名称", example = "北京协和医院")
	@TableField("hosname")
	private String hosname;

	@ApiModelProperty(value = "医院编号", example = "10000")
	@TableField("hoscode")
	private String hoscode;

	@ApiModelProperty(value = "api基础路径", example = "http://example.com")
	@TableField("api_url")
	private String apiUrl;

	@ApiModelProperty(value = "签名秘钥")
	@TableField("sign_key")
	private String signKey;

	@ApiModelProperty(value = "联系人姓名", example = "小谷")
	@TableField("contacts_name")
	private String contactsName;

	@ApiModelProperty(value = "联系人手机", example = "999")
	@TableField("contacts_phone")
	private String contactsPhone;

	@ApiModelProperty(value = "状态")
	@TableField("status")
	private Integer status; //1 可用  0 锁定

}

