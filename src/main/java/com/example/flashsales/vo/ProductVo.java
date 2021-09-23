package com.example.flashsales.vo;

import com.example.flashsales.domain.Product;

import java.util.Date;

public class ProductVo extends Product {

    private Double flashSalesPrice;
    private Integer flashSalesStock;
    private Date startDate;
    private Date endDate;

    public Double getFlashSalesPrice() {
        return flashSalesPrice;
    }

    public void setFlashSalesPrice(Double flashSalesPrice) {
        this.flashSalesPrice = flashSalesPrice;
    }

    public Integer getFlashSalesStock() {
        return flashSalesStock;
    }

    public void setFlashSalesStock(Integer flashSalesStock) {
        this.flashSalesStock = flashSalesStock;
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
