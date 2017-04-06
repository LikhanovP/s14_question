package com.rosa.swift.core.data.dto.geo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Представялет геообъект с адресом и координатами
 */
public class GeoObject extends GeoPoint {

    /**
     * Адрес объекта
     */
    @SerializedName("address")
    private String mAddressName;

    /**
     * Возвращает строку адреса объекта
     */
    public String getAddress() {
        return this.toString();
    }

    public void setAddressName(String addressName) {
        mAddressName = addressName;
    }

    /**
     * Возвращает значение, показывающее, корректно ли заданы координаты и имя адреса
     */
    @Override
    public boolean isCorrect() {
        return !TextUtils.isEmpty(mAddressName) && super.isCorrect();
    }

    @Override
    public String toString() {
        return !TextUtils.isEmpty(mAddressName) ? mAddressName :
                String.format(Locale.US, "Точка [%.4f, %.4f]", getLongitude(), getLatitude());
    }

    /**
     * Инициализирует экземпляр класса
     */
    public GeoObject() {
        super();
        mAddressName = null;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param address Адрес объекта
     * @param coords  Координаты объекта в формате долготы и широты (56.787878 57.875543)
     */
    public GeoObject(String address, String coords) {
        super(coords);
        mAddressName = address;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param address   Адрес объекта
     * @param longitude Долгота
     * @param latitude  Широта
     */
    public GeoObject(String address, double longitude, double latitude) {
        super(longitude, latitude);
        mAddressName = address;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param longitude Долгота
     * @param latitude  Широта
     */
    public GeoObject(double longitude, double latitude) {
        super(longitude, latitude);
        mAddressName = null;
    }

    //region Implement Parcelable

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mAddressName);
    }

    protected GeoObject(Parcel in) {
        super(in);
        mAddressName = in.readString();
    }

    public static final Parcelable.Creator<GeoObject> CREATOR = new Parcelable.Creator<GeoObject>() {
        @Override
        public GeoObject createFromParcel(Parcel in) {
            return new GeoObject(in);
        }

        @Override
        public GeoObject[] newArray(int size) {
            return new GeoObject[size];
        }
    };

    //endregion

}
