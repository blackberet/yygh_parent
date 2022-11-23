package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.prop.MqConst;
import com.atguigu.yygh.sms.service.SMSService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: TSK
 * @Date：2022/11/18 15:39
 */
@Component
public class SMSListener {

    @Autowired
    private SMSService smsService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    exchange = @Exchange(name = MqConst.EXCHANGE_SMS),
                    value = @Queue(name = MqConst.QUEUE_SMS),
                    key = {MqConst.ROUTING_SMS}
            )
    })
    public void consumer(Message message, Channel channel, MsmVo msmVo){
        System.out.println("就诊人手机号" + msmVo.getPhone());
    }
}
