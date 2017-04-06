package com.rosa.swift.core.network.json.sap.photoBase;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class PhotoDocumentRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("PHOTO_DOC_VIEW")
    private String mDocumentType;

    public PhotoDocumentRequest(String documentType) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mDocumentType = documentType;
    }

}
