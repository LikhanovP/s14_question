package com.rosa.swift.core.network.json.sap.driverRecords;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by egorov on 06.12.2016.
 */

public class JDriverRecordInfo implements Serializable {

    @SerializedName("RECORD_ID")
    public String record_id;

    @SerializedName("RECORD_MARK")
    public String record_mark;

    @SerializedName("TKNUM")
    public String record_tknum;

    @SerializedName("ERDAT")
    public String record_erdat;

    @SerializedName("ERZET")
    public String record_erzet;

    @SerializedName("RECORD_TITLE")
    public String record_title;

    @SerializedName("RECORD_TEXT")
    public String record_text;
}
