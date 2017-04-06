package com.rosa.swift.core.network.json.sap.cup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by inurlikaev on 12.04.2016.
 */
public class JDriverCupPhoto {
    //session_id type shm_inst_name
    @SerializedName("SESSION_ID")
    public String session_id;

    //cup_session_id type ztr_cup_session,
    @SerializedName("CUP_SESSION_ID")
    public String cup_session_id;

    //cup_view_id type ztr_cup_view_id,
    @SerializedName("CUP_VIEW_ID")
    public String cup_view_id;

    //mPhoto type string,
    @SerializedName("PHOTO")
    public String photo;

    //photo_filename type bds_propva,
    @SerializedName("PHOTO_FILENAME")
    public String photo_filename;
}
