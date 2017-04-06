package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

public class AppListItem {

    @SerializedName("APP_NAME")
    private String mAppName;

    @SerializedName("PACKAGE_NAME")
    private String mPackageName;

    public AppListItem(String appName, String packageName) {
        mAppName = appName;
        mPackageName = packageName;
    }

}
