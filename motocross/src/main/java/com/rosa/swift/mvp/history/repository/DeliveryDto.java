package com.rosa.swift.mvp.history.repository;


import android.os.Parcel;
import android.os.Parcelable;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;

import java.util.Calendar;

import static com.rosa.swift.core.business.utils.ParseUtils.calendarToStringByMask;

public class DeliveryDto implements Parcelable {
    private String DATE_MASK = "dd.MM.yyyy hh:mm";

    private String tkNum;
    private Calendar date = Calendar.getInstance();
    private String htmlDescription;

    public DeliveryDto(Delivery delivery) {
        tkNum = delivery.getNumber();
        htmlDescription = DataRepository.getInstance().getDeliveryDataHtml(
                TemplateType.DFD, delivery);
    }

    protected DeliveryDto(Parcel in) {
        DATE_MASK = in.readString();
        tkNum = in.readString();
        htmlDescription = in.readString();
    }

    public String getTkNum() {
        return tkNum;
    }

    public String getDate() {
        return calendarToStringByMask(date, DATE_MASK);
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    //region ============================== Parcelable ==============================

    public static final Creator<DeliveryDto> CREATOR = new Creator<DeliveryDto>() {
        @Override
        public DeliveryDto createFromParcel(Parcel in) {
            return new DeliveryDto(in);
        }

        @Override
        public DeliveryDto[] newArray(int size) {
            return new DeliveryDto[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DATE_MASK);
        dest.writeString(tkNum);
        dest.writeString(htmlDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //endregion
}
