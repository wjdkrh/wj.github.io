package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;

import java.util.Map;

public interface WxPayService {
    Map createNative(Long orderId, String remoteAddr);

    boolean queryPayStatus(Long orderId);

    void refund(PaymentInfo paymentInfo);

    void queryWxPay(OrderInfo orderInfo, Map<String, String> resultMap);

    boolean queryPayStatus2(Long orderId);
}
