package com.rosa.swift.core.network.requests.function;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class GateRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("GATE_ID")
    private String mGateId;

    public GateRequest(String gateId) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mGateId = gateId;
    }
}
