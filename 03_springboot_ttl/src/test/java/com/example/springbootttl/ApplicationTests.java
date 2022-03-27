package com.example.springbootttl;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 整个队列的消息设置过期时间
     *
     * @date 2022/3/24
     */
    @Test
    void sendMessage() {
        rabbitTemplate.convertAndSend(
                "ex.pay.ttl-waiting",
                "pay.ttl-waiting",
                "发送了TTL-WAITING-MESSAGE");
        System.out.println("queue-ttl-ok = ");
    }


    /**
     * 局部设置消息过期
     *
     * @date 2022/3/24
     */
    @Test
    void sendTTLMessage() throws UnsupportedEncodingException {
        MessageProperties properties = new MessageProperties();
        properties.setExpiration("5000");
        Message message = new Message("发送了WAITING-MESSAGE".getBytes("utf-8"), properties);
        rabbitTemplate.convertAndSend("ex.pay.waiting", "pay.waiting", message);
        System.out.println("msg-ttl-ok = ");
    }

}
