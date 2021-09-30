package com.example.flashsale.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive (String message){
        logger.info("receive message: " + message);
    }
}
