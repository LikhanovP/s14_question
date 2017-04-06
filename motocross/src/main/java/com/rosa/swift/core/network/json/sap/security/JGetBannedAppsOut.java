package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by inurlikaev on 15.05.2016.
 */
public class JGetBannedAppsOut {
    @SerializedName("BANNED_APPS_LIST")
    public List<String> BannedAppsList;
}
