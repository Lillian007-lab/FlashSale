package com.example.flashsale.service;

import com.example.flashsale.dao.ProductDAO;
import com.example.flashsale.vo.ProductVo;
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

    public ProductVo getProductVoByProductId(long productId) {
        return productDAO.getProductVoByProductId(productId);
    }
}
