package com.example.flashsale.service;

import com.example.flashsale.dao.ProductDAO;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.domain.Product;
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

    @Transactional
    public Order doFlashSale(FlashSaleUser user, ProductVo productVo) {

        // decrease stock
        productService.reduceStock(productVo);

        // place order, add to flash sale order
        return orderService.createOrder(user, productVo);
    }
}
