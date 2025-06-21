package com.totalelectro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingCalculationDTO {
    private String originAddress;
    private String destinationAddress;
    private double distance;
    private double shippingCost;
    private String estimatedDeliveryTime;
    private boolean freeShipping;
    private String errorMessage;
} 