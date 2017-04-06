package com.rosa.swift.core.network.json.sap.userdata;

import com.google.gson.annotations.SerializedName;

public class UserDataResponse {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("ERROR_MESSAGE")
    private String mErrorMessage;

    @SerializedName("VSART")
    private String mTypeTransport;

    public String getSessionId() {
        return mSessionId;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public String getTypeTransport() {
        return mTypeTransport;
    }
}
