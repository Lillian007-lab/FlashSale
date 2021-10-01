package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.rabbitmq.FlashSaleMessage;
import com.example.flashsale.rabbitmq.MQSender;
import com.example.flashsale.redis.ProductKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.result.Result;
import com.example.flashsale.service.FlashSaleService;
import com.example.flashsale.service.OrderService;
import com.example.flashsale.service.ProductService;
import com.example.flashsale.service.UserService;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/flash_sale")
public class FlashSaleController implements InitializingBean {

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

    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ProductVo> productVoList = productService.listProductVo();
        if (productVoList == null){
            return;
        }
        for (ProductVo productVo: productVoList){
            redisService.set(ProductKey.getFlashSaleStock, "" + productVo.getId(), productVo.getFlashSaleStock());
            localOverMap.put(productVo.getId(), false);
        }
    }



    /**
     * Jmeter Result:
     * Negative flash_sale_stock in flash_sale_product table
     * QPS: 2012
     * Threads: 5000 * 10
     *
     * @param model
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping(value = "do_flash_sale", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doFlashSale(Model model, FlashSaleUser user,
                              @RequestParam("productId")long productId){
        model.addAttribute("user", user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // Utilize memory tag, to reduce visits to redis
        // Check out is local is out of stock
        boolean isLocalOver = localOverMap.get(productId);
        if (isLocalOver) {
            localOverMap.put(productId, true);
            return Result.error(CodeMsg.FLASH_SALE_OUT_OF_STOCK);
        }

        // pre-decrease stock in redis
        long stock = redisService.decr(ProductKey.getFlashSaleStock, "" + productId);
        if (stock < 0){
            return Result.error(CodeMsg.FLASH_SALE_OUT_OF_STOCK);
        }

        // A user can't place multiple orders of a same flash sale product
        FlashSaleOrder flashSaleOrder = orderService.getFlashSaleOrderByUserIdProductId(user.getId(), productId);
        if (flashSaleOrder != null){
            return Result.error(CodeMsg.FLASH_SALE_REPEAT);
        }

        // add to queue
        FlashSaleMessage message = new FlashSaleMessage();
        message.setUser(user);
        message.setProductId(productId);
        mqSender.sendFlashSaleMessage(message);

        return Result.success(CodeMsg.SUCCESS.getCode());

/*        // check if the product is in stock
        ProductVo productVo = productService.getProductVoByProductId(productId);
        int stock = productVo.getFlashSaleStock();
        if (stock <= 0){
            //model.addAttribute("errmsg", CodeMsg.FLASH_SALE_OUT_OF_STOCK.getMsg());
            return Result.error(CodeMsg.FLASH_SALE_OUT_OF_STOCK);
        }
        System.out.println("stock: " + stock);


        // A user can't place multiple orders of a same flash sale product
        FlashSaleOrder flashSaleOrder = orderService.getFlashSaleOrderByUserIdProductId(user.getId(), productId);
        if (flashSaleOrder != null){
            //model.addAttribute("errmsg", CodeMsg.FLASH_SALE_REPEAT.getMsg());
            return Result.error(CodeMsg.FLASH_SALE_REPEAT);
        }

        // decrease stock, place order, add to flash sale order
        Order order = flashSaleService.doFlashSale(user, productVo);
//        model.addAttribute("order", order);
//        model.addAttribute("product", productVo);

        return Result.success(order);*/
    }


    /**
     * return orderIs, if success
     *        -1, if failed
     *        0, in the queue
     *
     * @param model
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> flashSaleResult(Model model, FlashSaleUser user,
                                           @RequestParam("productId") long productId){
        model.addAttribute("user", user);
        if (user == null){
            return  Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = flashSaleService.getFlashSaleResult(user.getId(), productId);
        return Result.success(result);
    }
}
