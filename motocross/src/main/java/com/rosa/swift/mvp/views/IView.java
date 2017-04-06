package com.rosa.swift.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(SkipStrategy.class)
public interface IView extends MvpView {

    void showError(int stringId);

    void showMessage(int stringId);

    void showError(String message);

    void showMessage(String message);

    void showToast(String message);

    void showToast(int stringId);

    void showProgress();

    void hideProgress();

}
