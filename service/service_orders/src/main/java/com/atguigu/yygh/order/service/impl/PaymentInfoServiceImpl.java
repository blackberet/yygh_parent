package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.mapper.PaymentInfoMapper;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:16
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Override
    public void savePaymentInfo(OrderInfo orderInfo) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderInfo.getId());
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0 ) {
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        paymentInfo.setSubject("预约挂号,支付");
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        baseMapper.insert(paymentInfo);
    }
}
