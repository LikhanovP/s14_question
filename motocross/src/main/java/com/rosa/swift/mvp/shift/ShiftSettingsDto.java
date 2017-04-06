package com.rosa.swift.mvp.shift;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.StringUtils;

public class ShiftSettingsDto {

    private int mIdleTimeInMilliseconds;

    private int mQueueNumber;

    public ShiftSettingsDto(int idleTime) {
        mIdleTimeInMilliseconds = idleTime > 0 ?
                idleTime * 60 * 3600 : Constants.DEFAULT_IDLE_TIME_IN_MILLISECONDS;
    }

    /**
     * Возвращает в секундах время простоя, после которого происходит выброс со смены
     */
    public int getIdleTimeInMilliseconds() {
        return mIdleTimeInMilliseconds;
    }

    public int getQueueNumber() {
        return mQueueNumber;
    }

    public void setDefaultQueueNumber() {
        mQueueNumber = Constants.DEFAULT_QUEUE_NUMBER;
    }

    public void setQueueNumber(String queueNumber) {
        try {
            mQueueNumber = Integer.parseInt(StringUtils.representAsNumber(queueNumber));
        } catch (Exception ignored) {
            mQueueNumber = Constants.DEFAULT_QUEUE_NUMBER;
        }

    }
}
