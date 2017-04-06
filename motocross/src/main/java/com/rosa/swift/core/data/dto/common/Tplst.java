package com.rosa.swift.core.data.dto.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.network.responses.structure.StructureUnitResponse;

/**
 * Представляет информацию по месту планирования транспорта
 */
public class Tplst extends GeoObject {

    //region Fields

    /**
     * Код места планирования
     */
    @SerializedName("code")
    private String mCode;

    /**
     * Название места планирования
     */
    @SerializedName("name")
    private String mName;

    //Используется для отгрузок
    /**
     *
     */
    @SerializedName("selected")
    private boolean mSelected;

    /**
     * Код места планирования
     */
    @SerializedName("parent_code")
    private String mParentCode;

    //endregion

    //region Getters and setters

    /**
     * Возвращает код места планирования
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Возвращает название места планирования
     */
    public String getName() {
        return mName;
    }

    /**
     *
     */
    public boolean isSelected() {
        return mSelected;
    }

    /**
     *
     */
    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    /**
     * @return
     */
    public String getParentCode() {
        return mParentCode;
    }

    //endregion

    //region Methods

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && !TextUtils.isEmpty(mCode) && !TextUtils.isEmpty(mName);
    }

    /**
     * Возвращает строковое представление экземпляра класса
     */
    @Override
    public String toString() {
        return TextUtils.isEmpty(mName) ? super.toString() : mName;
    }

    //endregion

    //region Constructors

    public Tplst(StructureUnitResponse unit, String parentCode, boolean isSelected) {
        super(unit.getAddress(), unit.getLongitude(), unit.getLatitude());
        mCode = unit.getCode();
        mName = unit.getName();
        mSelected = isSelected;
        mParentCode = parentCode;
    }

    public Tplst(String code, String name, String address, double longitude, double latitude,
                 boolean isSelected, String parentCode) {
        super(address, longitude, latitude);
        mCode = code;
        mName = name;
        mSelected = isSelected;
        mParentCode = parentCode;
    }

    //endregion

    //region Implement Parcelable

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(mCode);
        parcel.writeString(mName);
        parcel.writeString(mParentCode);
        parcel.writeInt(mSelected ? 1 : 0);
    }

    private Tplst(Parcel in) {
        super(in);
        mCode = in.readString();
        mName = in.readString();
        mParentCode = in.readString();
        mSelected = in.readInt() != 0;
    }

    public static final Parcelable.Creator<Tplst> CREATOR = new Parcelable.Creator<Tplst>() {
        @Override
        public Tplst createFromParcel(Parcel in) {
            return new Tplst(in);
        }

        @Override
        public Tplst[] newArray(int size) {
            return new Tplst[size];
        }
    };

    //endregion

}
