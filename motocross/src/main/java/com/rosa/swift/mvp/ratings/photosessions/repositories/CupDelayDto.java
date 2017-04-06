package com.rosa.swift.mvp.ratings.photosessions.repositories;

import android.text.TextUtils;

import com.rosa.swift.core.business.utils.Constants;

public class CupDelayDto {

    private boolean mDelayed;

    private int mDaysToDelay;

    public CupDelayDto(String delayed, int daysToDelay) {
        mDelayed = !TextUtils.isEmpty(delayed) && delayed.equals(Constants.SAP_TRUE_FLAG);
        mDaysToDelay = daysToDelay;
    }

    public boolean isDelayed() {
        return mDelayed;
    }

    public int getDaysToDelay() {
        return mDaysToDelay;
    }

}
