package com.rosa.swift.core.network.json.sap.swchat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yalang on 04.12.2014.
 * <p>
 * ROOM_ID	ZCHAT_ROOM_ID
 * TOPIC	CHAR100
 * LAST_CHANGE	TIMESTAMP
 * MESSAGE_COUNT	INT4
 * SWIFT_CHAT	ZSWIFT_CHAT
 */
public class JRoom implements Serializable {

    @SerializedName("ROOM_ID")
    public String room_id;

    @SerializedName("TOPIC")
    public String topic;

    @SerializedName("LAST_CHANGE")
    public String last_change;

    @SerializedName("MESSAGE_COUNT")
    public int message_count;

    @SerializedName("SWIFT_CHAT")
    public String swift_chat;

    public String getTitleTopic() {
        if (topic != null) topic = topic.replace("\r\n", "");
        return topic != null ? (topic.length() >= 20 ? topic.substring(0, 20) : topic) : room_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JRoom jRoom = (JRoom) o;

        if (!room_id.equals(jRoom.room_id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return room_id.hashCode();
    }
}
