package com.atguigu.yygh.sms.service.impl;


import com.atguigu.yygh.sms.service.SMSService;
import com.atguigu.yygh.sms.utils.HttpUtils;
import com.atguigu.yygh.sms.utils.RandomUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;



    @Override
    public Boolean sendCode(String phone) {
        //节约验证码,若有则不调用山东鼎信验证码服务,直接返回true
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return true;
        }
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "812e695cd4e145f68978a2c9b70f4889";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        String fourBitRandom = RandomUtil.getFourBitRandom();
        querys.put("param", "code:" + fourBitRandom);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);

            //验证码存到redis中,存放时间单位定为5天,为了节约验证码
            redisTemplate.opsForValue().set(phone,fourBitRandom,5, TimeUnit.DAYS);
            return true;
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
