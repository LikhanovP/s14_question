package com.rosa.swift.mvp.settings.repository;

import com.rosa.swift.core.data.dto.settings.Settings;

import io.reactivex.Completable;

public interface ISettingsRepository {

    Completable saveSetting(Settings settings);

    Settings getSettings();

}
