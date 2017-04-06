package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

import java.util.Date;


public class DeliveryTemplateWrapper extends CursorWrapper {

    public DeliveryTemplateWrapper(Cursor cursor) {
        super(cursor);
    }

    public DeliveryTemplateDto getDeliveryTemplate() {
        String text = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.TEXT));
        String type = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.TYPE));
        String eveningTime = getString(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.EVENING_TIME));
        long dateInMillis = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.LAST_UPDATED));

        Date lastUpdated = new Date(dateInMillis);
        TemplateType templateType = TemplateType.getType(type);

        return new DeliveryTemplateDto(text, templateType, eveningTime, lastUpdated);
    }

}
