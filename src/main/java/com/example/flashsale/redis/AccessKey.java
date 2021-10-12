package com.example.flashsale.redis;

public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expire) {
        return new AccessKey(expire, "access");
    }

    public static AccessKey access5Sec = new AccessKey(5, "access5Sec");
}
