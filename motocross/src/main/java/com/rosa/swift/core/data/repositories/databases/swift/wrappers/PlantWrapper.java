package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

public class PlantWrapper extends CursorWrapper {

    public PlantWrapper(Cursor cursor) {
        super(cursor);
    }

    public Tplst getPlant() {
        String code = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.CODE));
        String name = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.NAME));
        String address = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.ADDRESS));
        double longitude = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.LONGITUDE));
        double latitude = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.LATITUDE));
        boolean selected = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.SELECTED))
                .equals(Constants.SAP_TRUE_FLAG);
        String parentCode = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Plant.Columns.TPLST_CODE));

        return new Tplst(code, name, address, longitude, latitude, selected, parentCode);
    }


}
