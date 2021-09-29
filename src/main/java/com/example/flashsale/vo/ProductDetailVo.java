package com.example.flashsale.vo;

import com.example.flashsale.domain.FlashSaleUser;

public class ProductDetailVo {

    private int flashSaleStatus = 0;
    private int remainingSecToStart = 0;
    private ProductVo productVo;
    private FlashSaleUser user;

    public int getFlashSaleStatus() {
        return flashSaleStatus;
    }

    public void setFlashSaleStatus(int flashSaleStatus) {
        this.flashSaleStatus = flashSaleStatus;
    }

    public int getRemainingSecToStart() {
        return remainingSecToStart;
    }

    public void setRemainingSecToStart(int remainingSecToStart) {
        this.remainingSecToStart = remainingSecToStart;
    }

    public ProductVo getProductVo() {
        return productVo;
    }

    public void setProductVo(ProductVo productVo) {
        this.productVo = productVo;
    }

    public FlashSaleUser getUser() {
        return user;
    }

    public void setUser(FlashSaleUser user) {
        this.user = user;
    }
}
