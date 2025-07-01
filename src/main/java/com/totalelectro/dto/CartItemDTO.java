package com.totalelectro.dto;

import com.totalelectro.model.CartItem;

public class CartItemDTO {
    public String productName;
    public String productImage;
    public double productPrice;
    public int quantity;
    public Long productId;

    public CartItemDTO(CartItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        String raw = item.getProduct().getImageUrl();
        if (raw != null && raw.contains("/")) {
            this.productImage = raw.substring(raw.lastIndexOf("/") + 1);
        } else {
            this.productImage = raw;
        }
        double price = item.getProduct().getPrice().doubleValue();
        Double discount = item.getProduct().getDiscountPercent();
        if (discount != null && discount > 0) {
            price = price * (1 - discount / 100.0);
        }
        this.productPrice = price;
        this.quantity = item.getQuantity();
    }
} 