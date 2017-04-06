package com.rosa.swift.core.network.responses.structure;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Представляет модель ответа сервера на запрос списка структур
 */
public class StructureListResponse {

    /**
     * Список структур
     */
    @SerializedName("STRUCTURE_UNITS")
    private List<StructureUnitResponse> mStructureUnits;

    /**
     * Возвращает список структур
     */
    public List<StructureUnitResponse> getStructureUnits() {
        return mStructureUnits;
    }

}
