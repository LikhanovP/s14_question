package com.rosa.swift.core.data.dto.transports;

import android.os.Parcel;
import android.os.Parcelable;

import com.rosa.swift.core.network.responses.price.PriceTransportsResponse;

/**
 * Представляет вид транспорта
 */
public class TransportType implements Parcelable {

    /**
     * Название вида транспорта
     */
    private String mName;

    /**
     * Код вида транспорта
     */
    private String mCode;

    /**
     * Возвращает название вида транспортировки
     */
    public String getName() {
        return mName;
    }

    /**
     * Возвращает код вида транспортировки
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param typeTransport Вид транспорта из модели ответа от сервера
     *                      на запрос списка видов транспорта
     */
    TransportType(PriceTransportsResponse.TypeTransport typeTransport) {
        mName = typeTransport.getName();
        mCode = typeTransport.getCode();
    }

    @Override
    public String toString() {
        return this.mName;
    }

    //region Implement Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCode);
        parcel.writeString(mName);
    }

    protected TransportType(Parcel in) {
        mCode = in.readString();
        mName = in.readString();
    }

    public static final Parcelable.Creator<TransportType> CREATOR = new Parcelable.Creator<TransportType>() {
        @Override
        public TransportType createFromParcel(Parcel in) {
            return new TransportType(in);
        }

        @Override
        public TransportType[] newArray(int size) {
            return new TransportType[size];
        }
    };

    //endregion

}
