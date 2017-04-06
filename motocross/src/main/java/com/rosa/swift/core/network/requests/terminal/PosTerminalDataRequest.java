package com.rosa.swift.core.network.requests.terminal;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class PosTerminalDataRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    public PosTerminalDataRequest(String deliveryNumber) {
        mDeliveryNumber = deliveryNumber;
        mSessionId = DataRepository.getInstance().getSessionId();
    }

}
