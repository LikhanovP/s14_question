package com.rosa.swift.core.network.responses.photosession;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;

import java.util.List;

public class CupViewsResponse {

    @SerializedName("CUP_VIEWS_INFO")
    public List<DriverSessionResponse.PhotoSessionTask> cup_view_info_list;

}
