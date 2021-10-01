package com.example.flashsale.service;

import com.example.flashsale.dao.ProductDAO;
import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.domain.Product;
import com.example.flashsale.redis.FlashSaleKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlashSaleService {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public Order doFlashSale(FlashSaleUser user, ProductVo productVo) {

        // decrease stock
        boolean success = productService.reduceStock(productVo);
        if (success) {
            // place order, add to flash sale order
            return orderService.createOrder(user, productVo);

        } else {
            setProductOver(productVo.getId());
            return null;
        }

    }

    /**
     * return orderIs, if success
     *        -1, if failed
     *        0, in the queue
     *
     * @param userId
     * @param productId
     * @return
     */
    public long getFlashSaleResult(Long userId, long productId) {

        FlashSaleOrder order = orderService.getFlashSaleOrderByUserIdProductId(userId, productId);
        if (order != null){
            return  order.getOrderId();
        } else {
            boolean isOver = getProduceOver(productId);
            if (isOver) {
                return  -1;
            } else {
                return 0;
            }
        }
    }

    private void setProductOver(Long productId) {
        redisService.set(FlashSaleKey.isProductOver, "" + productId, true);
    }

    private boolean getProduceOver(long productId){
        return redisService.exists(FlashSaleKey.isProductOver, "" + productId);
    }
}
