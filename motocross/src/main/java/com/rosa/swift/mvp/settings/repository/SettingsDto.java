package com.rosa.swift.mvp.settings.repository;

import com.rosa.swift.core.data.dto.settings.Settings;

public class SettingsDto {

    private String mDriver;

    private String mPassword;

    private String mServer;

    private String mPhone;

    public SettingsDto(Settings settings) {
        mDriver = settings.getCall();
        mPassword = settings.getPassword();
        mServer = settings.getServer();
        mPhone = settings.getPhone();
    }

    public String getCall() {
        return mDriver;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getServer() {
        return mServer;
    }

    public String getPhone() {
        return mPhone;
    }

}
