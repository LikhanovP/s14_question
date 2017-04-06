package com.rosa.swift.mvp.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rosa.motocross.R;
import com.rosa.swift.mvp.settings.repository.SettingsDto;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class SettingsDialog extends DialogFragment implements ISettingsView {
    private static final String TAG = "SettingsView";

    private MvpDelegate mParentDelegate;
    private MvpDelegate<SettingsDialog> mMvpDelegate;

    @InjectPresenter
    SettingsPresenter mPresenter;

    private TextInputEditText mCallEdit;
    private TextInputEditText mPasswordEdit;
    private TextInputEditText mPhoneEdit;
    private TextInputEditText mServerEdit;
    private AppCompatButton mCancelButton;
    private AppCompatButton mSaveButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_settings, null);

        setUpViews(view);
        initPhoneEditText();
        initClickListeners();

        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(false)
                .create();
    }

    private void setUpViews(View root) {
        mCallEdit = (TextInputEditText) root.findViewById(R.id.driver_name_et);
        mPasswordEdit = (TextInputEditText) root.findViewById(R.id.password_et);
        mPhoneEdit = (TextInputEditText) root.findViewById(R.id.phone_et);
        mServerEdit = (TextInputEditText) root.findViewById(R.id.reserve_ip_et);
        mCancelButton = (AppCompatButton) root.findViewById(R.id.settings_cancel);
        mSaveButton = (AppCompatButton) root.findViewById(R.id.settings_save);
    }

    private void initClickListeners() {
        mCancelButton.setOnClickListener(view -> mPresenter.onCancelClick());
        mSaveButton.setOnClickListener(view -> onSaveClick());
    }

    private void initPhoneEditText() {
        MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        FormatWatcher watcher = new MaskFormatWatcher(mask);
        watcher.installOn(mPhoneEdit);
    }

    private void onSaveClick() {
        String driverCall = mCallEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        String phone = mPhoneEdit.getText().toString().trim();
        String server = mServerEdit.getText().toString().trim();

        mPresenter.onSaveClick(driverCall, password, phone, server);
    }

    //region ============================== Lifecycle ==============================

    @Override
    public void onDetach() {
        super.onDetach();
        getMvpDelegate().onSaveInstanceState();
        getMvpDelegate().onDetach();
    }

    //endregion

    //region ============================== ISettingsView ==============================

    @Override
    public void showSettings(SettingsDto settings) {
        mCallEdit.setText(settings.getCall());
        mPasswordEdit.setText(settings.getPassword());
        mPhoneEdit.setText(settings.getPhone());
        mServerEdit.setText(settings.getServer());
    }

    @Override
    public void close() {
        dismiss();
    }

    //endregion

    //region ============================== Moxy ==============================

    public void init(MvpDelegate parentDelegate) {
        mParentDelegate = parentDelegate;
    }

    public MvpDelegate<SettingsDialog> getMvpDelegate() {
        if (mMvpDelegate != null) {
            return mMvpDelegate;
        }

        mMvpDelegate = new MvpDelegate<>(this);
        mMvpDelegate.setParentDelegate(mParentDelegate, String.valueOf(getId()));
        return mMvpDelegate;
    }

    //endregion
}
