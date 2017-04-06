package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 29.04.2016.
 */
public class JCheckResult {
    //check_type type text255,
    @SerializedName("CHECK_TYPE")
    public JCheckType checkType;

    //check_result type text255,
    @SerializedName("CHECK_RESULT")
    public String checkResult;

    public JCheckResult(JCheckType type, String result) {
        checkType = type;
        checkResult = result;
    }
}
