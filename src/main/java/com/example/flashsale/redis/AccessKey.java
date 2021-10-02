package com.example.flashsale.redis;

public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expire) {
        return new AccessKey(expire, "access");
    }
}
