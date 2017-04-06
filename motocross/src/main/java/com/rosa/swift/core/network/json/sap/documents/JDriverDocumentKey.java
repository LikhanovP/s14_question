package com.rosa.swift.core.network.json.sap.documents;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 31.05.2016.
 */
public class JDriverDocumentKey {
    //DOC_KEY	Тип	TEXT255
    @SerializedName("DOC_KEY")
    public String documentKey;

    //TKNUM	Тип	TKNUM
    @SerializedName("TKNUM")
    public String tknum;

    //DOC_TYPE	Тип	CHAR40
    @SerializedName("DOC_TYPE")
    public String documentType;
}
