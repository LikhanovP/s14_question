package com.rosa.swift.core.network.requests.chat;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;

public class ChatMessageRequest {
    @SerializedName("CHAT_MESSAGE")
    private JChatMessage mChatMessage;

    @SerializedName("SESSION_ID")
    private String mSessionId;

    public ChatMessageRequest(JChatMessage chatMessage) {
        mChatMessage = chatMessage;
        mSessionId = DataRepository.getInstance().getSessionId();
    }
}
