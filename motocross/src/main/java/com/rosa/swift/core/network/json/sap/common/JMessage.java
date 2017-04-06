package com.rosa.swift.core.network.json.sap.common;

import com.google.gson.annotations.SerializedName;

public class JMessage {

    @SerializedName("ID")
    private String mId;

    @SerializedName("TYPE")
    private String mType;

    @SerializedName("MESSAGE")
    private String mMessage;

    public String getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }

}
