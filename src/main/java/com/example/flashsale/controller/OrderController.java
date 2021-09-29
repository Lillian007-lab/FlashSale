package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.result.Result;
import com.example.flashsale.service.FlashSaleUserService;
import com.example.flashsale.service.OrderService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.vo.OrderDetailVo;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    FlashSaleUserService flashSaleUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, FlashSaleUser user, @RequestParam("orderId") long orderId){

        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIT);
        }

        long productId = order.getProductId();
        ProductVo productVo = productService.getProductVoByProductId(productId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setProductVo(productVo);

        return Result.success(orderDetailVo);
    }
}
