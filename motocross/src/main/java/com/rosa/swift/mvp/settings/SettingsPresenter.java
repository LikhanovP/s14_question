package com.rosa.swift.mvp.settings;

import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.data.dto.settings.Settings;
import com.rosa.swift.mvp.settings.repository.ISettingsRepository;
import com.rosa.swift.mvp.settings.repository.SettingsDto;
import com.rosa.swift.mvp.settings.repository.SettingsRepository;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SettingsPresenter extends MvpPresenter<ISettingsView> {
    private static final String TAG = "SettingsPresenter";

    private ISettingsRepository mRepository = new SettingsRepository();
    private SettingsValidator mSettingsValidator = new SettingsValidator();

    //region ============================== Lifecycle ==============================

    @Override
    public void attachView(ISettingsView view) {
        super.attachView(view);
        loadSettings();
    }

    //endregion

    private void loadSettings() {
        Settings settings = mRepository.getSettings();
        if (settings != null) {
            getViewState().showSettings(new SettingsDto(settings));
        }
    }

    public void onSaveClick(String driver, String password, String phone, String server) {
        Settings settings = new Settings(driver, password, server, preparePhoneForSaving(phone));

        Set<SettingsValidator.Fields> incorrectFields = mSettingsValidator.findIncorrectFields(settings);

        if (!incorrectFields.isEmpty())
            handleIncorrectInput(incorrectFields);
        else
            saveSettingsAndCloseView(settings);
    }

    private void saveSettingsAndCloseView(Settings settings) {
        mRepository.saveSetting(settings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getViewState().close());
    }

    private String preparePhoneForSaving(String phoneFromView) {
        String phoneDigitsOnly = phoneFromView.replaceAll("\\W", "");

        return phoneDigitsOnly.length() != 1 ? phoneDigitsOnly : "";
    }

    private void handleIncorrectInput(Set<SettingsValidator.Fields> incorrectFields) {

        for (SettingsValidator.Fields incorrectField : incorrectFields) {
            switch (incorrectField) {
                case DRIVER_NAME:
                    break;
                case PASSWORD:
                    break;
                case PHONE:
                    //// FIXME: 16.02.2017 делегировать показ тоста MainActivity через root презентер
                    String message = "Неправильный номер телефона";
                    Toast.makeText(SwiftApplication.getApplication(), message, Toast.LENGTH_LONG).show();
                    break;
                case SERVER:
                    break;
            }
        }
    }

    public void onCancelClick() {
        getViewState().close();
    }
}
