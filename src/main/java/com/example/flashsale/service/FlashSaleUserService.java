package com.example.flashsale.service;

import com.example.flashsale.Exception.GlobalException;
import com.example.flashsale.dao.FlashSaleUserDAO;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.FlashSaleUserKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.util.MD5Util;
import com.example.flashsale.util.UUIDUtil;
import com.example.flashsale.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class FlashSaleUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    FlashSaleUserDAO flashSaleUserDAO;

    @Autowired
    RedisService redisService;

    public FlashSaleUser getById(long id){

        // get from redis cache
        FlashSaleUser user = redisService.get(FlashSaleUserKey.getById, "" + id, FlashSaleUser.class);
        if (user != null){
            return  user;
        }

        // get from DB and update redis
        user =  flashSaleUserDAO.getById(id);
        if (user != null){
            redisService.set(FlashSaleUserKey.getById, "" + id, user);
        }
        return user;
    }


    public boolean updatePassword(String token, long id, String formPass){
        // get user
        FlashSaleUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        // update DB
        FlashSaleUser userToBeUpdated = new FlashSaleUser();
        userToBeUpdated.setId(id);
        userToBeUpdated.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        flashSaleUserDAO.update(userToBeUpdated);

        // update redis
        redisService.delete(FlashSaleUserKey.getById, "" + id);
        user.setPassword(userToBeUpdated.getPassword());
        redisService.set(FlashSaleUserKey.token, token, user);

        return  true;
    }


    public String login(HttpServletResponse response, LoginVo loginVo) {

        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // determine if mobile exists
        FlashSaleUser flashSaleUser = getById(Long.parseLong(mobile));
        if (flashSaleUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // validate pass
        String dbPass = flashSaleUser.getPassword();
        String saltBD = flashSaleUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltBD);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // generate cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, flashSaleUser);

        return token;
    }

    public FlashSaleUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        FlashSaleUser flashSaleUser = redisService.get(FlashSaleUserKey.token, token, FlashSaleUser.class);
        // extend expiration time
        if (flashSaleUser != null){
            addCookie(response, token, flashSaleUser);
        }
        return flashSaleUser;

    }

    private void addCookie(HttpServletResponse response, String token, FlashSaleUser user) {
        redisService.set(FlashSaleUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(FlashSaleUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}


