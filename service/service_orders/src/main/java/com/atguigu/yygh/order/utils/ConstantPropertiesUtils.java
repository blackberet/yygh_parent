package com.atguigu.yygh.order.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: TSK
 * @Date：2022/11/19 10:38
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${weipay.appid}")
    private String appid;
    @Value("${weipay.partner}")
    private String partner;
    @Value("${weipay.partnerkey}")
    private String partnerkey;
    @Value("${weipay.cret}")
    private String cret;


    public static String APPID;
    public static String PARTNER;
    public static String PARTNERKEY;
    public static String CRET;


    @Override
    public void afterPropertiesSet() throws Exception {
        APPID = appid;
        PARTNER = partner;
        PARTNERKEY = partnerkey;
        CRET = cret;
    }
}
