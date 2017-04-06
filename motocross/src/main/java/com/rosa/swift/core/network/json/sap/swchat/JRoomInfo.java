package com.rosa.swift.core.network.json.sap.swchat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yalang on 05.12.2014.
 */
public class JRoomInfo implements Serializable {

    @SerializedName("ROOM")
    public JRoom room;

    @SerializedName("NEW_MESSAGE")
    public int new_messages;

    @Override
    public String toString() {
        return "JRoomInfo{" +
                "room=" + room.room_id +
                ", new_messages=" + new_messages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JRoomInfo jRoomInfo = (JRoomInfo) o;

        if (!room.equals(jRoomInfo.room)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return room.hashCode();
    }
}
