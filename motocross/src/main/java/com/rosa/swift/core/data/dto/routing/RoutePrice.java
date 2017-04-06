package com.rosa.swift.core.data.dto.routing;

import com.rosa.swift.core.network.responses.price.RoutePriceResponse;

import java.util.Locale;

/**
 * Представляет информацию о стоимости маршрута, полученную от сервиса Sap
 */
public class RoutePrice {

    /**
     * Стоимость маршрута в рублях
     */
    private float mPrice;

    /**
     * Стоимость 1 км в рублях
     */
    private float mCostPoint;

    /**
     * Возвращает стоимость маршрута в рублях
     */
    public float getPrice() {
        return mPrice;
    }

    /**
     * Возвращает стоимость 1 км в рублях
     */
    public float getCostPoint() {
        return mCostPoint;
    }

    /**
     * Возвращает строковое представление стоимости 1 км
     */
    public String getStringCostPoint() {
        return String.format(Locale.US, "%.0f р.", mCostPoint);
    }

    /**
     * Возвращает строковое представление стоимости маршрута
     */
    public String getStringPrice() {
        return String.format(Locale.US, "%,.0f р.", mPrice);
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param routePriceRes Модель ответа от сервера на запрос информации по стоимости маршрута
     */
    public RoutePrice(RoutePriceResponse routePriceRes) {
        if (routePriceRes != null) {
            mPrice = routePriceRes.getRoutePrice();
            mCostPoint = routePriceRes.getCostKilometer();
        }
    }

}
