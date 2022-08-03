package com.atguigu.yygh.order.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.mapper.RefundInfoMapper;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
    @Override
    public void saveRefundInfo(PaymentInfo paymentInfo, Map<String, String> resultMap) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",paymentInfo.getOrderId()).eq("payment_type",paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);
        if (refundInfo!=null){
            return;
        }
       refundInfo = new RefundInfo();
        BeanUtils.copyProperties(paymentInfo,refundInfo);
        refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
        refundInfo.setCallbackTime(new Date());
        String jsonString = JSONObject.toJSONString(refundInfo);
        refundInfo.setCallbackContent(jsonString);
        baseMapper.insert(refundInfo);
    }
}
