package com.example.entity;

import java.math.BigDecimal;

public class CartItem {
    /** 购物车项ID */
    private Integer id;
    /** 用户ID */
    private Integer userId;
    /** 商品ID */
    private Integer productId;
    /** 商品名称 */
    private String productName;
    /** 商品单价 */
    private BigDecimal price;
    /** 购买数量 */
    private Integer num;
    /** 商品图片 */
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
