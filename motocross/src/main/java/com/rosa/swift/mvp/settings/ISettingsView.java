package com.rosa.swift.mvp.settings;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.rosa.swift.mvp.settings.repository.SettingsDto;

/**
 * Created by mteterin on 15.02.2017.
 */

@StateStrategyType(SkipStrategy.class)
public interface ISettingsView extends MvpView {

    void showSettings(SettingsDto settings);

    void close();

}
