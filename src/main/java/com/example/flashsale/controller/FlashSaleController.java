package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.service.FlashSaleService;
import com.example.flashsale.service.OrderService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.service.UserService;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.ldap.SortResponseControl;

@Controller
@RequestMapping("/flash_sale")
public class FlashSaleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    FlashSaleService flashSaleService;

    @RequestMapping("do_flash_sale")
    public String doFlashSale(Model model, FlashSaleUser user,
                              @RequestParam("productId")long productId){
        model.addAttribute("user", user);
        if (user == null){
            return "login";
        }

        // check if the product is in stock
        ProductVo productVo = productService.getProductVoByProductId(productId);
        int stock = productVo.getFlashSaleStock();
        if (stock <= 0){
            model.addAttribute("errmsg", CodeMsg.FLASH_SALE_OUT_OF_STOCK.getMsg());
            return "flash_sale_fail";
        }
        System.out.println("stock: " + stock);


        // A user can't place multiple orders of a same flash sale product
        FlashSaleOrder flashSaleOrder = orderService.getFlashSaleOrderByUserIdProductId(user.getId(), productId);
        if (flashSaleOrder != null){
            model.addAttribute("errmsg", CodeMsg.FLASH_SALE_REPEAT.getMsg());
            return "flash_sale_fail";
        }

        // decrease stock, place order, add to flash sale order
        Order order = flashSaleService.doFlashSale(user, productVo);
        model.addAttribute("order", order);
        model.addAttribute("product", productVo);

        return "order_detail";
    }
}