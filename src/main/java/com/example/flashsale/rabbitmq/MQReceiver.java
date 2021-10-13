package com.example.flashsale.rabbitmq;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.service.FlashSaleService;
import com.example.flashsale.service.OrderService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.service.UserService;
import com.example.flashsale.vo.ProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    FlashSaleService flashSaleService;


    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive (String message){
        logger.info("receive message: " + message);
    }


    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1 (String message){
        logger.info("receive topic queue1 message: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2 (String message){
        logger.info("receive topic queue2 message: " + message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void receiveHeader (byte[] message){
        logger.info("receive header queue message: " + new String(message));
    }

    @RabbitListener(queues = MQConfig.FLASH_SALE_QUEUE)
    public void receiveFlashSale (String message){
        logger.info("receive message: " + message);
        FlashSaleMessage flashSaleMessage = RedisService.stringToBean(message, FlashSaleMessage.class);
        FlashSaleUser user = flashSaleMessage.getUser();
        long productId = flashSaleMessage.getProductId();


        // check if the product is in stock
        ProductVo productVo = productService.getProductVoByProductId(productId);
        int stock = productVo.getFlashSaleStock();
        if (stock <= 0){
            return;
        }
        System.out.println("stock: " + stock);

        // A user can't place multiple orders of a same flash sale product
        FlashSaleOrder flashSaleOrder = orderService.getFlashSaleOrderByUserIdProductId(user.getId(), productId);
        if (flashSaleOrder != null){
            return;
        }

        // generate order
        flashSaleService.doFlashSale(user, productVo);
    }
}
