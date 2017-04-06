package com.rosa.swift.core.network.json.sap.checkforupdates;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 12.08.13.
 */
@SuppressWarnings("unused")
public class JCheckForUpdatesOut {
    //VERSION_CODE TYPE INT4,
    @SerializedName("VERSION_CODE")
    public int versionCode;

    //UPDATE_URL TYPE STRING,
    @SerializedName("UPDATE_URL")
    public String updateURL;
}
