package com.rosa.swift.core.data.dto.common;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.network.responses.structure.StructureListResponse;
import com.rosa.swift.core.network.responses.structure.StructureUnitResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Представляет коллекцию мест планирования транспорта
 */
public class TplstCollection extends ArrayList<Tplst> {

    /**
     * Код места планирования
     */
    private String mMainCode;

    /**
     * Возвращает код родительского места планирования
     */
    public String getMainCode() {
        return mMainCode;
    }

    /**
     * Возвращает список имен элементов текущей коллекции
     */
    public List<String> getNames() {
        ArrayList<String> result = new ArrayList<>();
        for (Tplst tplst : this) {
            result.add(tplst.getName());
        }
        return result;
    }

    public TplstCollection() {
        mMainCode = "";
    }

    public TplstCollection(String mainCode, List<Tplst> tplstList) {
        super(tplstList);
        mMainCode = mainCode;
    }

    public TplstCollection(String jsonString) {
        this(jsonString, "");
    }

    public TplstCollection(String jsonString, String mainCode) {
        mMainCode = mainCode;
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                StructureListResponse response = new Gson().fromJson(String.format(
                        Locale.getDefault(), "{ STRUCTURE_UNITS : %s}", jsonString),
                        StructureListResponse.class);
                if (response != null) {
                    for (StructureUnitResponse structure : response.getStructureUnits()) {
                        add(new Tplst(structure, mainCode, true));
                    }
                }
            } catch (Exception exception) {
                Log.e("Failed serializing structure list");
            }
        }
    }

}
