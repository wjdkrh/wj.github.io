package com.atguigu.yygh.order.service;


import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long submitOrder(String scheduleId, Long patientId);

    OrderInfo getOrderInfo(Long orderId);

    List<OrderInfo> selectList(Long userId);

    void updateOrderInfoStatus(Long orderInfoId,Integer orderStatus);

    void cancelOrder(Long orderId);

    void remindPatient();

    void updateOrderInfo(OrderInfo orderInfo, Map<String, String> resultMap);

    boolean queryOrderStatus(Long orderId);

    OrderInfo getOrderInfoByoutTradeNo(String outTradeNo);
}
