package com.rosa.swift.mvp.shift.gate.repository;

import com.rosa.swift.core.business.utils.Constants;

public class GateKeeperDto {

    private boolean mEnabled;

    public GateKeeperDto(String enabled) {
        mEnabled = enabled != null && enabled.equals(Constants.SAP_TRUE_FLAG);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

}
