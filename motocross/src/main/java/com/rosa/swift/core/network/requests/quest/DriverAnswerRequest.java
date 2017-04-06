package com.rosa.swift.core.network.requests.quest;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.quest.StorageLocationAnswer;

import java.util.List;

public class DriverAnswerRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("QUEST_ID")
    private int mQuestId;

    @SerializedName("ANSW_ID")
    private int mAnswerId;

    @SerializedName("TKNUM")
    private String mDeliveryNumber;

    @SerializedName("ANS_BY_LGORT")
    private AnswerStorageLocationRequest mAnswerStorageLocationRequest;

    public DriverAnswerRequest(int questId, String deliveryNumber, List<StorageLocationAnswer> answers) {
        mSessionId = DataRepository.getInstance().getSessionId();
        mQuestId = questId;
        mDeliveryNumber = deliveryNumber;
        mAnswerStorageLocationRequest = new AnswerStorageLocationRequest();
        if (!TextUtils.isEmpty(deliveryNumber) || answers.get(0).getWarehouse() == null) {
            mAnswerId = answers.get(0).getSelectedAnswer();
        } else {
            for (int i = 0; i < answers.size(); i++) {
                int selectedAnswer = answers.get(i).getSelectedAnswer();
                String warehouseCode = answers.get(i).getWarehouse().getCode();
                switch (i) {
                    case 0:
                        mAnswerStorageLocationRequest.aid0 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort0 = warehouseCode;
                    case 1:
                        mAnswerStorageLocationRequest.aid1 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort1 = warehouseCode;
                        break;
                    case 2:
                        mAnswerStorageLocationRequest.aid2 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort2 = warehouseCode;
                        break;
                    case 3:
                        mAnswerStorageLocationRequest.aid3 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort3 = warehouseCode;
                        break;
                    case 4:
                        mAnswerStorageLocationRequest.aid4 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort4 = warehouseCode;
                        break;
                    case 5:
                        mAnswerStorageLocationRequest.aid5 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort5 = warehouseCode;
                        break;
                    case 6:
                        mAnswerStorageLocationRequest.aid6 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort6 = warehouseCode;
                        break;
                    case 7:
                        mAnswerStorageLocationRequest.aid7 = selectedAnswer;
                        mAnswerStorageLocationRequest.lgort7 = warehouseCode;
                        break;
                }
            }
        }
    }
}

