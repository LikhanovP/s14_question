package com.rosa.swift.core.network.requests.structure;

import com.google.gson.annotations.SerializedName;

public class PlantsRequest {

    @SerializedName("TPLST")
    private String mTownCode;

    public PlantsRequest(String townCode) {
        mTownCode = townCode;
    }

}
