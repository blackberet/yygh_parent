package com.atguigu.yygh.order.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.order.service.WeiPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:02
 */
@RestController
@RequestMapping("/user/weipay")
public class WeiPayController {

    @Autowired
    private WeiPayService weiPayService;

    @GetMapping("/createNative/{orderId}")
    public R createNative(@PathVariable Long orderId) throws Exception {
        String url = weiPayService.createNative(orderId);
        return R.ok().data("url",url);
    }


    @GetMapping("/queryPayStatus/{orderId}")
    public R queryPayStatus(@PathVariable Long orderId){
        Map<String,String> result = null;
        try {
            result = weiPayService.queryPayStatus(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            return R.error().message("查询失败");
        }
        if (result.get("trade_state").equalsIgnoreCase("SUCCESS")) {//支付成功
            weiPayService.updateStatus(result,orderId);
            return R.ok();
        }
        return R.ok().message("支付中");
    }


}
