package com.example.flashsale.dao;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDAO {

    @Select("select * from flash_sale_order where user_id=#{userId} and product_id=#{productId}")
    public FlashSaleOrder getFlashSaleOrderByUserIdProductId(@Param("userId")long userId,@Param("productId") long productId);

    @Insert("insert into `flash-sales`.order(user_id, product_id, delivery_addr_id, product_name, product_count, product_price, order_channel, status, create_date)values(" +
            "#{userId}, #{productId}, #{deliveryAddrId}, #{productName}, #{productCount}, #{productPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public long insert(Order order);

    @Insert("insert into flash_sale_order (user_id, product_id, order_id)values(#{userId}, #{productId}, #{orderId})")
    public int insertFlashSaleOrder(FlashSaleOrder flashSaleOrder);

    @Select("select * from `flash-sales`.order where id = #{orderId}")
    public Order getOrderById(@Param("orderId") long orderId);

    @Delete("delete from `flash-sales`.order")
    void deleteOrders();

    @Delete("delete from `flash-sales`.flash_sale_order")
    void deleteFlashSaleOrders();
}
