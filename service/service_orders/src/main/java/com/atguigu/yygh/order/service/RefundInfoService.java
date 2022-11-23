package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: TSK
 * @Date：2022/11/19 15:17
 */
public interface RefundInfoService extends IService<RefundInfo> {
    RefundInfo saveRefund(PaymentInfo paymentInfo);
}
