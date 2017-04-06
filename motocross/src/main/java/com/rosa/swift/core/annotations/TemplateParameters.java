package com.rosa.swift.core.annotations;

import com.rosa.swift.core.data.dto.deliveries.templates.TemplateFieldCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface TemplateParameters {

    String name();

    TemplateFieldCategory category();

}