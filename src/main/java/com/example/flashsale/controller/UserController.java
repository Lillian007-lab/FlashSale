package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.Result;
import com.example.flashsale.service.FlashSaleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    FlashSaleUserService flashSaleUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<FlashSaleUser> info(Model model, FlashSaleUser user){
        return  Result.success(user);
    }
}
