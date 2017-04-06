package com.rosa.swift.core.business.utils;

public interface AppConfig {
    /**
     * Url геокодера Яндекс Карт v 1.x
     */
    String URL_GEOCODE_MAPS_YANDEX = "https://geocode-maps.yandex.ru/";
    /**
     * Таймаут подключения к серверу
     */
    int MAX_CONNECT_TIMEOUT = 5000;
    /**
     * Таймаут чтения данных с сервера
     */
    int MAX_READ_TIMEOUT = 5000;
}
