package com.example.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void receive() throws UnsupportedEncodingException {
        String message = "word";
        //将消息放入头部
        final MessageProperties messageProperties = MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setContentEncoding("utf-8")
                .setHeader("hello", message)
                .build();

        //将消息放入Body中
        final Message msg = MessageBuilder
                .withBody(message.getBytes("utf-8"))
                .andProperties(messageProperties)
                .build();

        //第一个参数:交换机名称  第二个参数：routingKey  第三个参数：Message
        rabbitTemplate.send("ex.boot", "key.boot", msg);
    }

    @Test
    void receive2() {
        //2、将phone 和code封装到map中
        Map<String, String> map = new HashMap<>();
        map.put("phone", "18889899090");
        map.put("code", "code");
        rabbitTemplate.convertAndSend("ex2.boot", "key2.boot", map);
    }

}
