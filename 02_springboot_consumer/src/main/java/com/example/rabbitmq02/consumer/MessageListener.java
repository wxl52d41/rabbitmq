package com.example.rabbitmq02.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
public class MessageListener {

    private Integer index = 0;

    @RabbitListener(bindings = @QueueBinding(
            //参数 durable exclusive autoDelete 这些默认都是false,参数可以省略
            value = @Queue(name = "queue2.boot", durable = "false", exclusive = "false", autoDelete = "false"),
            exchange = @Exchange(name = "ex2.boot", type = ExchangeTypes.TOPIC),
            key = "key2.boot"))
    public void getMyMessage(Map<String, String> msg, Channel channel, Message message) throws IOException {
        System.out.println("message = " + message);
        // 手机号
        String phone = msg.get("phone");
        System.out.println("phone = " + phone);
        // 验证码
        String code = msg.get("code");
        System.out.println("code = " + code);
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);
    }
}
