package com.rosa.swift.core.network.json.sap.photoBase;

import com.google.gson.annotations.SerializedName;

public class JPhotoDocGetOut {

    @SerializedName("PHOTO")
    public String photo;

    @SerializedName("PHOTO_DOC_VIEW")
    public String photo_doc_view;

    @SerializedName("FILENAME")
    public String filename;


}