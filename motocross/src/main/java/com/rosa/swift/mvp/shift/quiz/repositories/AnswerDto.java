package com.rosa.swift.mvp.shift.quiz.repositories;

import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.Answer;

public class AnswerDto {

    private int mQuestId;

    private int mAnswerId;

    private String mText;

    public AnswerDto(Answer answer) {
        mQuestId = answer.getQuestId();
        mAnswerId = answer.getAnswerId();
        mText = answer.getText();
    }

    public int getQuestId() {
        return mQuestId;
    }

    public int getAnswerId() {
        return mAnswerId;
    }

    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mText != null ? mText : "";
    }

}
