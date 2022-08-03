package com.atguigu.yygh.order.service;


import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付信息表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
        void savePaymentInfo(Map<String,String> payResult, OrderInfo orderInfo);

    PaymentInfo selectPaymentInfoByOrderId(Long orderId,Integer type);

    void updatePaymentInfoStatus(Long id,Integer status);
}
