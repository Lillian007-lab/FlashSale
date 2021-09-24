package com.example.flashsales.redis;

public class FlashSaleUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 36000 * 24 * 2;

    public FlashSaleUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static FlashSaleUserKey token = new FlashSaleUserKey(TOKEN_EXPIRE,"token");
}
