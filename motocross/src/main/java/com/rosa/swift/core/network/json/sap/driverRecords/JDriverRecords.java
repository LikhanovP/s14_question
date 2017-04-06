package com.rosa.swift.core.network.json.sap.driverRecords;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by egorov on 06.12.2016.
 */

public class JDriverRecords implements Serializable {

    public class JDriverRecord implements Serializable {

        @SerializedName("RECORD_ID")
        public String record_id;

        @SerializedName("RECORD_MARK")
        public String record_mark;

        @SerializedName("RECORD_TITLE")
        public String record_title;

        @SerializedName("ERDAT")
        public String record_erdat;

    }

    @SerializedName("DRIVER_RECORDS_INFO")
    public List<JDriverRecord> driver_records_info;

    public JDriverRecords() {
        driver_records_info = new ArrayList<JDriverRecord>();
    }
}