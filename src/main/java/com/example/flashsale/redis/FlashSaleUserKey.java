package com.example.flashsale.redis;

public class FlashSaleUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 36000 * 24 * 2;

    public FlashSaleUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static FlashSaleUserKey sessionId = new FlashSaleUserKey(TOKEN_EXPIRE,"sessionID");
    public static FlashSaleUserKey getById = new FlashSaleUserKey(0,"id");

}
