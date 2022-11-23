package com.atguigu.yygh.oss.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(value = "oss")
//@PropertySource1.不支持yal文件,2.不能和主启动类@EnablConfigurationProperties使用
//@PropertySource(value = "classpath:oss.properties")
public class OssProperties {

    private String bucketname;
    private String endpoint;
    private String keyid;
    private String keysecret;
}
