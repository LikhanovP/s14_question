package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.dto.common.DeliveryAddress;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

public class DeliveryAddressWrapper extends CursorWrapper {
    public DeliveryAddressWrapper(Cursor cursor) {
        super(cursor);
    }

    public DeliveryAddress getDeliveryAddress() {
        try {
            DeliveryAddress address = new DeliveryAddress();
            address.setNumber(getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.DELIVERY_NUMBER)));
            address.setAddressName(getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.ADDRESS)));
            address.setLongitude(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.LONGITUDE)));
            address.setLatitude(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.LATITUDE)));
            address.setPosition(getInt(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.POSITION)));
            address.setContactName(getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.CONTACT_NAME)));
            address.setPhone(getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.CONTACT_PHONE)));
            return address;
        } catch (Exception exception) {
            Log.e("Ошибка считывания адреса доставки из базы данных", exception);
            return null;
        }
    }

}
