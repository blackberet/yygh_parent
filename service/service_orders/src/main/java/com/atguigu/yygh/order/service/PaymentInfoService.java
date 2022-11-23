package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:15
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo orderInfo);
}
