package com.rosa.swift.core.data.dto.settings;

public class Settings {

    private String mDriver;

    private String mPassword;

    private String mServer;

    private String mPhone;

    public Settings(String driver, String password, String server, String phone) {
        mDriver = driver;
        mPassword = password;
        mServer = server;
        mPhone = phone;
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
