package com.rosa.swift.core.network.json.sap.cup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 12.04.2016.
 */
public class JCupViewPhoto {
    //cup_view_id type ztr_cup_view_id,
    @SerializedName("CUP_VIEW_ID")
    public String cup_view_id;

    //cup_view_photo type string.
    @SerializedName("CUP_VIEW_PHOTO")
    public String cup_view_photo;

    //cup_view_photo_docid type text255,
    @SerializedName("CUP_VIEW_PHOTO_DOCID")
    public String view_photo_docid;
}
