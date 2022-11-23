package com.atguigu.yygh.task.config;

import com.atguigu.yygh.prop.MqConst;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: TSK
 * @Date：2022/11/21 10:53
 */
@Configuration
public class TaskConfig {

    @Bean
    public Exchange getExchange(){
       return ExchangeBuilder.directExchange(MqConst.EXCHANGE_TASK).build();
    }


    @Bean
    public Queue getQueue(){
        return QueueBuilder.durable(MqConst.QUEUE_TASK).build();
    }


    @Bean
    public Binding bindQueueToExchange(@Qualifier("getQueue") Queue queue,
                                       @Qualifier("getExchange") Exchange exchange){
        return BindingBuilder
                .bind(queue)//绑定的队列
                .to(exchange)//绑定的交换机
                .with(MqConst.ROUTING_TASK)
                .noargs();//无其他参数
    }

}
