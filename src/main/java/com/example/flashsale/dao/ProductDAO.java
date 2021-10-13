package com.example.flashsale.dao;

import com.example.flashsale.domain.FlashSaleProduct;
import com.example.flashsale.vo.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductDAO {

    @Select("select p.*,fp.flash_sale_price,fp.flash_sale_stock,fp.start_date,fp.end_date from flash_sale_product fp left join product p on fp.product_id = p.id")
    public List<ProductVo> listProductVo();

    @Select("select p.*,fp.flash_sale_price,fp.flash_sale_stock,fp.start_date,fp.end_date from flash_sale_product fp left join product p on fp.product_id = p.id where p.id = #{productId}")
    ProductVo getProductVoByProductId(@Param("productId") long productId);

    @Update("update flash_sale_product set flash_sale_stock = flash_sale_stock - 1 where product_id = #{productId} and flash_sale_stock > 0")
    public int reduceStock(FlashSaleProduct product);

    @Update("update flash_sale_product set flash_sale_stock = #{flashSaleStock} where product_id = #{productId}")
    void resetStock(long productId, int flashSaleStock);
}
