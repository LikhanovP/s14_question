package com.rosa.swift.core.network.json.sap.waisting;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yalang on 05.05.14.
 */
public class JWaistOut {
    /*
        begin of waist_out,
        waist_begann type flag,
        error_message type string,
        end of waist_out .
        */
    @SerializedName("ERROR_MESSAGE")
    public String err;

    @SerializedName("WAIST_BEGANN")
    public String succ;
}
