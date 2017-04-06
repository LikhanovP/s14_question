package com.rosa.swift.core.network.json.sap.swchat;

import android.text.format.Time;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yalang on 04.12.2014.
 * *
 * begin of chat_message ,
 * message_id type zchat_message_id,
 * room_id type zchat_room_id,
 * erdat type dats,
 * erzet type tims,
 * ernam type ernam,
 * ernam_text type string,
 * text type string,
 * message_type type zchat_message_type,
 * end of chat_message .
 */
public class JChatMessage implements Serializable {
    @SerializedName("MESSAGE_ID")
    public String message_id;

    @SerializedName("ROOM_ID")
    public String room_id;

    @SerializedName("ERDAT")
    public String erdat;

    @SerializedName("ERZET")
    public String erzet;

    @SerializedName("ERNAM")
    public String ernam;

    @SerializedName("ERNAM_TEXT")
    public String ernam_text;

    @SerializedName("TEXT")
    public String text;

    @SerializedName("MESSAGE_TYPE")
    public String message_type;

    public String read;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JChatMessage that = (JChatMessage) o;

        if (message_id != null ? !message_id.equals(that.message_id) : that.message_id != null)
            return false;
        if (room_id != null ? !room_id.equals(that.room_id) : that.room_id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message_id != null ? message_id.hashCode() : 0;
        result = 31 * result + (room_id != null ? room_id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JChatMessage{" +
                "message_id='" + message_id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public Time getCreateDateTime() {
        Time ret = new Time(Time.getCurrentTimezone());
        try {
            int year = Integer.parseInt(erdat.substring(0, 4));
            int month = Integer.parseInt(erdat.substring(4, 6));
            int day = Integer.parseInt(erdat.substring(6, 8));
            int hours = Integer.parseInt(erzet.substring(0, 2));
            int minutes = Integer.parseInt(erzet.substring(2, 4));
            int seconds = Integer.parseInt(erzet.substring(4, 6));
            ret.set(seconds, minutes, hours, day, month - 1, year);
        } catch (Exception ignored) {
        }
        return ret;
    }
}
