package com.atguigu.yygh.sms.controller.front;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Api(tags = "短信接口")
@RestController
@RequestMapping("/front/sms")
public class FrontSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("发送短信")
    @PostMapping("/send/{phone}")
    public R code(@PathVariable String phone) {
        //生成验证码
        try {
            String code = RandomUtil.getFourBitRandom();
            //发送短信
            //smsService.send(phone, code);

            //验证码存入redis，并设置有效时间
            redisTemplate.opsForValue().set("code:"+phone,code,5,TimeUnit.MINUTES);

            return R.ok().message("短信发送成功");
        } catch (Exception e) {
            throw new YyghException("短信发送失败", ResultCode.ERROR,e);
        }
    }
}