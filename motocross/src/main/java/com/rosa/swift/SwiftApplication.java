package com.rosa.swift;

import android.app.Application;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.rosa.motocross.BuildConfig;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class SwiftApplication extends Application {
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    //region App Version

    public static String getVersion() {
        return Integer.toString(BuildConfig.VERSION_CODE);
    }

    public static String getVersionString() {
        return String.format(Locale.getDefault(), "%s (%s)",
                BuildConfig.VERSION_NAME, getVersion());
    }

    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        initFabric();

        sApplication = this;
    }

    private void initFabric() {
        Fabric.with(this,
                new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build(),
                new Answers());
    }
}
