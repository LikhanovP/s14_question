package com.rosa.swift.core.data.dto.transports;

import com.rosa.swift.core.network.responses.price.PriceTransportsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Представляет коллекцию видов транспорта
 */
public class TransportTypeCollection extends ArrayList<TransportType> {

    /**
     * Список загружен
     */
    private boolean mIsLoaded = false;

    /**
     * Возвращает значение, показывающее, загружен ли список
     */
    public boolean isLoaded() {
        return mIsLoaded;
    }

    /**
     * Задает значение, показывающее, загружен ли список
     */
    public void setLoaded(boolean loaded) {
        mIsLoaded = loaded;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param priceTransportsRes Модель ответа от сервера на запрос списка видов транспорта
     */
    public TransportTypeCollection(PriceTransportsResponse priceTransportsRes) {
        if (priceTransportsRes != null && priceTransportsRes.getListTransports() != null) {
            for (PriceTransportsResponse.TypeTransport transport : priceTransportsRes.getListTransports()) {
                this.add(new TransportType(transport));
            }

            Collections.sort(this, new Comparator<TransportType>() {
                public int compare(TransportType transport1, TransportType transport2) {
                    return transport1.toString().compareTo(transport2.toString());
                }
            });
        }
    }
}
