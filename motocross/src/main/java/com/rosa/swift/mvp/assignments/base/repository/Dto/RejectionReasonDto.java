package com.rosa.swift.mvp.assignments.base.repository.Dto;

import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.RejectionReason;

public class RejectionReasonDto {

    private String mCode;

    private String mText;

    public String getCode() {
        return mCode;
    }

    public String getText() {
        return mText;
    }

    public RejectionReasonDto(RejectionReason reason) {
        mCode = reason.getCode();
        mText = reason.getText();
    }

    @Override
    public String toString() {
        return mText;
    }

}