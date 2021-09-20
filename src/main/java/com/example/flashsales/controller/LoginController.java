package com.example.flashsales.controller;

import com.example.flashsales.redis.RedisService;
import com.example.flashsales.result.CodeMsg;
import com.example.flashsales.result.Result;
import com.example.flashsales.service.FlashSalesUserService;
import com.example.flashsales.service.UserService;
import com.example.flashsales.util.ValidatorUtil;
import com.example.flashsales.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    FlashSalesUserService flashSalesUserService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(@Valid LoginVo loginVo) {
        logger.info(loginVo.toString());

        /*// Validate inputs
        String pass = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if (StringUtils.isEmpty(pass)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmpty((mobile))){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }*/

        // Login
        CodeMsg codeMsg = flashSalesUserService.login(loginVo);
        if (codeMsg.getCode() == 0){
            return Result.success(true);
        } else {
            return Result.error(codeMsg);
        }
    }

}
