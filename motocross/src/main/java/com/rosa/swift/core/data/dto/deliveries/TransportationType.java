package com.rosa.swift.core.data.dto.deliveries;

import android.text.TextUtils;

public enum TransportationType {
    /**
     * Доставка
     */
    Delivery("Delivery"),
    /**
     * Перемещение
     */
    Relocation("Relocation");

    private String mValue;

    String getValue() {
        return mValue;
    }

    public static TransportationType getType(String type) {
        if (!TextUtils.isEmpty(type)) {
            for (TransportationType transportationType : TransportationType.values()) {
                if (type.equals(transportationType.getValue())) {
                    return transportationType;
                }
            }
        }
        return Delivery;
    }

    @Override
    public String toString() {
        return mValue;
    }

    TransportationType(String type) {
        mValue = type;
    }

}
