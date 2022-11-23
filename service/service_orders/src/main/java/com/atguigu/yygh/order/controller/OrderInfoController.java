package com.atguigu.yygh.order.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;


    @GetMapping("/save/{scheduleId}/{patientId}")
    public R saveOrder(@PathVariable String scheduleId,
                       @PathVariable Long patientId){
        Long orderId = orderInfoService.saveOrder(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }

    //订单列表（条件查询带分页）
    @GetMapping("/auth/{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  OrderQueryVo orderQueryVo, @RequestHeader String token) {
        //设置当前用户id
        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                orderInfoService.selectPage(pageParam,orderQueryVo);
        return R.ok().data("pageModel",pageModel);
    }

    @GetMapping("/auth/getStatusList")
    public R getStatusList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("statusList",statusList);
    }

    //根据订单id查询订单详情
    @GetMapping("/auth/getOrders/{orderId}")
    public R getOrders(@PathVariable Long orderId,@RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId,userId);
        return R.ok().data("orderInfo",orderInfo);
    }


    @GetMapping("/cancelOrder/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderInfoService.cancelOrder(orderId);
        return R.ok();
    }

    @PostMapping("/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderInfoService.getCountMap(orderCountQueryVo);
    }


}

