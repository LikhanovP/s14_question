package com.rosa.swift.core.network.requests.terminal;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;

public class RegisterPaymentRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    @SerializedName("DEVICE")
    private String mDeviceAddress;

    @SerializedName("AMOUNT")
    private String mAmount;

    @SerializedName("CASHIER_CODE")
    private int mCashierCode;

    public RegisterPaymentRequest(String deliveryNumber, String deviceAddress, String amount, int cashierCode) {
        mDeliveryNumber = deliveryNumber;
        mDeviceAddress = deviceAddress;
        mAmount = amount;
        mCashierCode = cashierCode;
        mSessionId = DataRepository.getInstance().getSessionId();
    }

}
