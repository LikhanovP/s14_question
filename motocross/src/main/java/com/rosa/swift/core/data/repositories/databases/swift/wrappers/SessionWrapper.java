package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rosa.swift.core.business.Session;

public class SessionWrapper extends CursorWrapper {

    public SessionWrapper(Cursor cursor) {
        super(cursor);
    }

    public Session getSession() {
       /* String sessionId = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Session.Columns.SESSION_ID));
        String transportType = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Session.Columns.TYPE_TRANSPORT));
        long lastQueueId = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.Session.Columns.LAST_QUEUE_ID));
        boolean isLoginOnly = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Session.Columns.IS_LOGIN_ONLY))
                .equals(ConstantsManager.SAP_TRUE_FLAG);

        return new Session(sessionId, transportType, lastQueueId, isLoginOnly);*/
       return null;
    }


}
