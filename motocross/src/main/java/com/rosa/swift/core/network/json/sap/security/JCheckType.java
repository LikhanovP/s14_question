package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 14.05.2016.
 */
public enum JCheckType {
    @SerializedName("ROOT")
    Root,
    @SerializedName("EMULATOR")
    Emulator,
    @SerializedName("CLICKER")
    Clicker,
}
