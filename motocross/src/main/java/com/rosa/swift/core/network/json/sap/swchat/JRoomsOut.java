package com.rosa.swift.core.network.json.sap.swchat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yalang on 04.12.2014.
 */
public class JRoomsOut implements Serializable {

    @SerializedName("ROOMS")
    public List<JRoomInfo> roomsList = new ArrayList<JRoomInfo>();

}
