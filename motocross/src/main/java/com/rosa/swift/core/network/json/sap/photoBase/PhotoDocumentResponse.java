package com.rosa.swift.core.network.json.sap.photoBase;

import com.google.gson.annotations.SerializedName;

public class PhotoDocumentResponse {

    @SerializedName("PHOTO")
    private String mPhoto;

    @SerializedName("PHOTO_DOC_VIEW")
    private String mPhotoDocView;

    @SerializedName("FILENAME")
    private String mFilename;

    public String getPhoto() {
        return mPhoto;
    }

}
