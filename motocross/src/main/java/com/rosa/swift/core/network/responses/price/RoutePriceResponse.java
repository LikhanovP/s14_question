package com.rosa.swift.core.network.responses.price;

import com.google.gson.annotations.SerializedName;

public class RoutePriceResponse {

    @SerializedName("COST")
    private float mRoutePrice;

    @SerializedName("COST_KM")
    private float mCostOneKm;

    public float getRoutePrice() {
        return mRoutePrice;
    }

    public float getCostKilometer() {
        return mCostOneKm;
    }
}
