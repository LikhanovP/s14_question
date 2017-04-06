package com.rosa.swift.core.network.json.sap.assign;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.common.JMessage;

import java.util.List;

/**
 * Created by yalang on 13.09.13.
 */
public class JChangeOut {

    @SerializedName("MESSAGES")
    private List<JMessage> mMessages;

    @SerializedName("ERROR_MESSAGE")
    private String mErrorMessage;

    public List<JMessage> getMessages() {
        return mMessages;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
