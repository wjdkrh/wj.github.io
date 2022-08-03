package com.atguigu.yygh.order.service;


import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
public interface RefundInfoService extends IService<RefundInfo> {

    void saveRefundInfo(PaymentInfo paymentInfo, Map<String, String> resultMap);
}
