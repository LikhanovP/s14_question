package com.rosa.swift.core.data.dto.deliveries.templates;

import com.rosa.swift.core.annotations.TemplateParameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class TemplateFieldCollection {

    private static ArrayList<TemplateField> sTemplateFields = null;

    public static ArrayList<TemplateField> getInstance(Object object) {
        if (sTemplateFields == null) {
            sTemplateFields = new ArrayList<>();
            generateFields(object);
        }
        return sTemplateFields;
    }

    private static void generateFields(Object object) {
        if (object != null) {
            Field[] fields = object.getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    Annotation annotation = field.getAnnotation(TemplateParameters.class);
                    if (annotation != null) {
                        TemplateParameters parameters = (TemplateParameters) annotation;
                        sTemplateFields.add(new TemplateField(
                                parameters.name(),
                                parameters.category(),
                                field.getName()));
                    }
                }
            }
        }
    }

    private TemplateFieldCollection() {
    }

}
