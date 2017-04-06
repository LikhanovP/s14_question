package com.rosa.swift.core.data.dto.deliveries;

import android.location.Location;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.dto.common.Delivery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Представляет местоположение доставки в определенный момент времени
 */
public class DeliveryLocation {

    /**
     * Формат для отображения даты транспортировки
     */
    private final static SimpleDateFormat sLocationDateFormat = new SimpleDateFormat(
            Constants.PATTERN_DATE_LOCATION_NEXT,
            Locale.getDefault());

    /**
     * Код транспортировки
     */
    private String mIdTransportation;

    /**
     * Статус транспортировки
     */
    private String mStatusTransportation;

    /**
     * Долгота
     */
    private double mLongitude;

    /**
     * Широта
     */
    private double mLatitude;

    /**
     * Время записи местоположения транспортировки
     */
    private long mTime;

    /**
     * Была ли информация по местоположению передана на сервер
     */
    private boolean mIsTransferred;

    /**
     * Возвращает код транспортировки
     */
    public String getIdTransportation() {
        return mIdTransportation;
    }

    /**
     * Возвращает статус транспортировки
     */
    @SuppressWarnings("unused")
    public String getStatusTransportation() {
        return mStatusTransportation;
    }

    /**
     * Возвращает долготу
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Возвращает широту
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Возвращает время записи местоположения транспортировки в миллисекундах
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Возвращает значение, показывающее, была ли информация по местоположению передана на сервер
     */
    @SuppressWarnings("unused")
    public boolean isTransferred() {
        return mIsTransferred;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%1$.8f, %2$.8f, %3$s, %4$s",
                mLatitude, mLongitude, sLocationDateFormat.format(new Date(mTime)),
                mIdTransportation);
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param location географическое положение
     */
    public DeliveryLocation(Location location) {
        this(null, location);
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param delivery транспортировка
     * @param location географическое положение
     */
    public DeliveryLocation(Delivery delivery, Location location) {
        if (location != null) {
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            //mTime = location.getTime();
            //задаем время с вычетом часового пояса
            mTime = location.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET);
        }

        if (delivery != null) {
            mIdTransportation = delivery.getNumber();
            mStatusTransportation = String.valueOf(delivery.getStatus());
        } else {
            mIdTransportation = "";
            mStatusTransportation = "";
        }

        mIsTransferred = false;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param idTransportation     код транспортировки
     * @param statusTransportation статус транспортировки
     * @param longitude            долгота
     * @param latitude             широта
     * @param time                 время записи местоположения
     * @param isTransferred        была ли информация передана на сервер
     */
    public DeliveryLocation(String idTransportation, String statusTransportation,
                            double longitude, double latitude, long time, boolean isTransferred) {
        mIdTransportation = idTransportation;
        mStatusTransportation = statusTransportation;
        mLongitude = longitude;
        mLatitude = latitude;
        mTime = time;
        mIsTransferred = isTransferred;
    }
}
