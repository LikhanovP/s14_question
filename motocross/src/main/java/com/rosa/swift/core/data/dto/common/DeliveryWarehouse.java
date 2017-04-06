package com.rosa.swift.core.data.dto.common;

/**
 * Представляет транспортную доставку
 */
public class DeliveryWarehouse extends Warehouse {

    private String mDeliveryNumber;

    public String getDeliveryNumber() {
        return mDeliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        mDeliveryNumber = deliveryNumber;
    }

    public DeliveryWarehouse(String number, Warehouse warehouse) {
        super(warehouse.getCode(), warehouse.getName());
        mDeliveryNumber = number;
    }

    public DeliveryWarehouse(String number, String code, String name) {
        super(code, name);
        mDeliveryNumber = number;
    }

}
