@[TOC](文章预览：)
rabbitmq可以将消息放入到head中或者放入body中然后发送。

## 1 、环境准备

### 1.1添加依赖

```xml
     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```

### 1.2 配置yml

```yml
spring:
  application:
    name: springboot_rabbit
  rabbitmq:
    host: 139.9.123.123
    virtual-host: /
    username: admin
    password: 123
    port: 5672
```

## 2、提供者发送消息

### 2.1 send
```java
    @Test
    void receive() throws UnsupportedEncodingException {
    String message="word";
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
```

### 2.2 convertAndSend

```java
    @Test
    void receive2() {
        //2、将phone 和code封装到map中
        Map<String, String> map = new HashMap<>();
        map.put("phone", "18889899090");
        map.put("code", "code");
        rabbitTemplate.convertAndSend("ex2.boot", "key2.boot", map);
    }
```

**说明：**

```java
rabbitTemplate.send("ex.boot", "key.boot", msg);
rabbitTemplate.convertAndSend("ex2.boot", "key2.boot", map);
```
消息发送者发送消息时，需要指定交换机名称和routingKey这样另一端消费者根据交换机名称和routingKey才能到mq中匹配到对应的队列，进行消费。

## 3、消费者接收消息

### 3.1方式一 
RabbitConfig  配置文件
```java
import org.springframework.amqp.core.*;
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

```
**接收方式一**：
```java
@Component
public class MyMessageListener1 {

    @RabbitListener(queues = "queue.boot")
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
}
```
**结果**
```java
word
hello = word
```
**接收方式二**

```java
@Component
public class MyMessageListener {
    private Integer index = 0;
    @RabbitListener(queues = "queue.boot1")
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
```
**结果**

```java
(Body:'word' MessageProperties [headers={hello=word}, contentType=text/plain, contentEncoding=utf-8, contentLength=0, 
receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=ex.boot, receivedRoutingKey=key.boot, 
deliveryTag=1, consumerTag=amq.ctag-33BWKjwGT34_7xaetGd2NA, consumerQueue=queue.boot])
hello = word
s = word
```
### 3.2方式二

```java
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
```
**结果**：

```java
phone = 18889899090
code = code
```


# 案例源码
[案例源码](https://github.com/wxl52d41/rabbitmq.git)
![在这里插入图片描述](https://img-blog.csdnimg.cn/1f21c9bd67e143f68a5285869b2fc51b.png)

