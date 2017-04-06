package com.rosa.swift.mvp.settings;

import com.rosa.swift.core.data.dto.settings.Settings;

import java.util.HashSet;
import java.util.Set;

import static com.rosa.swift.mvp.settings.SettingsValidator.Fields.PHONE;

public class SettingsValidator {
    private static final String TAG = "SettingsValidator";

    public enum Fields {
        DRIVER_NAME, PASSWORD, PHONE, SERVER
    }

    public Set<Fields> findIncorrectFields(Settings settings) {
        Set<Fields> incorrectFields = new HashSet<>();

        if (!phoneIsValid(settings.getPhone())) incorrectFields.add(PHONE);

        return incorrectFields;
    }

    private boolean phoneIsValid(String phone) {
        return (phone.length() == 11 || phone.length() == 0);
    }
}
