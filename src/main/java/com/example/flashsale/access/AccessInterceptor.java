package com.example.flashsale.access;

import com.alibaba.fastjson.JSON;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.AccessKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.result.Result;
import com.example.flashsale.service.FlashSaleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


@Service
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    FlashSaleUserService flashSaleUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        if (handler instanceof HandlerMethod) {

            FlashSaleUser user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null ) {
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();

            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }


            // check visit times in redis, limit x times/ xx seconds
            AccessKey akey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(akey, key, Integer.class );
            if (count == null) {
                redisService.set(akey, key, 1);
            } else if (count < maxCount) {
                redisService.incr(akey, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }


    private FlashSaleUser getUser(HttpServletRequest request, HttpServletResponse response) {

        String paramToken = request.getParameter(FlashSaleUserService.COOKIE_NAME_SESSIONID);
        String coolieToken = getCookieValue(request, FlashSaleUserService.COOKIE_NAME_SESSIONID);
        if (StringUtils.isEmpty(coolieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? coolieToken : paramToken;
        return flashSaleUserService.getByToken(response, token);
    }


    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0){
            return  null;
        }
        for (Cookie cookie: cookies){
            if (cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
