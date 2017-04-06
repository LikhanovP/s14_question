package com.rosa.swift.core.network.json.sap.photoBase;

import com.google.gson.annotations.SerializedName;

/**
 * Created by egorov on 04.10.2016.
 */

public class JPhotoGetDocIn {

    @SerializedName("SESSION_ID")
    public String session_id;

    @SerializedName("PHOTO_DOC_VIEW")
    public String photo_doc_view;
}
