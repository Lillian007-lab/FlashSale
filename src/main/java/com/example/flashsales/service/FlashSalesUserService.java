package com.example.flashsales.service;

import com.example.flashsales.Exception.GlobalException;
import com.example.flashsales.dao.FlashSalesUserDAO;
import com.example.flashsales.domain.FlashSalesUser;
import com.example.flashsales.result.CodeMsg;
import com.example.flashsales.util.MD5Util;
import com.example.flashsales.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashSalesUserService {

    @Autowired
    FlashSalesUserDAO flashSalesUserDAO;

    public FlashSalesUser getById(long id){
        return flashSalesUserDAO.getById(id);
    }

    public boolean login(LoginVo loginVo) {

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
        return true;
    }
}
