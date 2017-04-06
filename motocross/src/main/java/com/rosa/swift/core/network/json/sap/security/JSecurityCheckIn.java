package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by inurlikaev on 13.05.2016.
 */
public class JSecurityCheckIn {
    //session_id type shm_inst_name
    @SerializedName("SESSION_ID")
    public String session_id;

    @SerializedName("CHECK_RESULTS")
    public List<JCheckResult> checkResults;
}
