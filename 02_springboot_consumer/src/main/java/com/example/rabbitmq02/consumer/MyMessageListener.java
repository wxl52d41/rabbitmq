package com.example.rabbitmq02.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyMessageListener {

    //    @RabbitListener(queues = "queue.boot")
    public void getMyMessage(@Payload String mes, @Header(name = "hello") String value,
                             Message message, Channel channel) throws IOException {
        //value为header中数据
        System.out.println("hello = " + value);
        //mes为body中的数据
        System.out.println(mes);

        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 确认消息
        channel.basicAck(deliveryTag, false);
        // 拒收消息
//        channel.basicReject();
    }

    private Integer index = 0;

    @RabbitListener(queues = "queue.boot")
    public void getMyMessage(Message message, Channel channel) throws IOException {
        System.out.println(message);

        //从header中获取数据
        String value = message.getMessageProperties().getHeader("hello");
        //从body中获取消息
        String s = new String(message.getBody());

        System.out.println("hello = " + value);
        System.out.println("s = " + s);
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if (index % 2 == 0) {
            // 确认消息
            channel.basicAck(deliveryTag, false);
        } else {
            // 拒收消息
            channel.basicReject(deliveryTag, false);
        }
        index++;
    }

}
