package com.example.flashsales.service;

import com.example.flashsales.Exception.GlobalException;
import com.example.flashsales.dao.FlashSalesUserDAO;
import com.example.flashsales.domain.FlashSalesUser;
import com.example.flashsales.redis.FlashSalesUserKey;
import com.example.flashsales.redis.RedisConfig;
import com.example.flashsales.redis.RedisService;
import com.example.flashsales.result.CodeMsg;
import com.example.flashsales.util.MD5Util;
import com.example.flashsales.util.UUIDUtil;
import com.example.flashsales.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class FlashSalesUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    FlashSalesUserDAO flashSalesUserDAO;

    @Autowired
    RedisService redisService;

    public FlashSalesUser getById(long id){
        return flashSalesUserDAO.getById(id);
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {

        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // determine if mobile exists
        FlashSalesUser flashSalesUser = getById(Long.parseLong(mobile));
        if (flashSalesUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // validate pass
        String dbPass = flashSalesUser.getPassword();
        String saltBD = flashSalesUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltBD);
        if (calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // generate cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, flashSalesUser);

        return true;
    }

    public FlashSalesUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        FlashSalesUser flashSalesUser = redisService.get(FlashSalesUserKey.token, token, FlashSalesUser.class);
        // extend expiration time
        if (flashSalesUser != null){
            addCookie(response, token, flashSalesUser);
        }
        return flashSalesUser;

    }

    private void addCookie(HttpServletResponse response, String token, FlashSalesUser user) {
        redisService.set(FlashSalesUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(FlashSalesUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}


