package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

public enum SecurityTask {

    @SerializedName("CHECK_RESULT")
    CheckResult,

    @SerializedName("APP_LIST")
    AppList,

    @SerializedName("PACKAGE_LIST")
    PackageList

}
