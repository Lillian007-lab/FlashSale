package com.example.flashsale.controller;

import com.example.flashsale.domain.User;
import com.example.flashsale.rabbitmq.MQSender;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.redis.UserKey;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.result.Result;
import com.example.flashsale.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("hello, rabbitMQ");
        return Result.success("hello, RabbitMQ");
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "Lillian");
        return "hello";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello, success");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        System.out.println("User Name: " + user.getName());
        System.out.println("Test Name: " + user.getTestName());
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();

        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById, "1", User.class);

        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("adf123");
        boolean val1 = redisService.set(UserKey.getById, "1", user);

        return Result.success(val1);
    }
}
