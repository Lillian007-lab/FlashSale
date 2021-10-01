package com.example.flashsale.rabbitmq;

import com.example.flashsale.domain.FlashSaleUser;

public class FlashSaleMessage {

    private FlashSaleUser user;
    private long productId;

    public FlashSaleUser getUser() {
        return user;
    }

    public void setUser(FlashSaleUser user) {
        this.user = user;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
