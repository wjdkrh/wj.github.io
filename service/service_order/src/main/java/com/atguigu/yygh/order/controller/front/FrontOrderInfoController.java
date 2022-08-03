package com.atguigu.yygh.order.controller.front;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2022-07-27
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/front/order/orderInfo")
public class FrontOrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(@PathVariable String scheduleId, @PathVariable Long patientId, HttpServletRequest request) {
        AuthContextHolder.checkAuth(request);
       Long orderId = orderInfoService.submitOrder(scheduleId,patientId);
       return R.ok().data("orderId",orderId);
    }

    @ApiOperation("根据订单id查询订单详情")
    @GetMapping("auth/getOrder/{orderId}")
    public R getOrder(@PathVariable Long orderId, HttpServletRequest request) {

        //安全校验
        AuthContextHolder.checkAuth(request);

        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return R.ok().data("orderInfo",orderInfo);
    }
    @ApiOperation("订单列表")
    @GetMapping("auth/list")
    public R list(HttpServletRequest request) {

        //安全校验
        Long userId = AuthContextHolder.getUserId(request);

        List<OrderInfo> orderInfolist = orderInfoService.selectList(userId);
        return R.ok().data("orderInfolist", orderInfolist);
    }

    @ApiOperation("取消预约")
    @GetMapping("auth/cancelOrder/{orderId}")
    public R cancelOrder(@PathVariable("orderId") Long orderId, HttpServletRequest request) {

        //安全校验
        AuthContextHolder.checkAuth(request);

        orderInfoService.cancelOrder(orderId);
        return R.ok().message("预约已取消");
    }

}

