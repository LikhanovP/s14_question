package com.rosa.swift.core.network.json.sap.queue;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.common.JMessage;

import java.util.List;

public class QueueResponse {

    @SerializedName("MESSAGES")
    private List<JMessage> mMessages;

    @SerializedName("LAST_QUEUE_ID")
    private long mLastQueueId;

    @SerializedName("ERROR_MESSAGE")
    private String mErrorMessage;

    public long getLastQueueId() {
        return mLastQueueId;
    }

    public List<JMessage> getMessages() {
        return mMessages;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
