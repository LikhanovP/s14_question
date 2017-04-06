package com.rosa.swift.core.data.dto.geo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;

import java.util.Locale;


/**
 * Представялет точку с географическими координатами
 */
public class GeoPoint implements Parcelable {

    /**
     * Долгота
     */
    @SerializedName("longitude")
    private double mLongitude;

    /**
     * Широта
     */
    @SerializedName("latitude")
    private double mLatitude;


    /**
     * Возвращает долготу объекта
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Возвращает широту объекта
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Возвращает строку координат объекта в формате долготы и широты (56.787878 57.875543)
     */
    public String getCoords() {
        return isCorrect() ? String.format("%1$s %2$s", mLongitude, mLatitude) : null;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    /**
     * Возвращает строку координат объекта в формате широты и долготы (57.875543 56.787878)
     */
    //public String getInverseCoords() {
    //    return isCorrect() ? String.format("%1$s %2$s", mLatitude, mLongitude) : null;
    //}


    /**
     * Возвращает значение, показывающее, корректно ли заданы координаты
     */
    public boolean isCorrect() {
        return mLongitude > 0 && mLatitude > 0;
    }

    @Override
    public String toString() {
        return isCorrect() ? String.format(Locale.US, "Точка [%.4f, %.4f]",
                mLongitude, mLatitude) : super.toString();
    }

    /**
     * Инициализирует экземпляр класса
     */
    GeoPoint() {
        mLongitude = 0;
        mLatitude = 0;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param coords Координаты объекта в формате долготы и широты (56.787878 57.875543)
     */
    GeoPoint(String coords) {
        try {
            //считаем правильной строку координат, которая не пуста и содержит пробел
            if (!TextUtils.isEmpty(coords) && coords.contains(Constants.SEPARATOR_SPACE)) {
                //разделяем координаты
                String[] coordinates = coords.split(Constants.SEPARATOR_SPACE);
                //парсим координаты на долготы и широту
                mLongitude = Double.parseDouble(coordinates[0]);
                mLatitude = Double.parseDouble(coordinates[1]);
            }
        } catch (Exception ex) {
            Log.e(ex);
        }
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param longitude Долгота
     * @param latitude  Широта
     */
    GeoPoint(double longitude, double latitude) {
        mLongitude = longitude;
        mLatitude = latitude;
    }

    //region Implement Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(mLongitude);
        parcel.writeDouble(mLatitude);
    }

    GeoPoint(Parcel in) {
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
    }

    public static final Parcelable.Creator<GeoPoint> CREATOR = new Parcelable.Creator<GeoPoint>() {
        @Override
        public GeoPoint createFromParcel(Parcel in) {
            return new GeoPoint(in);
        }

        @Override
        public GeoPoint[] newArray(int size) {
            return new GeoPoint[size];
        }
    };

    //endregion

}
