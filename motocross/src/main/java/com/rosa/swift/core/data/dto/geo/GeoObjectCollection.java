package com.rosa.swift.core.data.dto.geo;

import android.support.annotation.Nullable;

import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.network.responses.price.GeoObjectsGeocoderResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет коллекцию геообъектов, полученных от геокодера Яндекса
 */
public class GeoObjectCollection extends ArrayList<GeoObject> {

    /**
     * Возвращает первый элемент списка
     */
    @Nullable
    public GeoObject getFirst() {
        return this.size() == 1 ? this.get(0) : null;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param geoObjectCollection Модель ответа от геокодера Яндекса
     */
    public GeoObjectCollection(GeoObjectsGeocoderResponse.ResponseGeoObjectCollection geoObjectCollection) {
        if (geoObjectCollection != null) {
            try {
                //получаем список объектов, которые вернул геокодер
                List<GeoObjectsGeocoderResponse.FeatureMember> members = geoObjectCollection.getFeatureMembers();
                //из объектов получаем строку адреса и строку координат
                for (GeoObjectsGeocoderResponse.FeatureMember member : members) {
                    String address = member.getGeoObject()
                            .getMetaDataProperty().getGeocoderMetaData().getAddressDetails()
                            .getCountry().getAddressLine();
                    String position = member.getGeoObject()
                            .getPoint().getPosition();
                    this.add(new GeoObject(address, position));
                }
            } catch (Exception ex) {
                Log.e(ex);
            }
        }
    }

}
