package com.rosa.swift.core.business;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.common.JMessage;

import java.io.Serializable;

public class Message implements Serializable {

    @SerializedName("id")
    private String mId;

    @SerializedName("type")
    private MessageType mType;

    @SerializedName("message")
    private String mMessage;

    public String getId() {
        return mId;
    }

    public MessageType getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return String.format("Id: %s, Type: %s, Text: %s", mId, mType, mMessage);
    }

    //region Comparing

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Message message = (Message) object;

        return mId != null ? mId.equals(message.mId) : message.mId == null &&
                mType == message.mType && (mMessage != null ?
                mMessage.equals(message.mMessage) : message.mMessage == null);
    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        return result;
    }

    //endregion

    public Message(MessageType type) {
        mId = "";
        mType = type;
        mMessage = "";
    }

    public Message(MessageType type, String message) {
        mId = "";
        mType = type;
        mMessage = message;
    }

    public Message(JMessage message) {
        mId = message.getId();
        mType = MessageType.fromString(message.getType());
        mMessage = message.getMessage();
    }

}
