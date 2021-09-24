package com.example.flashsales.dao;

import com.example.flashsales.domain.FlashSaleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FlashSaleUserDAO {

    @Select("select * from flash_sale_user where id = #{id}")
    public FlashSaleUser getById(@Param("id") long id);
}
