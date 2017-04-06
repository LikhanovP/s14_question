package com.rosa.swift.core.network.json.sap.security;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

import java.util.List;


public class SendEventRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("EVENT")
    private SecurityTask mSecurityTask;

    @SerializedName("VALUE")
    private String mValue;

    public SendEventRequest(SecurityTask event, List<AppListItem> appListItems) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mSecurityTask = event;
        mValue = new Gson().toJson(appListItems, appListItems.getClass());
    }

}
