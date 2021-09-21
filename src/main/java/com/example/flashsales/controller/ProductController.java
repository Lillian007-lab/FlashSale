package com.example.flashsales.controller;

import com.example.flashsales.domain.FlashSalesUser;
import com.example.flashsales.redis.FlashSalesUserKey;
import com.example.flashsales.redis.RedisService;
import com.example.flashsales.service.FlashSalesUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    FlashSalesUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_list")
    public String toList(Model model, FlashSalesUser user){
        model.addAttribute("user", user);
        return "product_list";
    }

    @RequestMapping("/to_detail")
    public String detail(HttpServletResponse response, Model model,
                         @CookieValue (value=FlashSalesUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                         @RequestParam(value=FlashSalesUserService.COOKIE_NAME_TOKEN, required = false) String paramToken){
        //model.addAttribute("user", new FlashSalesUser());
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        FlashSalesUser user = userService.getByToken(response, token);
        System.out.println("user name: " + user.getUsername());
        model.addAttribute("user", user);
        return "product_list";
    }

}
