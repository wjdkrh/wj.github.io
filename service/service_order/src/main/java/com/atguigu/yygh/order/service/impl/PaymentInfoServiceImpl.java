package com.atguigu.yygh.order.service.impl;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.mapper.PaymentInfoMapper;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Override
    public void savePaymentInfo(Map<String, String> payResult, OrderInfo orderInfo) {
        //由于前端是定时器任务轮训查看订单状态。微信那里返回结果可能会有延迟，导致信息不同步。有可能支付信息
        //已经保存好了，导致后端这边重复保存支付信息导致重复订单信息。这里需要通过订单号查询下数据库是否有重复的支付信息

        try {
            Integer paymentInfoCount = baseMapper.selectCount(new QueryWrapper<PaymentInfo>().eq("order_id", orderInfo.getId()).eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus()));
            //如果是大于0说明是重复询问的前端重复询问订单状态的信息，且已经保存在数据库了。这里直接return就可以。
            if (paymentInfoCount>0){
                return;
            }
            PaymentInfo paymentInfo = new PaymentInfo();
            String outTradeNo = payResult.get("out_trade_no");
            String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                    +"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();

            paymentInfo.setSubject(subject);
            paymentInfo.setOutTradeNo(outTradeNo);
            paymentInfo.setOrderId(orderInfo.getId());
            paymentInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
            paymentInfo.setTradeNo(payResult.get("transaction_id"));
            paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
            paymentInfo.setTotalAmount(orderInfo.getAmount());
            paymentInfo.setCallbackTime(new DateTime().toDate());
            paymentInfo.setCallbackContent(payResult.toString());

            baseMapper.insert(paymentInfo);
        } catch (Exception e) {
            throw new YyghException("支付信息保存失败", ResultCode.ERROR,e);
        }
    }

    @Override
    public PaymentInfo selectPaymentInfoByOrderId(Long orderId,Integer type) {
     return   baseMapper.selectOne(new QueryWrapper<PaymentInfo>().eq("order_id",orderId).eq("payment_type",type));
    }

    @Override
    public void updatePaymentInfoStatus(Long id, Integer status) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setId(id);
        paymentInfo.setPaymentStatus(status);
        baseMapper.updateById(paymentInfo);
    }
}
