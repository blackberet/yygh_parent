package com.atguigu.yygh.order.listener;

import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.prop.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: TSK
 * @Date：2022/11/21 10:51
 */
@Component
public class TaskListener {


    @Autowired
    private OrderInfoService orderInfoService;

    @RabbitListener(queues = MqConst.QUEUE_TASK)
    public void getMessage(Message message, Channel channel){
        orderInfoService.patientRemind();
    }
}
