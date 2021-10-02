package com.example.flashsale.controller;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.rabbitmq.FlashSaleMessage;
import com.example.flashsale.rabbitmq.MQSender;
import com.example.flashsale.redis.FlashSaleKey;
import com.example.flashsale.redis.OrderKey;
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
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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
     * After caching and RabbitMQ added:
     * QPS: 2350
     *
     * @param model
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping(value = "/{path}/do_flash_sale", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doFlashSale(Model model, FlashSaleUser user,
                                       @RequestParam("productId")long productId,
                                       @PathVariable("path") String path){
        model.addAttribute("user", user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // verify path
        boolean isValidPath = flashSaleService.checkPath(user, productId, path);
        if (!isValidPath){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
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




    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getFlashSalePath(Model model, FlashSaleUser user,
                                           @RequestParam("productId") long productId,
                                           @RequestParam("verifyCode") int verifyCode){
        model.addAttribute("user", user);
        if (user == null){
            return  Result.error(CodeMsg.SESSION_ERROR);
        }

        // verify code
        boolean isValid = flashSaleService.checkVerifyCode(user, productId, verifyCode);
        if (!isValid) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = flashSaleService.createFlashSalePath(user, productId);
        return Result.success(path);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getFlashSaleVerifyCode(Model model, FlashSaleUser user,
                                                 @RequestParam("productId") long productId,
                                                 HttpServletResponse response){

        if (user == null){
            return  Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = flashSaleService.createVerifyCode(user, productId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error(CodeMsg.FLASH_SALE_FAILED);
        }
    }



    /**
     * Reset for testing purpose
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        int stockCountTOReset = 100;
        List<ProductVo> productVoList = productService.listProductVo();
        for (ProductVo productVo: productVoList) {
            productVo.setFlashSaleStock(stockCountTOReset);;
            redisService.set(ProductKey.getFlashSaleStock, "" + productVo.getId(), stockCountTOReset);
            localOverMap.put(productVo.getId(), false);
        }
        redisService.delete(OrderKey.getFlashSaleOrderByUidPid.getPrefix() + "*");
        redisService.delete(FlashSaleKey.isProductOver.getPrefix() + "*");
        flashSaleService.reset(productVoList);
        return Result.success(true);
    }
}
