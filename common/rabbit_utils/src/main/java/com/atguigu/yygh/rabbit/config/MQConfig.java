package com.atguigu.yygh.rabbit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    @Bean
    public MessageConverter messageConverter(){
        //json字符串转换器, 默认是字符串转换器
        return new Jackson2JsonMessageConverter();
    }

    @Bean("exchangeDirectOrder")
    public DirectExchange exchangeDirectOrder(){
        return ExchangeBuilder.directExchange(MQConst.EXCHANGE_DIRECT_ORDER).durable(true).build();
    }

    @Bean("queueOrder")
    public Queue queueOrder(){
        return QueueBuilder.durable(MQConst.QUEUE_ORDER).build();
    }

    @Bean
    public Binding queueOrderBindingExchangeOrder(@Qualifier("exchangeDirectOrder") DirectExchange exchangeDirectOrder,
                                                  @Qualifier("queueOrder") Queue queueOrder){
        return BindingBuilder.bind(queueOrder).to(exchangeDirectOrder).with(MQConst.ROUTING_ORDER);
    }

    @Bean("exchangeDirectSMS")
    public DirectExchange exchangeDirectSMS(){
        return ExchangeBuilder.directExchange(MQConst.EXCHANGE_DIRECT_SMS).durable(true).build();
    }

    @Bean("queueSMS")
    public Queue queueSMS(){
        return QueueBuilder.durable(MQConst.QUEUE_SMS).build();
    }

    @Bean
    public Binding queueSMSBindingExchangeSMS(@Qualifier("exchangeDirectSMS") DirectExchange exchangeDirectSMS,
                                                  @Qualifier("queueSMS") Queue queueSMS){
        return BindingBuilder.bind(queueSMS).to(exchangeDirectSMS).with(MQConst.ROUTING_SMS);
    }
}