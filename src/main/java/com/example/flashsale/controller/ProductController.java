package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.redis.ProductKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.service.FlashSaleUserService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.vo.ProductVo;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxExpressionContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * QPS: 2176
     * Threads: 5000 * 10
     *
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(Model model, FlashSaleUser user, HttpServletResponse response, HttpServletRequest request){
        model.addAttribute("user", user);
        // get product list
        List<ProductVo> productVoList = productService.listProductVo();
//        System.out.println("productVoList size: "  + productVoList.size());
//        for (ProductVo p: productVoList){
//            System.out.println(p.getId());
//            System.out.println(p.getProductName());
//        }

        model.addAttribute("productList", productVoList);
        //return "product_list";

        // get cache from redis
        String html = redisService.get(ProductKey.getProductList, "", String.class);
        if (!StringUtils.isEmpty(html)){
            return  html;
        }

        // render
        IWebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("product_list", context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(ProductKey.getProductList, "", html);
        }
        return html;

    }

    @RequestMapping(value = "/to_detail/{productId}", produces = "text/html")
    @ResponseBody
    public String detail(Model model, FlashSaleUser user, @PathVariable("productId")long productId,
                         HttpServletResponse response, HttpServletRequest request){

        model.addAttribute("user", user);

        // get cache from redis
        String html = redisService.get(ProductKey.getProductDetail, "" + productId, String.class);
        if (!StringUtils.isEmpty(html)){
            return  html;
        }

        // render
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
        //return "product_detail";

        IWebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("product_detail", context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(ProductKey.getProductDetail, "" + productId, html);
        }
        return html;
    }

}
