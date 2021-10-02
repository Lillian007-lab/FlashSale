package com.example.flashsale.access;

import com.example.flashsale.domain.FlashSaleUser;

public class UserContext {

    private static ThreadLocal<FlashSaleUser> userHolder = new ThreadLocal<>();

    public static void setUser (FlashSaleUser user) {
        userHolder.set(user);
    }

    public static FlashSaleUser getUser() {
        return userHolder.get();
    }
}
