package com.atguigu.yygh.user.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 将yml文件中的内容绑定到pojo类的属性上
 * 1.Component+Value
 * 2.Component+@ConfigurationProperties(prefix = "wexin")
 * 3.@ConfigurationProperties(prefix = "wexin")+
 * @EnableConfigurationProperties(value = WeixinProperties.class)
 */

@Data
//@Component
@ConfigurationProperties(prefix = "weixin")
public class WeixinProperties {
    private String appid;
    private String appsecret;
    private String redirecturl;
}
