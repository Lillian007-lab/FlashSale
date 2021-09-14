package com.example.flashsales.controller;

import com.example.flashsales.domain.User;
//import com.example.flashsales.service.UserService;
import com.example.flashsales.result.CodeMsg;
import com.example.flashsales.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.RectangularShape;

@Controller
@RequestMapping("/demo")
public class SampleController {

//    @Autowired
//    UserService userService;

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
        return Result.error(CodeMsg.server_error);
    }

//    @RequestMapping("/db/get")
//    @RequestBody
//    public Result<String> dbGet(){
//        User user = userService.getById(1);
//
//        return Result
//    }
}
