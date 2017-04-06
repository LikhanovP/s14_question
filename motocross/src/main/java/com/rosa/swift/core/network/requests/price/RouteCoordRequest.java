package com.rosa.swift.core.network.requests.price;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.dto.geo.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteCoordRequest {

    private RoutePoint[] mRoutePoints = new RoutePoint[0];

    public RoutePoint[] getRoutePoints() {
        return mRoutePoints;
    }

    private class RoutePoint {

        @SerializedName("type")
        private String mPointType;

        @SerializedName("point")
        private List<Double> mPoints = new ArrayList<>();

        private RoutePoint(String type, double latitude, double longitude) {
            mPointType = type;
            mPoints = new ArrayList<>(Arrays.asList(latitude, longitude));
        }
    }

    public RouteCoordRequest(GeoPoint startPoint, GeoPoint endPoint) {
        mRoutePoints = new RoutePoint[2];
        mRoutePoints[0] = new RoutePoint("viaPoint",
                startPoint.getLatitude(), startPoint.getLongitude());
        mRoutePoints[1] = new RoutePoint("viaPoint",
                endPoint.getLatitude(), endPoint.getLongitude());
    }

    public RouteCoordRequest(GeoPoint[] points) {
        if (points != null) {
            mRoutePoints = new RoutePoint[points.length];
            for (int i = 0; i < points.length; i++) {
                mRoutePoints[i] = new RoutePoint("viaPoint",
                        points[i].getLatitude(),
                        points[i].getLongitude());
            }
        }
    }

}
