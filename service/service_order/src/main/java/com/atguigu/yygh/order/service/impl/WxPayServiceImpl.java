package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.teaopenapi.models.Params;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.client.HospitalFeignClient;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WxPayService;
import com.atguigu.yygh.order.utils.ConstansProperties;
import com.atguigu.yygh.order.utils.HttpClientUtils;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.order.utils.HttpUtil;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerTypePredicate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WxPayServiceImpl * @Description TODO
 * @Author ehdk
 * @Date 12:39 2022/7/29
 * @Version 1.0
 **/
    @Service
    @Slf4j
    public class WxPayServiceImpl implements WxPayService {
        @Autowired
        ConstansProperties constansProperties;

        @Autowired
        OrderInfoService orderInfoService;

        @Autowired
        PaymentInfoService paymentInfoService;
        @Autowired
        HospitalFeignClient hospitalFeignClient;
        @Autowired
         RefundInfoService refundInfoService;

        private String signKey ="8af52af00baf6aec434109fc17164aae";


        public Map createNative(Long orderId, String remoteAddr) {
            Map<String, Object> map = null;
            try {
                //微信支付系统接口。微信那里通过我们传入的参数生成预支付交易
                String  url="https://api.mch.weixin.qq.com/pay/unifiedorder";
                //TODO 注意这里得泛型都是字符串！！ 传入得参数都是字符串类型
                //根据API列表文档 组装参数了。
                Map<String, String> params = new HashMap<>();
                String appId = constansProperties.getAppId();
                String mchId = constansProperties.getPartner();
                String notifyUrl = constansProperties.getNotifyUrl();
                String partnerKey = constansProperties.getPartnerKey();
                //微信支付分配的公众账号ID（企业号corpid即为此appid）
                params.put("appid",appId);
                //微信支付分配的商户号
                params.put("mch_id",mchId);
                //随机字符串，长度要求在32位以内。通过工具类生产得
                String nonceStr = WXPayUtil.generateNonceStr();
                params.put(	"nonce_str",nonceStr);
                //body商品描述，用户在微信立即支付那里显示支付哪类商品得描述信息；
                OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
                String hosname = orderInfo.getHosname();
                String depname = orderInfo.getDepname();
                Date reserveDate = orderInfo.getReserveDate();
                String body=hosname+depname+"于"+new DateTime(reserveDate).toString("yyyy/MM/dd") +"就诊";
                params.put("body",body);
                //out_trade_no	订单号
                String outTradeNo = orderInfo.getOutTradeNo();
                params.put("out_trade_no",outTradeNo);
                //total_fee订单金额 这里得金额微信文档虽然写的是int但是类型是string;
                //微信那里金额是以分为单位，如果我们是元得话需要 multiply(new BigDecimal(100))乘以100从分换算成元
                //BigDecimal amount = orderInfo.getAmount();
                params.put("total_fee","1");
                //spbill_create_ip 支持IPV4和IPV6两种格式的IP地址。用户的客户端IP
                params.put("spbill_create_ip",remoteAddr);
                //通知地址
                params.put("notify_url",notifyUrl);
                //交易类型：trade_type
                params.put("trade_type","NATIVE");
                //开始生产sign签名参数 WXPayUtil也是根据微信安全规范中得方式生成签名 默认使用得MD5
                /*String sign = WXPayUtil.generateSignature(params, partnerKey);
                params.put("sign",sign);
                String xml = WXPayUtil.mapToXml(params);*/

                //这种方式是上面的三个步骤汇总，内部已经把sign放进参数里了 默认使用得MD5
                String xml= WXPayUtil.generateSignedXml(params, partnerKey);

                //微信接口那边只接收xml类型格式得参数 将参数放入请求对象中，设置https和post形式发送 按照微信文档
                HttpClientUtils httpClient = new HttpClientUtils(url);
                httpClient.setHttps(true);
                httpClient.setXmlParam(xml);
                httpClient.post();
                //得到响应结果也是xml格式
                String resp = httpClient.getContent();
                Map<String, String> result = WXPayUtil.xmlToMap(resp);
                if ("FAIL".equals(result.get("return_code"))||"FAIL".equals(result.get("result_code"))){
                   log.error("获取微信支付二维码失败"+result.get("return_msg"));
                    throw  new YyghException("获取微信支付二维码失败:"+result.get("return_msg"), ResultCode.ERROR);
                }
                String codeUrl = result.get("code_url");
                //组装需要的内容
                map = new HashMap<>();
                map.put("orderId", orderId);
                map.put("totalFee", orderInfo.getAmount());
                map.put("resultCode", result.get("result_code"));
                map.put("codeUrl", result.get("code_url"));
            } catch (Exception e) {
                throw  new YyghException("获取微信支付二维码失败", ResultCode.ERROR,e);
            }
            return map;

        }
        /**
         * 查询订单交易情况
         * @param orderId
         * @return
         */
        @Override
        public boolean queryPayStatus(Long orderId) {
            try {
                String url ="https://api.mch.weixin.qq.com/pay/orderquery";
                //请求参数组装
                Map<String, String> params = new HashMap<>();
                params.put("appid",constansProperties.getAppId());
                params.put("mch_id",constansProperties.getPartner());
                OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
                params.put("out_trade_no",orderInfo.getOutTradeNo());
                params.put("nonce_str",WXPayUtil.generateNonceStr());
                String xml = WXPayUtil.generateSignedXml(params, constansProperties.getPartnerKey());
                //调用腾讯端查询接口，参数值已经封装好了在xml里
                HttpClientUtils httpClient = new HttpClientUtils(url);
                httpClient.setXmlParam(xml);
                httpClient.setHttps(true);
                httpClient.post();
                //返回结果也同样是xml形式的字符串需要转换
                String resp = httpClient.getContent();
                Map<String, String> resultMap = WXPayUtil.xmlToMap(resp);
                //通过结果做判断
                if("FAIL".equals(resultMap.get("return_code")) ||"FAIL".equals(resultMap.get("result_code"))){
                    log.error("查询订单状态失败："+resultMap.get("return_msg"));
                    log.error("查询订单状态失败："+",错误代码: "+resultMap.get("err_code")+",错误代码描述: "+resultMap.get("err_code_des"));
                    throw new YyghException("查询订单状态失败",ResultCode.ERROR);
                }
                //如果前面两个结果都是success，那么判断trade_state如果是成功状态返回true否则返回flase
                if ("SUCCESS".equals(resultMap.get("trade_state"))){
                    //走了这一步说明订单查询成功，且是支付成功状态，那么需要修改订单状态为已支付，并且保存付款的订单信息；
                    //修改订单信息为已支付
                    Integer status = OrderStatusEnum.PAID.getStatus();
                    orderInfoService.updateOrderInfoStatus(orderId,status);
                    //保存支付信息
                    paymentInfoService.savePaymentInfo(resultMap,orderInfo);
                    //调用医院端接口，同步订单状态
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

                    return true;
                }
            } catch (Exception e) {
                throw new YyghException("查询订单状态失败",ResultCode.ERROR,e);
            }

            return false;
        }

    @Override
    public void refund(PaymentInfo paymentInfo) {

        try {
        /*  String url="https://api.mch.weixin.qq.com/secapi/pay/refund";
            Map<String, String> params = new HashMap<>();
            params.put("appid",constansProperties.getAppId());
            params.put("mch_id",constansProperties.getPartner());
            params.put("nonce_str",WXPayUtil.generateNonceStr());

            params.put("transaction_id",paymentInfo.getTradeNo());
            params.put("out_trade_no", paymentInfo.getOutTradeNo());
            params.put("out_refund_no","tk"+paymentInfo.getOutTradeNo());
            //这里我改了数据库，数据库是1  以元为单位，但是微信这里的单位是  分 所以应该需要乘100

            BigDecimal totalAmount = paymentInfo.getTotalAmount();
            params.put("refund_fee",totalAmount.toString());
            params.put("total_fee",totalAmount.toString());
            String xml = WXPayUtil.generateSignedXml(params, constansProperties.getPartnerKey());
            HttpClientUtils client = new HttpClientUtils(url);
            client.setHttps(true);
            client.setCert(true);
            client.setCertPassword(constansProperties.getPartner());
            client.setCertPath(constansProperties.getCert());
            client.setXmlParam(xml);
            client.post();*/
            Map<String, String> params = new HashMap<>();
            params.put("appid", constansProperties.getAppId()); //公众账号ID
            params.put("mch_id", constansProperties.getPartner()); //商户编号
            params.put("nonce_str", WXPayUtil.generateNonceStr()); //随机数
            params.put("transaction_id", paymentInfo.getTradeNo()); //微信订单号
            params.put("out_trade_no", paymentInfo.getOutTradeNo()); //商户订单编号
            params.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo()); //商户退款单号
            //params.put("total_fee", paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).toString());
            //params.put("refund_fee", paymentInfo.getTotalAmount().multiply(new BigDecimal(100)).toString());
            params.put("total_fee", "1"); //测试
            params.put("refund_fee", "1"); //测试
            HttpClientUtils client = new HttpClientUtils("https://api.mch.weixin.qq.com/secapi/pay/refund");
            //将参数转换成xml字符串格式：生成带有签名的xml格式字符串
            String xmlParams = WXPayUtil.generateSignedXml(params, constansProperties.getPartnerKey());
            client.setXmlParam(xmlParams);//将参数放入请求对象的方法体
            client.setHttps(true);//使用https形式发送
            client.setCert(true); //使用证书
            client.setCertPassword(constansProperties.getPartner()); //证书密码
            client.setCertPath(constansProperties.getCert()); //证书路径
            client.post();//发送请求*/
            String resp = client.getContent();
            Map<String, String>  resultMap = WXPayUtil.xmlToMap(resp);
            if ("FAIL".equals(resultMap.get("return_code"))||"FAIL".equals(resultMap.get("result_code"))) {
                log.error("微信申请退款失败"+resultMap.toString());
                throw new YyghException("微信申请退款失败："+"错误码："+resultMap.get("err_code")+"错误描述："+resultMap.get("err_code_des"),ResultCode.ERROR);
            }
            refundInfoService.saveRefundInfo(paymentInfo,resultMap);
        } catch (YyghException e) {
            throw e;
        }catch (Exception e){
            throw new YyghException("微信申请退款失败",ResultCode.ERROR,e);
        }

    }

    @Override
    public void queryWxPay( OrderInfo orderInfo, Map<String, String> resultMap) {
        orderInfoService.updateOrderInfo(orderInfo,resultMap);
    }

    @Override
    public boolean queryPayStatus2(Long orderId) {
        return  orderInfoService.queryOrderStatus(orderId);

    }
}
