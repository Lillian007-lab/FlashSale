package com.example.flashsale.rabbitmq;

import com.example.flashsale.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }


    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send topic message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, msg + "2");
    }


    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send fanout message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "",msg);
    }
}
