package com.example.flashsale.domain;

import java.util.Date;

public class FlashSaleProduct {

    private Long id;
    private Long productId;
    private Double flashSalePrice;
    private Integer flashSaleStock;
    private Date startDate;
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

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
