package com.rosa.swift.core.network.json.sap.documents;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by inurlikaev on 30.05.2016.
 */
public class JDriverDocuments implements Serializable {
    public class JDriverDocument {
        //DOC_KEY	Тип	TEXT255
        @SerializedName("DOC_KEY")
        public String documentKey;

        //TKNUM	Тип	TKNUM
        @SerializedName("TKNUM")
        public String tknum;

        //DOC_TYPE	Тип	CHAR40
        @SerializedName("DOC_TYPE")
        public String documentType;

        //FILENAME	Тип	ZSTRING
        @SerializedName("FILENAME")
        public String fileName;

        //FILETYPE	Тип	ZSTRING
        @SerializedName("FILETYPE")
        public String fileType;

        //DESCRIPTION	Тип	ZSTRING
        @SerializedName("DESCRIPTION")
        public String description;
    }

    @SerializedName("DOCUMENTS_LIST")
    public List<JDriverDocument> documentsList;
}
