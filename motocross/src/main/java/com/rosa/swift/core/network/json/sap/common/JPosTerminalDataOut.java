package com.rosa.swift.core.network.json.sap.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yalang on 13.04.2015.
 */
public class JPosTerminalDataOut {

    @SerializedName("DEVICE")
    public String deviceAddress;

    @SerializedName("AMOUNT")
    public String amount;

}
