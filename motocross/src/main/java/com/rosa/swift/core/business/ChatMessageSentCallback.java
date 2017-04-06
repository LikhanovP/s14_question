package com.rosa.swift.core.business;

import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;

/**
 * Created by yalang on 10.12.2014.
 */
public interface ChatMessageSentCallback {
    void onSentComplete(JChatMessage message, String message_id);
}
