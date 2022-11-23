package com.atguigu.yygh.task.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: TSK
 * @Date：2022/11/21 10:30
 */
//测试,注释掉,则不会执行这个定时任务
//@Component
public class PatientRemindJob {

    //@Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(fixedDelay = 3000)
    public void printTime() throws InterruptedException {

        Thread.sleep(4000);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
