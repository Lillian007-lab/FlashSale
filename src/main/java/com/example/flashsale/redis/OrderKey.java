package com.example.flashsale.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getFlashSaleOrderByUidPid = new OrderKey("flashSaleUPid");
}
