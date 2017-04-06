package com.rosa.swift.core.ui.base;

import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatFragment;

public class BaseFragment extends MvpAppCompatFragment {


    protected View mInflatedView;

    /**
     * Set inflated view in onCreateView()
     */
    protected void setInflatedView(View inflatedView) {
        if (inflatedView == null) throw new IllegalStateException("Inflated view must not be null");
        mInflatedView = inflatedView;
    }

    protected ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @SuppressWarnings({"unchecked cast", "ConstantConditions"})
    protected <V extends View> V $(@IdRes int id) {
        return (V) mInflatedView.findViewById(id);
    }
}