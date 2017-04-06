package com.rosa.swift.core.network.requests.logon;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.network.json.sap.security.JCheckResult;

import java.util.List;

/**
 * Created by yalang on 01.08.13.
 */
public class UserDataRequest {

    @SerializedName("LOGIN")
    private String mLogin;

    @SerializedName("PASSWORD")
    private String mPassword;

    @SerializedName("VERSION")
    private String mVersion;

    @SerializedName("TPLST")
    private String mTplst;

    @SerializedName("CHECK_RESULTS")
    private List<JCheckResult> mCheckResults;

    public UserDataRequest(String login, String password, String tplst, List<JCheckResult> checkResults) {
        mLogin = login;
        mPassword = password;
        mTplst = tplst;
        mCheckResults = checkResults;
    }

}
