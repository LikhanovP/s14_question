package com.rosa.swift.core.data.dto.quest;

import com.rosa.swift.core.data.dto.common.Warehouse;

public class StorageLocationAnswer {
    private Warehouse mWarehouse;
    private int mSelectedAnswer;

    public StorageLocationAnswer(Warehouse warehouse, int selectedAnswer) {
        mWarehouse = warehouse;
        mSelectedAnswer = selectedAnswer;
    }

    public Warehouse getWarehouse() {
        return mWarehouse;
    }

    public int getSelectedAnswer() {
        return mSelectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        mSelectedAnswer = selectedAnswer;
    }
}
