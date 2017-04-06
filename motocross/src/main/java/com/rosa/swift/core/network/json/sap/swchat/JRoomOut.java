package com.rosa.swift.core.network.json.sap.swchat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yalang on 04.12.2014.
 */
public class JRoomOut implements Serializable {

    @SerializedName("ROOM")
    public JRoom room;

    @SerializedName("MESSAGES")
    public List<JChatMessage> messageList = new ArrayList<JChatMessage>();

}
