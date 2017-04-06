package com.rosa.swift.mvp.assignments.base.repository.Dto;

import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;

public class DriverMessageDto {

    private String mType;

    private String mText;

    public DriverMessageDto(DriverSessionResponse.DriverMessage message) {
        mType = message.getId();
        mText = message.getText();
    }

    public String getText() {
        return mText;
    }

    public String getType() {
        return mType;
    }

    @Override
    public String toString() {
        return mText;
    }

}
