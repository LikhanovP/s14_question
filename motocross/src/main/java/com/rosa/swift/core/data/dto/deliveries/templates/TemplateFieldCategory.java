package com.rosa.swift.core.data.dto.deliveries.templates;

public enum TemplateFieldCategory {
    Currency("Currency"),
    Weight("Weight"),
    Volume("Volume"),
    Icon("Icon"),
    Phone("Phone"),
    Default("Default");

    private String mValue;

    @Override
    public String toString() {
        return mValue;
    }

    TemplateFieldCategory(String value) {
        mValue = value;
    }
}
