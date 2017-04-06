package com.rosa.swift.core.network.responses.photosession;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.business.utils.Constants;

public class CupStatusResponse {

    @SerializedName("CURRENT_DATE")
    private String mCurrentDate;

    @SerializedName("CURRENT_TIME")
    private String mCurrentTime;

    @SerializedName("NEXT_CUP_DATE")
    private String mCupDate;

    @SerializedName("NEXT_CUP_TIME")
    private String mCupTime;

    @SerializedName("CUP_AVAILABLE_DAYS")
    private int mCupAvailableDays;

    @SerializedName("CUP_WAS_DELAYED")
    private String mCupWasDelayed;

    @SerializedName("CUP_AVAILABLE_NOW")
    private String mCupAvailableNow;

    public String getCurrentDate() {
        return mCurrentDate;
    }

    public String getCurrentTime() {
        return mCurrentTime;
    }

    public String getCupDate() {
        return mCupDate;
    }

    public String getCupTime() {
        return mCupTime;
    }

    public int getCupAvailableDays() {
        return mCupAvailableDays;
    }

    public boolean isCupWasDelayed() {
        return mCupWasDelayed != null && mCupWasDelayed.equals(Constants.SAP_TRUE_FLAG);
    }

    public String getCupAvailableNow() {
        return mCupAvailableNow != null ? mCupAvailableNow : "";
    }
}
