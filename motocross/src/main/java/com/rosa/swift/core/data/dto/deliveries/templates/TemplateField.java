package com.rosa.swift.core.data.dto.deliveries.templates;

public class TemplateField {

    /**
     * Псевдоним поля класса
     */
    private String mName;

    /**
     * Имя поля класс
     */
    private String mField;

    /**
     * Категория поля
     */
    private TemplateFieldCategory mCategory;

    public String getName() {
        return mName;
    }

    public String getField() {
        return mField;
    }

    public TemplateFieldCategory getCategory() {
        return mCategory;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Category: %s, Field %s",
                mName, mCategory, mField);
    }

    TemplateField(String name, TemplateFieldCategory category, String field) {
        mName = name;
        mField = field;
        mCategory = category;
    }

}
