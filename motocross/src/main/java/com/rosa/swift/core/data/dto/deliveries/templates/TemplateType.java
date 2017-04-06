package com.rosa.swift.core.data.dto.deliveries.templates;

import android.text.TextUtils;

public enum TemplateType {
    /**
     * Доставка в основном списке
     */
    DML("DML"),
    /**
     * Доставка в основном списке (вечер)
     */
    DMLS("DMLS"),
    /**
     * Подтверждение в осн. списке
     */
    DMC("DMC"),
    /**
     * Детали назначенной доставки
     */
    DAD("DAD"),
    /**
     * Доставка в списке комплектации
     */
    DCL("DCL"),
    /**
     * Детали завершенной доставки
     */
    DFD("DFD");

    private String mValue;

    public static TemplateType getType(String type) {
        if (!TextUtils.isEmpty(type)) {
            for (TemplateType templateType : TemplateType.values()) {
                if (type.equals(templateType.mValue)) {
                    return templateType;
                }
            }
        }
        return DML;
    }

    @Override
    public String toString() {
        return mValue;
    }

    TemplateType(String type) {
        this.mValue = type;
    }

}
