package com.rosa.swift.core.network.requests.log;

import com.google.gson.annotations.SerializedName;

public class LogRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("LOGS")
    private String mLogs;

    public LogRequest(String sessionId, String logs) {
        mSessionId = sessionId;
        mLogs = logs;
    }

}
