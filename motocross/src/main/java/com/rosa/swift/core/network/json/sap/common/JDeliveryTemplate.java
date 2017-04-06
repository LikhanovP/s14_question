package com.rosa.swift.core.network.json.sap.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yalang on 28.10.13.
 */
public class JDeliveryTemplate {

    @SerializedName("ID")
    public String id;

    @SerializedName("TEXT")
    public List<String> text;

    @SerializedName("LDATE")
    public String ldate;

    @SerializedName("LTIME")
    public String ltime;

    @SerializedName("ETIME")
    public String etime;

}
