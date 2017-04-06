package com.rosa.swift.core.data.dto.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Представляет информацию по складу
 */
public class Warehouse implements Serializable {

    /**
     * Код склада
     */
    @SerializedName("code")
    private String mCode;

    /**
     * Название склада
     */
    @SerializedName("name")
    private String mName;

    /**
     * Возвращает код склада
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Возвращает название склада
     */
    public String getName() {
        return mName;
    }

    /**
     * Инициализирует новый экземпляр класса
     *
     * @param code код склада
     * @param name название склада
     */
    public Warehouse(String code, String name) {
        mCode = code;
        mName = name;
    }
}
