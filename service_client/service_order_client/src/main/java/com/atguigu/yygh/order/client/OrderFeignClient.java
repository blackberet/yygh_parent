package com.atguigu.yygh.order.client;

import com.atguigu.yygh.order.sentinel.OrderFeignClientFallBack;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Author: TSK
 * @Date：2022/11/21 14:59
 */
@FeignClient(value = "service-order",path = "/user/order",fallbackFactory = OrderFeignClientFallBack.class)
public interface OrderFeignClient {

    @PostMapping("/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);

}
