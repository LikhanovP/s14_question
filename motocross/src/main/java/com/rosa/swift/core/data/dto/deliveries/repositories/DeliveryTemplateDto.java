package com.rosa.swift.core.data.dto.deliveries.repositories;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateField;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateFieldCollection;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.network.json.sap.common.JDeliveryTemplate;

import java.lang.reflect.Field;
import java.util.Date;

public class DeliveryTemplateDto {

    private String mText;

    private String mEveningTime;

    private Date mLastUpdated;

    private TemplateType mType;

    public TemplateType getType() {
        return mType;
    }

    public String getDeliveryDataHtml(Delivery delivery) {
        String deliveryText = "";
        if (delivery != null) {
            //TODO: 30.03.2017 переделать логику работы с шаблонами в целом, сейчас на костылях
            DeliveryTemplateDto template = this;
            //если задан шаблон для вечерних доставок и время доставки меньше,
            //чем определенное для вечерней доставки, то используем основной шаблон для доставок
            if (mType == TemplateType.DMLS && !TextUtils.isEmpty(mEveningTime) &&
                    !template.isEveningDelivery(delivery)) {
                template = DataRepository.getInstance().getDeliveryTemplateByType(TemplateType.DML);
            }
            //если шаблон найден, то заполним его по заданной заявке
            deliveryText = template != null ? template.fillTemplate(delivery) : null;
            if (TextUtils.isEmpty(deliveryText)) {
                return delivery.getNumber();
            }
        }

        return deliveryText;
    }

    @SuppressLint("DefaultLocale")
    private String getValue(TemplateField templateField, Delivery delivery) {
        try {
            //TODO: 2017.02.02 оптимизировать, не пробегать по всей коллекции полей, а только по заданным в шаблоне
            Field field = delivery.getClass().getDeclaredField(templateField.getField());
            field.setAccessible(true);
            switch (templateField.getCategory()) {
                case Default:
                    Object text = field.get(delivery);
                    return text != null && !TextUtils.isEmpty(text.toString()) ?
                            text.toString() : null;
                case Currency:
                    double currency = field.getDouble(delivery);
                    return currency != 0.0 ? String.format("%.2f руб.", currency) : null;
                case Weight:
                    double weight = field.getDouble(delivery);
                    return weight != 0.0 ? String.format("%.2f кг.", weight) : null;
                case Volume:
                    double volume = field.getDouble(delivery);
                    return volume != 0.0 ? String.format("%.2f м3.", volume) : null;
                case Icon:
                    return field.getBoolean(delivery) ? "Да" : null;
                case Phone:
                    Object phoneText = field.get(delivery);
                    String phone = phoneText != null ? phoneText.toString() : null;
                    return TextUtils.isEmpty(phone) ? null :
                            String.format("<a href='tel:%1$s'>%1$s</a>", phone);
                default:
                    return null;
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    private boolean isEveningDelivery(Delivery delivery) {
        if (delivery != null && !TextUtils.isEmpty(mEveningTime)) {
            try {
                Date deliveryStart = delivery.getStartDate();
                int eveningHour = Integer.parseInt(mEveningTime.substring(0, 2));
                int eveningMinute = Integer.parseInt(mEveningTime.substring(1, 2));
                int deliveryHour = deliveryStart.getHours();
                int deliveryMinute = deliveryStart.getMinutes();
                return (deliveryHour > eveningHour) || (deliveryHour == eveningHour &&
                        deliveryMinute >= eveningMinute);
            } catch (Exception exception) {
                Log.e("Ошибка получения времени для вечерней заявки");
            }
        }
        return false;
    }

    private String fillTemplate(Delivery delivery) {
        String text = mText;
        if (delivery != null && TextUtils.isEmpty(text)) return "";
        for (TemplateField field : TemplateFieldCollection.getInstance(delivery)) {
            String value = getValue(field, delivery);
            try {
                String name = field.getName();
                if (value != null) {
                    text = text.replace("{" + name + "}", value);
                } else {
                    String pattern = "\\[[^\\[]*\\{" + name + "\\}[^\\]]*\\](<br/>)?";
                    text = text.replaceAll(pattern, "");
                }
            } catch (Exception ignored) {
            }
        }
        text = text.replace("[", "");
        text = text.replace("]", "");
        return text;
    }

    @Override
    public String toString() {
        return mType.toString();
    }

    //region Comparing

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DeliveryTemplateDto template = (DeliveryTemplateDto) object;
        return getType() == template.getType();
    }

    @Override
    public int hashCode() {
        int result = mText != null ? mText.hashCode() : 0;
        result = 31 * result + (mEveningTime != null ? mEveningTime.hashCode() : 0);
        result = 31 * result + (mLastUpdated != null ? mLastUpdated.hashCode() : 0);
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        return result;
    }

    //endregion

    public DeliveryTemplateDto(String template, TemplateType type, String time, Date lastUpdated) {
        mType = type;
        mText = template;
        mEveningTime = time;
        mLastUpdated = lastUpdated;
    }

    public DeliveryTemplateDto(String deliveryTemplateJsonString) throws Exception {
        JDeliveryTemplate deliveryTemplate = new Gson().fromJson(deliveryTemplateJsonString,
                JDeliveryTemplate.class);

        mType = TemplateType.getType(deliveryTemplate.id);

        String sapDate = deliveryTemplate.ldate;
        String sapTime = deliveryTemplate.ltime;
        if (!TextUtils.isEmpty(sapDate) && !TextUtils.isEmpty(sapTime)) {
            mLastUpdated = StringUtils.getDateFromSapDateTime(sapDate, sapTime);

            //TODO: ipopov 29.03.2017 убрать эту логику в другое более подходящее место
            Date oldDate = StringUtils.getDateFromSapDateTime(
                    DataRepository.getInstance().getLastTemplateDate(),
                    DataRepository.getInstance().getLastTemplateTime());
            if (mLastUpdated != null && oldDate != null &&
                    mLastUpdated.getTime() < oldDate.getTime()) {
                DataRepository.getInstance().setLastTemplateDate(sapDate);
                DataRepository.getInstance().setLastTemplateTime(sapTime);
            }
        }

        StringBuilder text = new StringBuilder();
        for (String stringLine : deliveryTemplate.text) {
            if (text.length() != 0) {
                text.append("<br/>");
            }
            text.append(stringLine);
        }

        mText = text.toString();
        mEveningTime = deliveryTemplate.etime;
    }

}
