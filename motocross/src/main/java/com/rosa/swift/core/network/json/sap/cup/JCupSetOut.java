package com.rosa.swift.core.network.json.sap.cup;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lvetoshkina on 20.04.2016.
 */
public class JCupSetOut implements Serializable {

    public class JCupSet implements Serializable {

        @SerializedName("CUP_ID")
        public String cup_id;

        @SerializedName("PHOTO_ID")
        public String photo_id;

        @SerializedName("AUDAT")
        public String audat;

        @SerializedName("AUZET")
        public String auzet;

        @SerializedName("CUP_MARK")
        public String cup_mark;

        @SerializedName("CUP_NOTE")
        public String cup_note;

        @SerializedName("CUP_VIEW_TITLE")
        public String cup_view_title;

        //photo_filename type string,
        @SerializedName("PHOTO_FILENAME")
        public String photo_filename;
    }

    @SerializedName("CUP_SET")
    public List<JCupSet> cup_set;

}