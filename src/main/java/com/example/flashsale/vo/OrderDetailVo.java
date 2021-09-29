package com.example.flashsale.vo;

import com.example.flashsale.domain.Order;

public class OrderDetailVo {

    private ProductVo productVo;
    private Order order;

    public ProductVo getProductVo() {
        return productVo;
    }

    public void setProductVo(ProductVo productVo) {
        this.productVo = productVo;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
