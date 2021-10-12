package com.example.flashsale.service;

import com.example.flashsale.domain.FlashSaleOrder;
import com.example.flashsale.domain.FlashSaleUser;
import com.example.flashsale.domain.Order;
import com.example.flashsale.redis.FlashSaleKey;
import com.example.flashsale.redis.RedisService;
import com.example.flashsale.util.MD5Util;
import com.example.flashsale.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class FlashSaleService {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public Order doFlashSale(FlashSaleUser user, ProductVo productVo) {

        // decrease stock
        boolean success = productService.reduceStock(productVo);
        if (success) {
            // place order, add to flash sale order
            return orderService.createOrder(user, productVo);

        } else {
            setProductOver(productVo.getId());
            return null;
        }

    }

    /**
     * return orderIs, if success
     *        -1, if failed
     *        0, in the queue
     *
     * @param userId
     * @param productId
     * @return
     */
    public long getFlashSaleResult(Long userId, long productId) {

        FlashSaleOrder order = orderService.getFlashSaleOrderByUserIdProductId(userId, productId);
        if (order != null){
            return  order.getOrderId();
        } else {
            boolean isOver = getProduceOver(productId);
            if (isOver) {
                return  -1;
            } else {
                return 0;
            }
        }
    }

    private void setProductOver(Long productId) {
        redisService.set(FlashSaleKey.isProductOver, "" + productId, true);
    }

    private boolean getProduceOver(long productId){
        return redisService.exists(FlashSaleKey.isProductOver, "" + productId);
    }

    /**
     * Reset Database for testing purpose
     *
     * @param productVoList
     */
    public void reset(List<ProductVo> productVoList) {
        productService.resetStock(productVoList);
        orderService.deleteOrders();
    }

    /**
     * Generate UUID path
     *
     * @param user
     * @param productId
     * @return
     */
    public String createFlashSalePath(FlashSaleUser user, long productId) {
        String str = MD5Util.md5(UUID.randomUUID() + "qwer");
        redisService.set(FlashSaleKey.getFlashSalePath, "" + user.getId() + "_" + productId, str);
        return str;
    }

    /**
     * Verify path
     *
     * @param user
     * @param productId
     * @param path
     * @return
     */
    public boolean checkPath(FlashSaleUser user, long productId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathFromRedis = redisService.get(FlashSaleKey.getFlashSalePath, "" + user.getId() + "_" + productId, String.class);
        return pathFromRedis.equals(path);
    }


    /**
     * Create verify code image
     *
     * @param user
     * @param productId
     * @return
     */
    public BufferedImage createVerifyCode(FlashSaleUser user, long productId) {
        if (user == null || productId <= 0){
            return null;
        }

        int width = 80;
        int height = 30;
        // create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random random = new Random();
        // make some confusion points
        for (int i = 0 ; i < 50; i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(random);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        // add verification code to redis
        int rnd = calc(verifyCode);
        redisService.set(FlashSaleKey.getVerificationCode, user.getId() + "," + productId, rnd);
        // return the image;
        return image;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();;
            return 0;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    /**
     * +, -, *
     *
     * @param random
     * @return
     */
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];

        String exp = "" + num1 + op1 + num2 + op2 + num3;
        System.out.println("exp: " + exp);
        return exp;
    }

    public boolean checkVerifyCode(FlashSaleUser user, long productId, int verifyCode) {
        if (user == null || productId <= 0) {
            return false;
        }
        Integer verifyCodeOnRedis = redisService.get(FlashSaleKey.getVerificationCode, user.getId() + "," + productId, Integer.class);

        if (verifyCodeOnRedis == null || verifyCodeOnRedis - verifyCode != 0) {
            return false;
        }
        redisService.delete(FlashSaleKey.getVerificationCode, user.getId() + "," + productId);
        return true;
    }
}
