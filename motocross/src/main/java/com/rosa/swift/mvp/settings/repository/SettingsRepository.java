package com.rosa.swift.mvp.settings.repository;

import android.support.annotation.NonNull;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.settings.Settings;

import io.reactivex.Completable;

public class SettingsRepository implements ISettingsRepository {
    private static final String TAG = "SettingsRepository";

    private DataRepository mDataRepository = DataRepository.getInstance();

    @Override
    public Completable saveSetting(@NonNull Settings settings) {
        return Completable.fromRunnable(() -> {
            mDataRepository.setSettings(settings);
            mDataRepository.updateCustomServer();
        });
    }

    @Override
    public Settings getSettings() {
        return DataRepository.getInstance().getSettings();
    }
}
