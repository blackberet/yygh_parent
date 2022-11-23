package com.atguigu.yygh.task.job;

import com.atguigu.yygh.prop.MqConst;
import com.atguigu.yygh.rabbitmq.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 每天7点执行 提醒就诊
     */
    //@Scheduled(cron = "0 0 7 * * ?")
    @Scheduled(cron = "0/5 * * * * ?")
    public void task1() {
        System.out.println(new Date().toLocaleString());
        rabbitService.sendMessage(MqConst.EXCHANGE_TASK, MqConst.ROUTING_TASK, "");
    }
}