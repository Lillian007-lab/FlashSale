package com.example.flashsales.redis;

public class FlashSalesUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 36000 * 24 * 2;

    public FlashSalesUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static FlashSalesUserKey token = new FlashSalesUserKey(TOKEN_EXPIRE,"tokenKey");
}
