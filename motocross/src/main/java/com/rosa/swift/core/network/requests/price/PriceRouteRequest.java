package com.rosa.swift.core.network.requests.price;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.dto.geo.GeoObject;

/**
 * Представляет данные для запроса стоимости маршрута
 */
public class PriceRouteRequest {

    @SerializedName("VSART")
    private String mCodeTransport;

    @SerializedName("COORDS_A")
    private Point mStartCoords;

    @SerializedName("COORDS_B")
    private Point mEndCoords;

    private class Point {

        @SerializedName("LONGITUDE")
        private String mLongitude;

        @SerializedName("LATITUDE")
        private String mLatitude;

        public Point(String longitude, String latitude) {
            mLongitude = longitude;
            mLatitude = latitude;
        }
    }

    public PriceRouteRequest(String codeTransport, GeoObject startPoint, GeoObject endPoint) {
        if (startPoint != null && endPoint != null) {
            mCodeTransport = codeTransport;
            mStartCoords = new Point(String.valueOf(startPoint.getLongitude()),
                    String.valueOf(startPoint.getLatitude()));
            mEndCoords = new Point(String.valueOf(endPoint.getLongitude()),
                    String.valueOf(endPoint.getLatitude()));
        }
    }
}
