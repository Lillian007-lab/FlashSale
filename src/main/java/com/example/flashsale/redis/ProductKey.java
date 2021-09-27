package com.example.flashsale.redis;

public class ProductKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 60;

    private ProductKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static ProductKey getProductList = new ProductKey(TOKEN_EXPIRE,"productList");
    public static ProductKey getProductDetail = new ProductKey(TOKEN_EXPIRE,"productDetail");

}
