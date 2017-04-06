package com.rosa.swift.core.network.responses.structure;

import com.google.gson.annotations.SerializedName;

/**
 * Представляет модель ответа сервера по структуре
 */
public class StructureUnitResponse {

    //region Fields

    /**
     * Код структуры
     */
    @SerializedName("CODE")
    private String mCode;

    /**
     * Название структуры
     */
    @SerializedName("NAME")
    private String mName;

    /**
     * Адрес структуры
     */
    @SerializedName("ADDRESS")
    private String mAddress;

    /**
     * Широта
     */
    @SerializedName("LATITUDE")
    private double mLatitude;

    /**
     * Долгота
     */
    @SerializedName("LONGITUDE")
    private double mLongitude;

    //endregion

    //region Getters

    /**
     * Возвращает код структуры
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Возвращает название структуры
     */
    public String getName() {
        return mName;
    }

    /**
     * Возвращает географический адрес структуры
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Возвращает географическую широту
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Возвращает географическую долготу
     */
    public double getLongitude() {
        return mLongitude;
    }

    //endregion

}
