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

    public static final String COOKIE_NAME_SESSIONID = "SessionID";

    @Autowired
    FlashSaleUserDAO flashSaleUserDAO;

    @Autowired
    RedisService redisService;

    /**
     * Get By Id
     *
     * - Apply Object Caching
     *
     * @param id
     * @return
     */
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


    /**
     * Update password
     *
     *  - Apply Object Caching
     *
     * @param token
     * @param id
     * @param formPass
     * @return
     */
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
        flashSaleUserDAO.updatePassword(userToBeUpdated);

        // update redis
        redisService.delete(FlashSaleUserKey.getById, "" + id);
        user.setPassword(userToBeUpdated.getPassword());
        redisService.set(FlashSaleUserKey.sessionId, token, user);

        return  true;
    }


    /**
     * Login
     *
     * @param response
     * @param loginVo
     * @return
     */
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

        // generate cookie and add it to client side
        String sessionId = UUIDUtil.uuid();
        addCookie(response, sessionId, flashSaleUser);
        System.out.println("sessionId UUID: " + sessionId);
        return sessionId;
    }

    /**
     * Get By Token
     *
     * @param response
     * @param sessionId
     * @return
     */
    public FlashSaleUser getByToken(HttpServletResponse response, String sessionId) {
        if (StringUtils.isEmpty(sessionId)){
            return null;
        }
        FlashSaleUser flashSaleUser = redisService.get(FlashSaleUserKey.sessionId, sessionId, FlashSaleUser.class);
        // extend expiration time
        if (flashSaleUser != null){
            addCookie(response, sessionId, flashSaleUser);
        }
        return flashSaleUser;

    }

    private void addCookie(HttpServletResponse response, String sessionId, FlashSaleUser user) {
        redisService.set(FlashSaleUserKey.sessionId, sessionId, user);
        Cookie cookie = new Cookie(COOKIE_NAME_SESSIONID, sessionId);
        cookie.setMaxAge(FlashSaleUserKey.sessionId.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}


