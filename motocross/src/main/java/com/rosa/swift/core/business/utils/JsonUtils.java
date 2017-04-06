package com.rosa.swift.core.business.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.core.network.responses.delivery.DeliveryResponse;

/**
 * Created by ipopov on 02.02.2017.
 */

public class JsonUtils {

    /**
     * @param message
     * @return
     */
    public static DeliveryResponse deserializeDelivery(String message) {
        DeliveryResponse deliveryRes = null;
        if (!TextUtils.isEmpty(message)) {
            try {
                deliveryRes = new Gson().fromJson(message, DeliveryResponse.class);
            } catch (Exception e) {
                Log.e("Не удалось преобразовать Json строку для доставки");
            }
        }
        return deliveryRes;
    }

}
