package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

public class TransportLocationWrapper extends CursorWrapper {

    public TransportLocationWrapper(Cursor cursor) {
        super(cursor);
    }

    public DeliveryLocation getTransportLocation() {
        String id = getString(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSPORTATION_ID));
        String status = getString(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSPORTATION_STATUS));
        double longitude = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.LONGITUDE));
        double latitude = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.LATITUDE));
        long date = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.TIME));
        boolean transferred = getString(getColumnIndex(SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSFERRED))
                .equals(Constants.SAP_TRUE_FLAG);

        return new DeliveryLocation(id, status, longitude, latitude, date, transferred);
    }
}