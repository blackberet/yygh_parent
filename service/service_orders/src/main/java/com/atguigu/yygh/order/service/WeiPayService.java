package com.atguigu.yygh.order.service;

import java.util.Map;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:06
 */
public interface WeiPayService {
    String createNative(Long orderId) throws Exception;

    Map<String, String> queryPayStatus(Long orderId) throws Exception;

    void updateStatus(Map<String, String> result, Long orderId);

    boolean refund(Long orderId);
}
