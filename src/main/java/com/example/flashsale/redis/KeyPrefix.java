package com.example.flashsale.redis;

public interface KeyPrefix {

    public int getExpireSeconds();
    public String getPrefix();
}
