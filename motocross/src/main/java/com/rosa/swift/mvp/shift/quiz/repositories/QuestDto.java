package com.rosa.swift.mvp.shift.quiz.repositories;

import android.text.TextUtils;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.Quest;

import java.io.Serializable;

public class QuestDto implements Serializable {

    private int mId;

    private String mText;

    private boolean mRequired;

    private String mWarehouse;

    public QuestDto(Quest quest) {
        mId = quest.getId();
        mText = quest.getText();
        mRequired = !TextUtils.isEmpty(quest.isRequired()) &&
                quest.isRequired().equals(Constants.SAP_TRUE_FLAG);
        mWarehouse = quest.getWarehouse();
    }

    public int getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public boolean isRequired() {
        return mRequired;
    }

    public String getWarehouse() {
        return mWarehouse;
    }

}
