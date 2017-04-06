package com.rosa.swift.core.network.json.sap.queue;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.business.Session;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.DataRepository;

public class QueueRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("LAST_QUEUE_ID")
    private long mLastQueueId;

    @SerializedName("TMPL_DATE")
    private String mTemplateDate;

    @SerializedName("TMPL_TIME")
    private String mTemplateTime;

    @SerializedName("LC_ONLY")
    private String mLoginOnly;

    public QueueRequest(Session session) throws NullPointerException {
        if (session == null) {
            throw new NullPointerException();
        }

        mSessionId = session.getSessionId();
        mLoginOnly = session.isLoginOnly() ? Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG;
        mLastQueueId = session.getLastMessageId();
        mTemplateDate = session.getLastTemplateDate();
        mTemplateTime = session.getLastTemplateTime();
    }

}
