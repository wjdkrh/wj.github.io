package com.atguigu.yygh.order.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.client.HospitalFeignClient;
import com.atguigu.yygh.order.client.UserFeignClient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.*;
import com.atguigu.yygh.order.utils.ConstansProperties;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.rabbit.service.RabbitService;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    HospitalFeignClient hospitalFeignClient;
    @Autowired
    ConstansProperties constansProperties;
    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    RefundInfoService refundInfoService;
    @Autowired
    WxPayService wxPayService;
    @Autowired
    RabbitService rabbitService;
    @Override
    public Long submitOrder(String scheduleId, Long patientId) {
        OrderInfo orderInfo = new OrderInfo();
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        orderInfo.setScheduleId(scheduleId);
        //组装就诊人数据
        Patient patient = userFeignClient.getPatient(patientId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patient.getId());
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());

        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);

        //获取医院系统url和密钥
        HospitalSet hospitalSet = hospitalFeignClient.getHospitalSet(scheduleOrderVo.getHoscode());
        String apiUrl = hospitalSet.getApiUrl();
        String signKey = hospitalSet.getSignKey();

        //给医院系统发送预约挂号信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleOrderVo.getHoscode()); //医院编号
        paramMap.put("depcode", scheduleOrderVo.getDepcode()); //科室编号
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId()); //排班编号（医院自己的排班主键）
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd")); //安排日期
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime()); //安排时间
        paramMap.put("amount", scheduleOrderVo.getAmount()); //挂号费
        paramMap.put("name", patient.getName()); //就诊人姓名
        paramMap.put("certificatesType", patient.getCertificatesType()); //就诊人证件类型
        paramMap.put("certificatesNo", patient.getCertificatesNo()); //就诊人证件号
        paramMap.put("birthdate", patient.getBirthdate()); //就诊人出生年月
        paramMap.put("phone", patient.getPhone()); //就诊人手机
        paramMap.put("provinceCode", patient.getProvinceCode()); //省/直辖市的code
        paramMap.put("cityCode", patient.getCityCode()); //市code
        paramMap.put("districtCode", patient.getDistrictCode()); //区code
        paramMap.put("address", patient.getAddress()); //就诊人详情地址
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp()); //时间戳
        //发送医院端时加上签名
        String sign = HttpRequestHelper.getSign(paramMap, signKey);
        paramMap.put("sign", sign);
        //像医院系统远程接口，发送请求
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, apiUrl + "/order/submitOrder");
        //得到返回数据
        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
        //预约记录唯一标识（医院预约记录主键）
        String hosRecordId = jsonObject1.getString("hosRecordId");
        //预约序号
        Integer reservedNumber = jsonObject1.getInteger("reservedNumber");
        Integer availableNumber = jsonObject1.getInteger("availableNumber");
        Integer number = jsonObject1.getInteger("number");
        //取号时间
        String fetchTime = jsonObject1.getString("fetchTime");
        //取号地址
        String fetchAddress = jsonObject1.getString("fetchAddress");
        orderInfo.setHosRecordId(hosRecordId);
        orderInfo.setNumber(number);
        orderInfo.setFetchTime(fetchTime);
        orderInfo.setFetchAddress(fetchAddress);
        baseMapper.insert(orderInfo);
        //异步发送消息给rbmq
        //同步医院端预约数
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setAvailableNumber(availableNumber);
        orderMqVo.setReservedNumber(reservedNumber);
        orderMqVo.setScheduleId(scheduleId);
        rabbitService.sendOrderMqVo(orderMqVo);

        //异步发送短信给SMS微服务
        SmsVo smsVo = new SmsVo();
        smsVo.setPhone(patient.getPhone());
        smsVo.setTemplateCode(patient.getName()+"您已预约成功,"+scheduleOrderVo.getHosname()+" "+scheduleOrderVo.getDepname()+".预约时间:"+scheduleOrderVo.getReserveTime()+",取号时间:"+fetchTime+",取号地址:"+fetchAddress);
        Map<String, Object> map = new HashMap<>();
        map.put("patientName",patient.getName());
        map.put("hosname",scheduleOrderVo.getHosname());
        map.put("depname",scheduleOrderVo.getDepname());
        map.put("reserveTime",scheduleOrderVo.getReserveTime());
        map.put("fetchTime",fetchTime);
        map.put("fetchAddress",fetchAddress);
        smsVo.setParam(map);
        rabbitService.sendSmsVo(smsVo);

        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectOne(new QueryWrapper<OrderInfo>().eq("id", orderId));

        return this.packageOrderInfo(orderInfo);
    }

    @Override
    public List<OrderInfo> selectList(Long userId) {
        List<OrderInfo> orderInfoList = baseMapper.selectList(new QueryWrapper<OrderInfo>().eq("user_id", userId));
        orderInfoList.stream().forEach(orderInfo -> {
            this.packageOrderInfo(orderInfo);
        });
        return orderInfoList;
    }

    private OrderInfo packageOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

    @Override
    public void updateOrderInfoStatus(Long orderInfoId, Integer orderStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderInfoId);
        orderInfo.setOrderStatus(orderStatus);
        baseMapper.updateById(orderInfo);

    }

    @Override
    public void cancelOrder(Long orderId) {
        //取消订单分为两种情况，第一种情况是， 没有支付 ，二是已支付取消。通过两种情况来做判断
        if(orderId==null){
            log.error("取消订单失败，orderId非法参数");
            throw new YyghException("取消订单失败，orderId非法参数", ResultCode.ERROR);
        }

        OrderInfo orderInfo = baseMapper.selectOne(new QueryWrapper<OrderInfo>().eq("id", orderId));
        //从订单中取出退款时间，如果现在时间过了，那么就不允许退款
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()){
            throw new YyghException("时间已到期，不允许取消",ResultCode.ERROR);
        }
        PaymentInfo paymentInfo = paymentInfoService.selectPaymentInfoByOrderId(orderId, PaymentTypeEnum.WEIXIN.getStatus());
        //如果用户直接取消，还没有点微信扫码就生产不了支付信息，直接return
        if (paymentInfo!=null) {
            //前端那边多次请求退款，会造成失败，这里先查询一下支付状态，如果已退款，则直接返回即可；
            if (paymentInfo.getPaymentStatus().intValue() == PaymentStatusEnum.REFUND.getStatus().intValue()) {
                return;
            }
            //如果订单状态是已支付，那就是要进行退款了
            if (paymentInfo.getPaymentStatus().intValue() == PaymentStatusEnum.PAID.getStatus().intValue()) {
                //微信退款
                wxPayService.refund(paymentInfo);
            }
            //如果是支付中取消，要设置已取消支付
            if (paymentInfo.getPaymentStatus().intValue()==PaymentStatusEnum.UNPAID.getStatus().intValue()) {
                paymentInfoService.updatePaymentInfoStatus(paymentInfo.getId(), PaymentStatusEnum.CANCLE.getStatus());
            }
            paymentInfoService.updatePaymentInfoStatus(paymentInfo.getId(),PaymentStatusEnum.REFUND.getStatus());
        }

        //无论是支付中取消还是已支付退款 都需要修改一下订单得状态为取消预约
        this.updateOrderInfoStatus(orderId,OrderStatusEnum.CANCLE.getStatus());

        //提交订单时，orderinfoservice那边调用医院端扣减过库存，这边由于是退款同样告诉医院端要把预约得库存加回去
        HospitalSet hospitalSet = hospitalFeignClient.getHospitalSet(orderInfo.getHoscode());
        String signKey = hospitalSet.getSignKey();
        //  /order/updateCancelStatus医院端接口uri
        String apiUrl = hospitalSet.getApiUrl();
        Map<String, Object> params= new HashMap<>();
        params.put("hoscode",orderInfo.getHoscode());
        params.put("hosRecordId",orderInfo.getHosRecordId());
        params.put("hosScheduleId",orderInfo.getHosScheduleId());
        params.put("timestamp",HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(params, signKey);
        params.put("sign",sign);
        JSONObject jsonObject = HttpRequestHelper.sendRequest(params, apiUrl + "/order/updateCancelStatus");
        if (!"200".equals(jsonObject.getString("code"))) {
            log.error("医院端同步订单信息失败,医院端返回消息："+jsonObject.getString("message"));
            throw new YyghException("医院端同步订单信息失败",ResultCode.ERROR);
        }
        //JSONObject里面有一个属性实际就是一个map
        JSONObject data = jsonObject.getJSONObject("data");
        Integer reservedNumber = data.getInteger("reservedNumber");
        Integer availableNumber = data.getInteger("availableNumber");
        //从医院接口返回了总预约数和可预约数。我们可以发送rabbitMQ去异步消息
        //异步发送消息给rbmq
        //同步医院端预约数
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setAvailableNumber(availableNumber);
        orderMqVo.setReservedNumber(reservedNumber);
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        rabbitService.sendOrderMqVo(orderMqVo);
        //异步发送短信给SMS微服务
    }

    /**
     *  定时发短信任务
     */
    @Override
    public void remindPatient() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);
        orderInfoList.stream().forEach(orderInfo -> {
            SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<>();
            param.put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
            param.put("reserveDate", reserveDate);
            param.put("name", orderInfo.getPatientName());
            smsVo.setTemplateCode("模板coder");
            smsVo.setParam(param);
            rabbitService.sendSmsVo(smsVo);
        });
    }

    @Override
    public void updateOrderInfo( OrderInfo orderInfo, Map<String, String> resultMap) {
        if (orderInfo.getOrderStatus().intValue()==OrderStatusEnum.CANCLE.getStatus().intValue()||orderInfo.getOrderStatus().intValue()==OrderStatusEnum.GET_NUMBER.getStatus().intValue()){
            log.error("订单状态查询异常");
            throw new YyghException("订单状态查询异常",ResultCode.ERROR);
        }
        //如果订单显示已支付则直接返回
        if (orderInfo.getOrderStatus().intValue()==OrderStatusEnum.PAID.getStatus().intValue()){
            return;
        }
        if (orderInfo.getOrderStatus().intValue()==OrderStatusEnum.UNPAID.getStatus().intValue()) {
            //修改订单状态为已支付
            OrderInfo orderInfo1 = new OrderInfo();
            orderInfo1.setId(orderInfo.getId());
            orderInfo1.setOrderStatus(OrderStatusEnum.PAID.getStatus());
            baseMapper.updateById(orderInfo1);
            //保存支付信息
            paymentInfoService.savePaymentInfo(resultMap,orderInfo);
            }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", orderInfo.getHoscode()); //医院编号
        paramMap.put("hosRecordId", orderInfo.getHosRecordId()); //医院端订单编号
        System.out.println("hosRecordId" + orderInfo.getHosRecordId());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp()); //时间戳
        //发送医院端时加上签名
        HospitalSet hospitalSet = hospitalFeignClient.getHospitalSet(orderInfo.getHoscode());
        String sign = HttpRequestHelper.getSign(paramMap, hospitalSet.getSignKey());
        paramMap.put("sign", sign);
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, hospitalSet.getApiUrl()+"/order/updatePayStatus");
        if(jsonObject.getInteger("code")!=200){
            log.error("更新医院订单状态失败");
            throw new YyghException("更新医院订单状态失败",ResultCode.ERROR);
        }
    }

    @Override
    public boolean queryOrderStatus(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectOne(new QueryWrapper<OrderInfo>().eq("id", orderId));
        if (orderInfo.getOrderStatus().intValue()== OrderStatusEnum.PAID.getStatus().intValue()) {
            return true;
        }

        return false;
    }

    @Override
    public OrderInfo getOrderInfoByoutTradeNo(String outTradeNo) {
      return   baseMapper.selectOne(new QueryWrapper<OrderInfo>().eq("out_trade_no",outTradeNo));
    }
}
