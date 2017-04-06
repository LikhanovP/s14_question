package com.rosa.swift.core.business.utils;

public interface Constants {

    //region Actions

    String ACTION_NEW_MESSAGE = "com.rosa.motocross.NEW_MESSAGE";

    //endregion

    //region Arguments

    //endregion

    //region Requests

    int REQUEST_CUP_PERFORMING = 100;

    int REQUEST_SUGGEST_ITEM = 101;

    int REQUEST_TRANSPORT_TYPE = 102;

    int REQUEST_PERMISSION_CODE = 200;

    int REQUEST_PERMISSION_SETTINGS_CODE = 201;

    int REQUEST_PERMISSION_CAMERA_CODE = 202;

    int REQUEST_PERMISSION_LOCATION_CODE = 203;

    int REQUEST_TAKE_PHOTO = 210;

    int REQUEST_PHOTO_DOCUMENT = 211;

    //endregion

    //region Extras

    String EXTRA_SUGGEST_POINT = "EXTRA_SUGGEST_POINT";

    String EXTRA_SUGGEST_GEO_OBJECT = "EXTRA_SUGGEST_GEO_OBJECT";

    String EXTRA_SUGGEST_IS_START = "EXTRA_SUGGEST_IS_START";

    String EXTRA_SESSION = "EXTRA_SESSION";

    String EXTRA_DELIVERY_ADDRESSES = "EXTRA_DELIVERY_POINTS";

    String EXTRA_PHOTO_DATE = "EXTRA_PHOTO_DATE";

    String EXTRA_CAMERA_MODE = "EXTRA_CAMERA_MODE";

    String EXTRA_PHOTO_NAME = "EXTRA_PHOTO_NAME";

    String EXTRA_NEW_MESSAGE = "EXTRA_NEW_MESSAGE";

    //endregion

    //region Camera modes

    int RECIPIENT_CUP_SESSION = 300;

    int RECIPIENT_PHOTO_BASE = 301;

    int RECIPIENT_PHOTO_INCIDENT = 302;

    //endregion

    //region Sap

    /**
     * Логическое значение "Истина" в SAP
     */
    String SAP_TRUE_FLAG = "X";

    /**
     * Логическое значение "Ложь" в SAP
     */
    String SAP_FALSE_FLAG = "";

    //endregion

    //region Date Patterns

    /**
     * Шаблон для отображения даты следующей фотосессии.
     * Пример отображения даты: 01.01.2000 12:30
     */
    String PATTERN_DATE_CUP_SESSION_NEXT = "dd.MM.yyyy HH:mm";

    /**
     * Шаблон для отображения даты записи координаты транспортировки.
     * Пример отображения даты: 01.01.2000 12:30:00
     */
    String PATTERN_DATE_LOCATION_NEXT = "dd.MM.yyyy HH:mm:ss";

    /**
     * Формат даты и времени в SAP.
     * Пример: ГГГГММДД ЧЧММСС
     */
    String FORMAT_SAP_DATETIME = "yyyyMMdd HHmmss";

    /**
     * Формат даты в SAP.
     * Пример: ГГГГММДД
     */
    String FORMAT_SAP_DATE = "yyyyMMdd";

    /**
     * Формат времени в SAP.
     * Пример: ЧЧММСС
     */
    String FORMAT_SAP_TIME = "HHmmss";


    //endregion

    //region Network Services

    String SERVICE_YANDEXMAPS_RESULTS = "5";

    String SERVICE_YANDEXMAPS_FORMAT = "json";

    String SERVICE_YANDEXMAPS_LENGTH_FIELD = "0.01,0.01";

    //endregion

    //region Price

    /**
     * Задержка в миллисекундах до выполнения запроса к геокодеру Яндекса
     */
    int TIME_DELAY_TO_REQUEST_GEOCODER = 1500;

    //endregion

    //region Shift

    int DEFAULT_IDLE_TIME_IN_MILLISECONDS = 3600 * 1000;

    int DEFAULT_QUEUE_NUMBER = 0;

    long QUEUE_LAST_MESSAGE_ID = -1;

    //endregion

    //region Cup Timer

    /**
     * Единица приращения времени для таймера фотосессии ЦУП
     */
    int DELTA_TIME_FOR_CUP_SESSION = 1000;

    /**
     * Значение времени, при котором таймер должен быть остановлен
     */
    int STOP_TIME_FOR_CUP_SESSION = 0;

    //endregion

    //region Transport Location

    /**
     * Единица приращения времени для таймера сервиса определения местоположения
     */
    int DELTA_TIME_FOR_LOCATION_SERVICE = 30000;

    /**
     * Минимальное время через которое выполняется запрос координат Gps
     */
    long GPS_UPDATE_MIN_TIME = 30000; //10 секунд предыдущее значение

    /**
     * Минимальная дистанция через которую выполняется запрос координат Gps
     */
    float GPS_UPDATE_MIN_DISTANCE = 50; //10 метров предыдущее значение

    //endregion

    //region Separators

    String SEPARATOR_SPACE = " ";

    //endregion

    //region DeliveryPattern

    String LAST_TEMPLATE_DATE = "20131027";

    String LAST_TEMPLATE_TIME = "000000";

    //endregion

}
