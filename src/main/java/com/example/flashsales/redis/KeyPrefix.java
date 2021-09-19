package com.example.flashsales.redis;

public interface KeyPrefix {

    public int expireSeconds();
    public String getPrefix();
}
