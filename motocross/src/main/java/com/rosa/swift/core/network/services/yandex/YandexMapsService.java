package com.rosa.swift.core.network.services.yandex;

import com.rosa.swift.core.network.responses.price.GeoObjectsGeocoderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexMapsService {
    /**
     * Возвращает вызов запроса формата Retrofit для получения геообъектов от геокодера Яндекса
     *
     * @param format       Формат возвращаемого типа строки (json или xml)
     * @param geoCode      адрес для геокодирования
     * @param results      количество возвращаемых геокодером значений
     * @param centerRegion задает долготу и широту центра области. Например: 65.534328,57.153033
     * @param lengthField  протяженность области; задается двумя числами,
     *                     первое из которых есть разница между максимальной и минимальной долготой,
     *                     а второе — между максимальной и минимальной широтой данной области.
     *                     Например: 0.01, 0.01
     */
    @GET("/1.x")
    Call<GeoObjectsGeocoderResponse> getGeoObjects(
            @Query("format") String format,
            @Query("geocode") String geoCode,
            @Query("results") String results,
            @Query("ll") String centerRegion,
            @Query("spn") String lengthField
    );
}
