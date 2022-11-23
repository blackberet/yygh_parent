package com.atguigu.yygh.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //排除数据库的连接
@EnableDiscoveryClient
@ComponentScan("com.atguigu.yygh")
public class SMSMainStart {
    public static void main(String[] args) {
        SpringApplication.run(SMSMainStart.class,args);
    }
}
