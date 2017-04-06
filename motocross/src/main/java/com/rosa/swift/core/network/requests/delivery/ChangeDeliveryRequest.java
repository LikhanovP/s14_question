package com.rosa.swift.core.network.requests.delivery;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class ChangeDeliveryRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    @SerializedName("ADDS")
    private String mAdditions;

    public ChangeDeliveryRequest(String deliveryNumber) {
        this(deliveryNumber, "");
    }

    public ChangeDeliveryRequest(String deliveryNumber, String additions) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mDeliveryNumber = deliveryNumber;
        mAdditions = additions;
    }

}
