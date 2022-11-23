package com.atguigu.yygh.order.sentinel;

import com.atguigu.yygh.model.order.OrderInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: TSK
 * @Date：2022/11/21 15:12
 */
@Component
public class OrderFeignClientFallBack implements FallbackFactory<OrderInfo> {
    @Override
    public OrderInfo create(Throwable throwable) {
        return new OrderInfo();
    }
}
