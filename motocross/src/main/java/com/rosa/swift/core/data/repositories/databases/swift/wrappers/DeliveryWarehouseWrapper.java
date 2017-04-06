package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.dto.common.DeliveryWarehouse;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

public class DeliveryWarehouseWrapper extends CursorWrapper {
    public DeliveryWarehouseWrapper(Cursor cursor) {
        super(cursor);
    }

    public DeliveryWarehouse getDeliveryWarehouse() {
        try {
            String number = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.DELIVERY_NUMBER));
            String code = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.CODE));
            String name = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.NAME));
            return new DeliveryWarehouse(number, code, name);
        } catch (Exception exception) {
            Log.e("Ошибка считывания склада доставки из базы данных", exception);
            return null;
        }
    }
}
