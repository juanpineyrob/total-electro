package com.totalelectro.model;

public enum OrderStatus {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 