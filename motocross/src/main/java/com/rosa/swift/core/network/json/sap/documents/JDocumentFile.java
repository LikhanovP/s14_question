package com.rosa.swift.core.network.json.sap.documents;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by inurlikaev on 01.06.2016.
 */
public class JDocumentFile implements Serializable {
    @SerializedName("DATA")
    public String data;

    @SerializedName("ERROR")
    public String error;
}
