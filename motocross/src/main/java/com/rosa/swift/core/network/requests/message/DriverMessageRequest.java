package com.rosa.swift.core.network.requests.message;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class DriverMessageRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("MESSAGE_ID")
    private String mMessageId;

    @SerializedName("COMMENT")
    private String mComment;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    public DriverMessageRequest(String messageId, String comment, String deliveryNumber) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mMessageId = messageId;
        mComment = comment;
        mDeliveryNumber = deliveryNumber;
    }
}
