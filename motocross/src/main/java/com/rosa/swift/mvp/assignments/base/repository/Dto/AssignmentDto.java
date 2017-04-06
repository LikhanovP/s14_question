package com.rosa.swift.mvp.assignments.base.repository.Dto;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;

public class AssignmentDto {

    private String mNumber;

    private String mHtmlDescriptionForList;

    private String mHtmlDescriptionCurrent;

    public AssignmentDto(Delivery delivery) {
        if (delivery != null) {
            mNumber = delivery.getNumber();
            mHtmlDescriptionCurrent = DataRepository.getInstance().getDeliveryDataHtml(
                    TemplateType.DAD, delivery);
            mHtmlDescriptionForList = DataRepository.getInstance().getDeliveryDataHtml(
                    TemplateType.DML, delivery);
        }
    }

    public String getNumber() {
        return mNumber;
    }

    public String getHtmlDescriptionCurrent() {
        return mHtmlDescriptionCurrent;
    }

    public String getHtmlDescriptionForList() {
        return mHtmlDescriptionForList;
    }

}
