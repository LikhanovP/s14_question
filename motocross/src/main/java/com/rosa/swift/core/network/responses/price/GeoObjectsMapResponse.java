package com.rosa.swift.core.network.responses.price;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Представляет класс, соотвествующий ответу от Яндекс карт API 2.1 в формате JSON
 */
public class GeoObjectsMapResponse {

    @SerializedName("GeoObjectCollection")
    @Expose
    private GeoObjectsGeocoderResponse.ResponseGeoObjectCollection mGeoObjectCollection;

    public GeoObjectsGeocoderResponse.ResponseGeoObjectCollection getGeoObjectCollection() {
        return mGeoObjectCollection;
    }

}
