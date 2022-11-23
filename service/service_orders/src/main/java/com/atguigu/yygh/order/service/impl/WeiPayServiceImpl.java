package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.order.utils.HttpClient;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:06
 */
@Service
public class WeiPayServiceImpl implements WeiPayService {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public String createNative(Long orderId) throws Exception {
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        paymentInfoService.savePaymentInfo(orderInfo);
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", "挂号费用");
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("total_fee", "1");//为了测试
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "127.0.0.1");
        paramMap.put("trade_type", "NATIVE");
        httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
        httpClient.setHttps(true);
        httpClient.post();//发送请求
        //微信返回的xml字符串
        String content = httpClient.getContent();
        //微信返回的xml字符串
        Map<String, String> result = WXPayUtil.xmlToMap(content);
        System.out.println(result);

        if (result.get("result_code").equalsIgnoreCase("success")) {
            String code_url = result.get("code_url");
            return code_url;
        }else {
            return "";
        }

    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId){
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            httpClient.setHttps(true);
            httpClient.post();
            //微信返回的xml字符串
            String content = httpClient.getContent();
            //微信返回的xml字符串
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Transactional
    @Override
    public void updateStatus(Map<String, String> result, Long orderId) {
        //1.修改订单状态
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);

        //2.支付记录表的支付状态.callbackTime
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTradeNo(result.get("transaction_id"));
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(result.toString());
        paymentInfoService.update(paymentInfo,updateWrapper);

        //通知第三方医院
        Map<String, Object> map = new HashMap<>();
        map.put("hoscode",orderInfo.getHoscode());
        map.put("hosRecordId",orderInfo.getHosRecordId());
        HttpRequestHelper.sendRequest(map,"http://localhost:9998/order/updatePayStatus");

    }

    @Override
    public boolean refund(Long orderId) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = paymentInfoService.getOne(queryWrapper);
        RefundInfo refundInfo = refundInfoService.saveRefund(paymentInfo);
        if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
            return true;
        }
        //请求微信服务器完成退款
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //paramMap.put("out_trade_no",paymentInfo.getOutTradeNo());
        paramMap.put("transaction_id",paymentInfo.getTradeNo());
        paramMap.put("out_refund_no","tk" + paymentInfo.getOutTradeNo());
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");

        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            httpClient.setHttps(true);
            httpClient.setCert(true);//开启证书支持
            httpClient.setCertPassword(ConstantPropertiesUtils.PARTNER);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            if (result !=null && ("SUCCESS").equalsIgnoreCase(result.get("result_code"))) {
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setTradeNo(result.get("refund_id"));
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(result.toString());
                refundInfoService.updateById(refundInfo);
                //微信退款成功
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
