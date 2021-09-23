package com.example.flashsales.domain;

import java.util.Date;

public class FlashSalesProduct {

    private Long id;
    private Long productId;
    private Double flashSalesPrice;
    private Integer flashSalesStock;
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
