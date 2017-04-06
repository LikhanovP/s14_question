package com.rosa.swift.core.network.requests.chat;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class RoomRequest {

    @SerializedName("ROOM_ID")
    private String mRoomId;

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("STARTDATE")
    private String mStartDate;

    @SerializedName("MESSAGE_ID")
    private String mMessageId;

    public RoomRequest(String roomId, String sessionId) {
        mRoomId = roomId;
        mSessionId = sessionId;
    }

    public RoomRequest(String startDate) {
        mStartDate = startDate;
        mSessionId = DataRepository.getInstance().getSessionId();
    }

}
