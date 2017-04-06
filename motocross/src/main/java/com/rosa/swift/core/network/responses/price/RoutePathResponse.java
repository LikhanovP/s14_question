package com.rosa.swift.core.network.responses.price;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutePathResponse {

    @SerializedName("length")
    @Expose
    private float mLength;

    @SerializedName("time")
    @Expose
    private float mSeconds;

    public float getLength() {
        return mLength;
    }

    public float getSeconds() {
        return mSeconds;
    }

}
