package com.example.flashsale.service;

import com.example.flashsale.dao.ProductDAO;
import com.example.flashsale.domain.FlashSaleProduct;
import com.example.flashsale.domain.Product;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.util.SpringRequestUtils;

import java.util.List;
import java.util.PropertyPermission;

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

    public boolean reduceStock(ProductVo productVo) {
        FlashSaleProduct product = new FlashSaleProduct();
        product.setProductId(productVo.getId());
        int res = productDAO.reduceStock(product);
        System.out.println("stock is reduced");

        return res> 0;
    }


    /**
     * Reset flash sale stock in table flash_sale_product, for testing purpose
     *
     * @param productVoList
     */
    public void resetStock(List<ProductVo> productVoList) {
        for (ProductVo productVo: productVoList) {
            long productId = productVo.getId();
            int flashSaleStock = productVo.getFlashSaleStock();
            productDAO.resetStock(productId, flashSaleStock);
        }
    }
}
