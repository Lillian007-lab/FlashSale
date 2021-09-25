package com.example.flashsale.service;

import com.example.flashsale.dao.OrderDAO;
import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ldap.SortResponseControl;
import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDAO orderDAO;

    public FlashSaleOrder getFlashSaleOrderByUserIdProductId(Long userId, long productId) {
        return orderDAO.getFlashSaleOrderByUserIdProductId(userId, productId);
    }


    @Transactional
    public Order createOrder(FlashSaleUser user, ProductVo productVo) {
        Order order = new Order();
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setProductCount(1);;
        order.setProductId(productVo.getId());
        order.setProductName(productVo.getProductName());
        order.setProductPrice(productVo.getFlashSalePrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setUserId(user.getId());

        long orderId = orderDAO.insert(order);
        System.out.println("order ID: " + orderId);

        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setOrderId(orderId);
        flashSaleOrder.setProductId(productVo.getId());
        flashSaleOrder.setUserId(user.getId());


        orderDAO.insertFlashSaleOrder(flashSaleOrder);
        System.out.println("order is created");
        return order;
    }
}
