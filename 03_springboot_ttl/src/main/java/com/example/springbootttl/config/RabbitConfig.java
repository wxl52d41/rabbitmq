package com.example.springbootttl.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义了两个交换机
 * 1、通过Queue属性设置，队列中所有消息都有相同的过期时间。
 * 2、对消息自身进行单独设置，每条消息的TTL 可以不同。
 */
@Configuration
public class RabbitConfig {


    /**-----------------------TTL交换机和队列配置------------------------------------------------*/
    /**
     * TTL队列
     */
    @Bean
    public Queue queueTTLWaiting() {
        Map<String, Object> props = new HashMap<>();
        //对于该队列中的消息，设置都等待10s
        props.put("x-message-ttl", 10000);
        Queue queue = new Queue("q.pay.ttl-waiting", false, false, false, props);
        return queue;
    }

    /**
     * TTL交换机
     */
    @Bean
    public Exchange exchangeTTLWaiting() {
        DirectExchange exchange = new DirectExchange("ex.pay.ttl-waiting", false, false);
        return exchange;
    }

    /**
     * TTL交换机和队列绑定
     */
    @Bean
    public Binding bindingTTLWaiting() {
        return BindingBuilder.bind(queueTTLWaiting()).to(exchangeTTLWaiting()).with("pay.ttl-waiting").noargs();
    }

    /**
     * -----------------------普通交换机和队列配置------------------------------------------------
     */
    @Bean
    public Queue queueWaiting() {
        Queue queue = new Queue("q.pay.waiting", false, false,
                false);
        return queue;
    }

    /**
     * 该交换器使用的时候，需要给每个消息设置有效期   
     */
    @Bean
    public Exchange exchangeWaiting() {
        DirectExchange exchange = new DirectExchange("ex.pay.waiting", false, false);
        return exchange;
    }


    @Bean
    public Binding bindingWaiting() {
        return BindingBuilder.bind(queueWaiting()).to(exchangeWaiting()).with("pay.waiting").noargs();
    }
}
