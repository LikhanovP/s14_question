package com.rosa.swift.core.business;

import com.rosa.swift.CrashlyticsConfigurator;
import com.rosa.swift.core.network.json.sap.userdata.UserDataResponse;

public class Session {

    private String mSessionId;

    private String mTypeTransport;

    private boolean mLoginOnly;

    private long mLastMessageId;

    private String mLastTemplateDate;

    private String mLastTemplateTime;

    public Session(UserDataResponse userData, boolean loginOnly) {
        mSessionId = userData.getSessionId();
        mTypeTransport = userData.getTypeTransport();
        mLoginOnly = loginOnly;

        CrashlyticsConfigurator.setSessionId(mSessionId);
    }

    public Session(String sessionId, String typeTransport, boolean loginOnly, long lastMessageId,
            String lastTemplateDate, String lastTemplateTime) {
        mSessionId = sessionId;
        mTypeTransport = typeTransport;
        mLoginOnly = loginOnly;
        mLastMessageId = lastMessageId;
        mLastTemplateDate = lastTemplateDate;
        mLastTemplateTime = lastTemplateTime;

        CrashlyticsConfigurator.setSessionId(mSessionId);
    }

    //region Getters

    public String getSessionId() {
        return mSessionId;
    }

    public String getTypeTransport() {
        return mTypeTransport;
    }

    public long getLastMessageId() {
        return mLastMessageId;
    }

    public String getLastTemplateDate() {
        return mLastTemplateDate;
    }

    public String getLastTemplateTime() {
        return mLastTemplateTime;
    }

    public boolean isLoginOnly() {
        return mLoginOnly;
    }

    //endregion

}
