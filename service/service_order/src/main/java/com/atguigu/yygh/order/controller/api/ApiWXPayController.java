package com.atguigu.yygh.order.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.ResultCode;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.WxPayService;
import com.atguigu.yygh.order.utils.ConstansProperties;
import com.atguigu.yygh.order.utils.StreamUtils;
import com.github.wxpay.sdk.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/order/wxpay")
@Slf4j
public class ApiWXPayController {
    @Autowired
    ConstansProperties constansProperties;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private OrderInfoService orderInfoService;



    @ApiOperation("获取支付二维码url")
    @GetMapping("auth/createNative/{orderId}")
    public R createNative( HttpServletRequest request,@PathVariable("orderId") Long orderId) {
        
        //校验用户登录状态
        AuthContextHolder.checkAuth(request);
        String remoteAddr = request.getRemoteAddr();
        Map map = wxPayService.createNative(orderId, remoteAddr);
        return R.ok().data(map);
    }

    @ApiOperation("查询支付状态")
@GetMapping("queryPayStatus/{orderId}")
public R queryPayStatus(@PathVariable("orderId") Long orderId) {

    //调用查询接口
    boolean success = wxPayService.queryPayStatus(orderId);
    if (success) {
        return R.ok().message("支付成功");
    }
    return R.ok().message("支付中");
}

    @ApiOperation("支付回调的方式查询支付状态")
    @PostMapping("callback")
    public String queryPayStatusV2(HttpServletRequest request) throws IOException {
        //微信返回的是输入流的形式使用工具转成字符串，传参的字符串是xml形式的
        Map<String, String> returnParams = new HashMap<>(2);
        try {
            ServletInputStream inputStream = request.getInputStream();
            String xml = StreamUtils.inputStream2String(inputStream, "utf-8");
            log.info(xml);
            //把结果转成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            String sign = resultMap.get("sign");
            //在处理业务前，需要对传过来的数据做校验，使用商户秘钥。生成签名，比较签名是否一致
            String signkey = WXPayUtil.generateSignature(resultMap, constansProperties.getPartnerKey());
            if (!signkey.equals(sign)){
                log.error("查询订单状态失败，非法签名");
                returnParams.put("return_code","FAIL");
                returnParams.put("return_msg","验签失败");
                String result = WXPayUtil.mapToXml(returnParams);
                return result;
            }
    //out_trade_no为订单交易号，扫码支付时填写过，微信这边回调过来也带这个参数
    // 时间戳加随机数String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
            String outTradeNo = resultMap.get("out_trade_no");
            OrderInfo orderInfo = orderInfoService.getOrderInfoByoutTradeNo(outTradeNo);
            //这里的amount单位是元，但是微信那边是分，所以要同步，而且amount为浮点型，它数据里的小数点为0，所以才可以这么用
            BigDecimal amount = orderInfo.getAmount().divide(new BigDecimal(100));
            if (orderInfo==null|| !amount.toString().equals(resultMap.get("total_fee"))){
                log.error("查询订单状态失败，金额错误");
                returnParams.put("return_code","FAIL");
                returnParams.put("return_msg","金额错误");
                String result = WXPayUtil.mapToXml(returnParams);
                return result;
            }
            //查询成功时
            if ("SUCCESS".equals(resultMap.get("return_code"))&&"SUCCESS".equals(resultMap.get("result_code"))){
                        //修改订单状态，保存支付信息

                wxPayService.queryWxPay(orderInfo,resultMap);
                returnParams.put("return_code","SUCCESS");
                returnParams.put("return_msg","OK");
                String result = WXPayUtil.mapToXml(returnParams);
                log.info("支付查询成功");
                return result;
            }
            returnParams.put("return_code","FAIL");
            returnParams.put("return_msg","失败");
            String result = WXPayUtil.mapToXml(returnParams);
            log.info("支付查询失败");
            return result;
        } catch (YyghException e) {
            throw e;
        } catch (Exception e){
            log.error("查询订单状态失败");
            throw new YyghException("查询订单状态失败",ResultCode.ERROR,e);
        }

    }

    @ApiOperation("查询支付状态")
    @GetMapping("queryPayStatusV2/{orderId}")
    public R queryPayStatus2(@PathVariable("orderId") Long orderId) {

        //调用查询接口
        boolean success = wxPayService.queryPayStatus2(orderId);
        if (success) {
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}