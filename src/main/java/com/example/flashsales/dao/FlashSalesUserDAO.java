package com.example.flashsales.dao;

import com.example.flashsales.domain.FlashSalesUser;
import com.example.flashsales.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FlashSalesUserDAO {

    @Select("select * from flash_sales_user where id = #{id}")
    public FlashSalesUser getById(@Param("id") long id);
}
