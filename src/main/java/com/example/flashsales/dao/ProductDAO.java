package com.example.flashsales.dao;

import com.example.flashsales.vo.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductDAO {

    @Select("select p.*,fp.flash_sales_price,fp.flash_sales_stock,fp.start_date,fp.end_date from flash_sales_product fp left join product p on fp.product_id = p.id")
    public List<ProductVo> listProductVo();
}
