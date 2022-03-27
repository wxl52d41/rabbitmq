package com.example.rabbitmq02.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    /**
     * 声明交换机
     */
    @Bean
    public Exchange exchange() {
        //参数一：交换机名称 参数二：是否持久化 true--交换机持久化，mq重启后交换机会恢复
        // 参数三：是否自动删除，true---使用后自动删除 参数四：参数
        return new TopicExchange("ex.boot", false, false, null);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue queue() {
        //参数一：队列名称 参数二：是否持久化 true--队列持久化，mq重启后队列会恢复
        // 参数三：是否自动删除，true---使用后自动删除 参数四：参数
        return new Queue("queue.boot", false, false, false, null);
    }

    /**
     * 交换机和队列绑定
     */
    @Bean
    public Binding binding() {
        //参数一：队列名称 参数二：交换机名称
        // 参数三：routingKey 参数四：参数
        return new Binding("queue.boot",
                Binding.DestinationType.QUEUE,
                "ex.boot",
                "key.boot",
                null);
    }
}
