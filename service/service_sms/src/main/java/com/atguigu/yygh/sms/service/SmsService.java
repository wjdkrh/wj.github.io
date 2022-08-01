package com.atguigu.yygh.sms.service;

import com.atguigu.yygh.vo.sms.SmsVo;

public interface SmsService   {
    void send(String phone, String code);

    void sendMsg(SmsVo smsVo);
}
