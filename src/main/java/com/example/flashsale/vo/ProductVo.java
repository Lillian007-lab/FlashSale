package com.example.flashsale.vo;

import com.example.flashsale.domain.Product;

import java.util.Date;

public class ProductVo extends Product {

    private Double flashSalePrice;
    private Integer flashSaleStock;
    private Date startDate;
    private Date endDate;

    public Double getFlashSalePrice() {
        return flashSalePrice;
    }

    public void setFlashSalePrice(Double flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }

    public Integer getFlashSaleStock() {
        return flashSaleStock;
    }

    public void setFlashSaleStock(Integer flashSaleStock) {
        this.flashSaleStock = flashSaleStock;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
