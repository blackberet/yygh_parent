package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.hosp.service.ScheService;
import com.atguigu.yygh.prop.MqConst;
import com.atguigu.yygh.rabbitmq.RabbitService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
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
 * @Date：2022/11/18 15:22
 */

@Component
public class OrderListener {

    @Autowired
    private ScheService scheService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = MqConst.QUEUE_ORDER),
                    exchange = @Exchange(value = MqConst.EXCHANGE_ORDER),
                    key = {MqConst.ROUTING_KEY})
    })
    public void consumer(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        if (availableNumber != null) {
            scheService.updateAvailableNumber(scheduleId,availableNumber);
        }else {
            scheService.cancelAvailableNumber(scheduleId);
        }
        MsmVo msmVo = orderMqVo.getMsmVo();
        if (msmVo != null) {
            rabbitService.sendMessage(MqConst.EXCHANGE_SMS,MqConst.ROUTING_SMS,msmVo);
        }

    }
}
