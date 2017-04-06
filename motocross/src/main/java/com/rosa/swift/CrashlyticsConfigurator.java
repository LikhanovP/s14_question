package com.rosa.swift;

import com.crashlytics.android.Crashlytics;

public class CrashlyticsConfigurator {

    private CrashlyticsConfigurator() {
        throw new AssertionError("Can not instantiate class");
    }

    public static void setDriverName(String driver) {
        setString("DRIVER", driver);
    }

    public static void setSessionId(String sessionId) {
        setString("SESSION_ID", sessionId);
    }

    private static void setString(String key, String value) {
        if (isCrashlyticsEnabled())
            Crashlytics.setString(key, value);
    }

    private static boolean isCrashlyticsEnabled() {
        return Crashlytics.getInstance() != null;
    }
}
