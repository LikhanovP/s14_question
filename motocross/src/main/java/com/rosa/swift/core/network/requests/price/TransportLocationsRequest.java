package com.rosa.swift.core.network.requests.price;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Представляет данные для запроса стоимости маршрута
 */
public class TransportLocationsRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("GPS_LOCATION_INFO")
    private List<GpsLocationInfo> mLocationInfoList = new ArrayList<>();

    public TransportLocationsRequest(List<DeliveryLocation> locations) {
        if (locations != null) {
            mSessionId = DataRepository.getInstance().getSessionId();
            for (DeliveryLocation location : locations) {
                mLocationInfoList.add(new GpsLocationInfo(location,
                        String.valueOf(location.getIdTransportation()),
                        String.valueOf(location.getStatusTransportation())));
            }
        }
    }

    public class GpsLocationInfo {

        @SerializedName("POINT")
        private Point mPoint;

        @SerializedName("ERDAT")
        private String mDate;

        @SerializedName("ERZET")
        private String mTime;

        @SerializedName("TKNUM")
        private String mTransportId;

        @SerializedName("STTRG")
        private String mTransportStatus;

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

        public GpsLocationInfo(DeliveryLocation location, String transportId,
                               String transportStatus) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd",
                    Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss",
                    Locale.getDefault());

            if (location != null) {
                mPoint = new Point(String.valueOf(location.getLongitude()),
                        String.valueOf(location.getLatitude()));
                mTransportId = transportId;
                mTransportStatus = transportStatus;
                mDate = dateFormat.format(new Date(location.getTime()));
                mTime = timeFormat.format(new Date(location.getTime()));
            }
        }

    }

}
