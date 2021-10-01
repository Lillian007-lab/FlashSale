package com.example.flashsale.redis;

public class FlashSaleKey extends BasePrefix{

    public FlashSaleKey(String prefix) {
        super(prefix);
    }

    public static FlashSaleKey isProductOver = new FlashSaleKey("productOver");
}
