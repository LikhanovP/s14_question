package com.rosa.swift.core.ui.fragments;

/**
 * Created by yalang on 05.12.2014.
 */
public interface CabinetFragment {
    void refreshData();

    String getTitle();

    boolean getDrawerEnabled();

    String getPCabTag();

    String getBackStackName();
}
