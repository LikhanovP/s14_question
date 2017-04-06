package com.rosa.swift.core.network.json.sap.cup;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvetoshkina on 19.04.2016.
 */
public class JCupSetInfoOut implements Serializable {

    public class JCupSetInfo implements Serializable {

        @SerializedName("CUP_ID")
        public String cup_id;

        @SerializedName("CUP_MARK")
        public String cup_mark;

        @SerializedName("AUDAT")
        public String cup_audat;

    }

    @SerializedName("CUP_SETS_INFO")
    public List<JCupSetInfo> cup_sets_info;

    public JCupSetInfoOut() {
        cup_sets_info = new ArrayList<JCupSetInfo>();
    }

}
