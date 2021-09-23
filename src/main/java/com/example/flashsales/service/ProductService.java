package com.example.flashsales.service;

import com.example.flashsales.dao.ProductDAO;
import com.example.flashsales.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductDAO productDAO;

    public List<ProductVo> listProductVo(){
        return productDAO.listProductVo();
    }

}
