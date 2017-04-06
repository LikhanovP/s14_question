package com.rosa.swift.core.network.requests.function;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class WaistRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    @SerializedName("LOCATION")
    private String mLocation;

    @SerializedName("CLIENT_PHONE")
    private String mPhone;

    public WaistRequest(String deliveryNumber, String location, String phone) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mDeliveryNumber = deliveryNumber;
        mLocation = location;
        mPhone = phone;
    }

}
