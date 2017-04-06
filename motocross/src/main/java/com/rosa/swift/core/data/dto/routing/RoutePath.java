package com.rosa.swift.core.data.dto.routing;

import com.rosa.swift.core.network.responses.price.RoutePathResponse;

import java.io.Serializable;

/**
 * Представляет информацию о продолжительности маршрута, полученную от маршрутизатора Яндекса
 */
public class RoutePath implements Serializable {

    /**
     * Длинна маршрута в метрах
     */
    private float mLength;

    /**
     * Продолжительность маршрута по времени в секундах
     */
    private float mSeconds;

    /**
     * Возвращает длинну маршрута в метрах
     */
    public float getLength() {
        return mLength;
    }

    /**
     * Возвращает продолжительность маршрута по времени в секундах
     */
    public float getSeconds() {
        return mSeconds;
    }

    /**
     * Возвращает строковое представление длинны маршрута
     */
    public String getStringLength() {
        //парсим метры
        int kilometers = (int) Math.round(mLength / 1000.0);
        return String.format("%1$s км", kilometers);
    }

    /**
     * Возвращает строковое представление продолжительность маршрута по времени
     */
    public String getStringTime() {
        //парсим секунды
        int hours = ((int) mSeconds % 86400) / 3600;
        int minutes = ((int) mSeconds % 3600) / 60;

        return hours != 0 ? String.format("%1$s ч : %2$s мин", hours, minutes) :
                String.format("%1$s мин", minutes);
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param routePathRes Модель ответа от сервера на запрос информации по продолжительности маршрута
     */
    public RoutePath(RoutePathResponse routePathRes) {
        if (routePathRes != null) {
            mLength = routePathRes.getLength();
            mSeconds = routePathRes.getSeconds();
        }
    }

}
