package com.rosa.swift.core.network.json.sap.cup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 24.04.2016.
 */
public class JCupGetPhotoOut {
    //cup_id type ztr_cup_id,
    @SerializedName("CUP_ID")
    public String cup_id;

    //photo_id type wb2_document_item,
    @SerializedName("PHOTO_ID")
    public String photo_id;

    //mPhoto type string,
    @SerializedName("PHOTO")
    public String photo;

    //filename type string,
    @SerializedName("FILENAME")
    public String filename;
}
