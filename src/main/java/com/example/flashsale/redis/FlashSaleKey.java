package com.example.flashsale.redis;

public class FlashSaleKey extends BasePrefix{

    public FlashSaleKey(String prefix) {
        super(prefix);
    }

    public FlashSaleKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static FlashSaleKey isProductOver = new FlashSaleKey("productOver");
    public static FlashSaleKey getFlashSalePath = new FlashSaleKey(60, "flashSalePath");

}
