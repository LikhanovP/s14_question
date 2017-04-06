package com.rosa.swift.core.data.dto.common;

import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.network.responses.delivery.DeliveryResponse;

/**
 * Представляет дополнительный адрес выгрузки для доставки
 */
public class DeliveryAddress extends GeoObject {

    /**
     * Номер транспортировки
     */
    private String mTknum;

    /**
     * Порядковый номер дополнительного адреса
     */
    private int mPosition;

    /**
     * Контактное лицо по дополнительному адресу
     */
    private String mContactName;

    /**
     * Номер телефона контактного лица
     */
    private String mPhone;

    //region Getters and setters

    /**
     * Возвращает номер транспортировки
     */
    public String getTknum() {
        return mTknum;
    }

    /**
     * Возвращает порядковый номер дополнительного адреса
     */
    public int getPosition() {
        return mPosition;
    }

    /**
     * Возвращает контактное лицо по дополнительному адресу
     */
    public String getContactName() {
        return mContactName;
    }

    /**
     * Возвращает номер телефона контактного лица
     */
    public String getPhone() {
        return mPhone;
    }

    public void setNumber(String tknum) {
        mTknum = tknum;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void setContactName(String contactName) {
        mContactName = contactName;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    //endregion

    public DeliveryAddress() {

    }

    public DeliveryAddress(DeliveryResponse.AdditionAddress address) {
        super(address != null ? address.getFullAddress() : "",
                address != null ? address.getLongitude() : 0,
                address != null ? address.getLatitude() : 0);

        if (address != null) {
            mTknum = address.getTknum();
            mPosition = address.getPosition();
            mPhone = address.getContactPhone();
            mContactName = address.getContactName();
        }
    }

}
