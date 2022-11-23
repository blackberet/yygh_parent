package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.order.client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: TSK
 * @Date：2022/11/21 14:54
 */
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @PostMapping("/info")
    public R getOrderInfoStatisticsInfo(@RequestBody OrderCountQueryVo orderCountQueryVo){
        Map<String, Object> result = orderFeignClient.getCountMap(orderCountQueryVo);
        return R.ok().data(result);
    }

}
