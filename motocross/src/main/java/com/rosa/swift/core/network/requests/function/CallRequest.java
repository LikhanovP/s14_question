package com.rosa.swift.core.network.requests.function;

import com.google.gson.annotations.SerializedName;

public class CallRequest {

    @SerializedName("DRIVER")
    private String mDriver;

    @SerializedName("WHOM")
    private String mWhom;

    @SerializedName("FROM")
    private String mFrom;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    public CallRequest(String driver, String whom, String from, String deliveryNumber) {
        mDriver = driver;
        mWhom = whom;
        mFrom = from;
        mDeliveryNumber = deliveryNumber;
    }

}
