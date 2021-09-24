package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.service.FlashSaleUserService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    FlashSaleUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @RequestMapping("/to_list")
    public String toList(Model model, FlashSaleUser user){
        model.addAttribute("user", user);
        // get product list
        List<ProductVo> productVoList = productService.listProductVo();
//        System.out.println("productVoList size: "  + productVoList.size());
//        for (ProductVo p: productVoList){
//            System.out.println(p.getId());
//            System.out.println(p.getProductName());
//        }

        model.addAttribute("productList", productVoList);
        return "product_list";
    }

    @RequestMapping("/to_detail/{productId}")
    public String detail(Model model, FlashSaleUser user, @PathVariable("productId")long productId){
        model.addAttribute("user", user);

        ProductVo productVo = productService.getProductVoByProductId(productId);
        model.addAttribute("product", productVo);

        long startAt = productVo.getStartDate().getTime();
        long endAt = productVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int flashSaleStatus = 0;
        int remainingSecToStart = 0;

        if (now < startAt){
            flashSaleStatus = 0;
            remainingSecToStart = (int)((startAt - now) / 1000);
        } else if (now > endAt){
            flashSaleStatus = 2;
            remainingSecToStart = -1;
        } else {
            flashSaleStatus = 1;
            remainingSecToStart = 0;
        }

        System.out.println("flashSaleStatus: " +  flashSaleStatus);
        System.out.println("remainingSecToStart: " +  remainingSecToStart);

        model.addAttribute("flashSaleStatus", flashSaleStatus);
        model.addAttribute("remainingSecToStart", remainingSecToStart);
        return "product_detail";
    }

}
