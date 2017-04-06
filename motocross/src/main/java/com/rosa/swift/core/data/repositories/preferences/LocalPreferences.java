package com.rosa.swift.core.data.repositories.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.CrashlyticsConfigurator;
import com.rosa.swift.core.business.Session;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.settings.ChatSettings;
import com.rosa.swift.core.data.dto.settings.ChatSettings.NotificationType;
import com.rosa.swift.core.data.dto.settings.Settings;
import com.rosa.swift.core.data.repositories.PreferencesRepository;

public class LocalPreferences implements PreferencesRepository {

    private static final String SETTINGS_CHAT_TYPE_NOTIFICATION = "SETTINGS_CHAT_TYPE_NOTIFICATION";
    private static final String SETTINGS_SELECTED_TOWN = "SETTINGS_SELECTED_TOWN";

    private static final String SETTINGS_DRIVER_CALL = "driverCall";
    private static final String SETTINGS_DRIVER_PASSWORD = "driverPwd";
    private static final String SETTINGS_PHONE_NUMBER = "phoneNumber";
    private static final String SETTINGS_SERVER_CUSTOM = "serverCustomAddr";

    private static final String SESSION_ID = "SESSION_ID";
    private static final String SESSION_TYPE_TRANSPORT = "SESSION_TYPE_TRANSPORT";
    private static final String SESSION_LOGIN_ONLY = "SESSION_LOGIN_ONLY";
    private static final String QUEUE_LAST_MESSAGE_ID = "QUEUE_LAST_MESSAGE_ID";
    private static final String QUEUE_LAST_TEMPLATE_DATE = "QUEUE_LAST_TEMPLATE_DATE";
    private static final String QUEUE_LAST_TEMPLATE_TIME = "QUEUE_LAST_TEMPLATE_TIME";

    private final SharedPreferences mSharedPreferences;

    public LocalPreferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //region Town

    public void saveSelectedTown(Tplst town) {
        if (town != null) {
            try {
                String json = new Gson().toJson(town);
                mSharedPreferences
                        .edit()
                        .putString(SETTINGS_SELECTED_TOWN, json)
                        .apply();
            } catch (Exception exception) {
                Log.e("Ошибка сериализации выбранного города", exception);
            }
        }
    }

    @Nullable
    public Tplst loadSelectedTown() {
        Tplst town = null;
        try {
            String json = mSharedPreferences.getString(SETTINGS_SELECTED_TOWN, null);
            if (!TextUtils.isEmpty(json)) {
                town = new Gson().fromJson(json, Tplst.class);
            }
        } catch (Exception exception) {
            Log.e("Ошибка десериализации выбранного города", exception);
        }
        return town;
    }

    //endregion

    //region Session

    @Override
    public void saveSession(Session session) throws NullPointerException {
        if (session == null) {
            throw new NullPointerException();
        }
        mSharedPreferences
                .edit()
                .putString(SESSION_ID, session.getSessionId())
                .putBoolean(SESSION_LOGIN_ONLY, session.isLoginOnly())
                .putString(SESSION_TYPE_TRANSPORT, session.getTypeTransport())
                .apply();
    }

    @Override
    public Session loadSession() {
        return new Session(
                mSharedPreferences.getString(SESSION_ID, null),
                mSharedPreferences.getString(SESSION_TYPE_TRANSPORT, null),
                mSharedPreferences.getBoolean(SESSION_LOGIN_ONLY, false),
                mSharedPreferences.getLong(QUEUE_LAST_MESSAGE_ID, Constants.QUEUE_LAST_MESSAGE_ID),
                mSharedPreferences.getString(QUEUE_LAST_TEMPLATE_DATE, Constants.LAST_TEMPLATE_DATE),
                mSharedPreferences.getString(QUEUE_LAST_TEMPLATE_TIME, Constants.LAST_TEMPLATE_TIME));
    }

    @Override
    public void removeSession() {
        mSharedPreferences.edit()
                .remove(SESSION_ID)
                .remove(SESSION_TYPE_TRANSPORT)
                .remove(SESSION_LOGIN_ONLY)
                .remove(QUEUE_LAST_MESSAGE_ID)
                .remove(QUEUE_LAST_TEMPLATE_DATE)
                .remove(QUEUE_LAST_TEMPLATE_TIME)
                .apply();
    }

    @Nullable
    @Override
    public String loadSessionId() {
        return mSharedPreferences.getString(SESSION_ID, null);
    }

    @Nullable
    @Override
    public String loadTypeTransport() {
        return mSharedPreferences.getString(SESSION_TYPE_TRANSPORT, null);
    }

    @Override
    public boolean loadLoginOnly() {
        return mSharedPreferences.getBoolean(SESSION_LOGIN_ONLY, false);
    }

    @Override
    public void saveLoginOnly(boolean loginOnly) {
        mSharedPreferences.edit().putBoolean(SESSION_LOGIN_ONLY, loginOnly).apply();
    }

    @Override
    public long loadLastMessageQueueId() {
        return mSharedPreferences.getLong(QUEUE_LAST_MESSAGE_ID, Constants.QUEUE_LAST_MESSAGE_ID);
    }

    @Override
    public void saveLastMessageQueueId(long messageId) {
        mSharedPreferences.edit().putLong(QUEUE_LAST_MESSAGE_ID, messageId).apply();
    }

    @Override
    public String loadLastTemplateDate() {
        return mSharedPreferences.getString(QUEUE_LAST_TEMPLATE_DATE, Constants.LAST_TEMPLATE_DATE);
    }

    @Override
    public void saveLastTemplateDate(String lastTemplateDate) {
        mSharedPreferences.edit().putString(QUEUE_LAST_TEMPLATE_DATE, lastTemplateDate).apply();
    }

    @Override
    public String loadLastTemplateTime() {
        return mSharedPreferences.getString(QUEUE_LAST_TEMPLATE_TIME, Constants.LAST_TEMPLATE_TIME);
    }

    @Override
    public void saveLastTemplateTime(String lastTemplateTime) {
        mSharedPreferences.edit().putString(QUEUE_LAST_TEMPLATE_TIME, lastTemplateTime).apply();
    }

    //endregion

    //region Settings

    public void saveApplicationSettings(Settings settings) throws NullPointerException {
        if (settings == null) {
            throw new NullPointerException();
        }
        mSharedPreferences
                .edit()
                .putString(SETTINGS_DRIVER_CALL, settings.getCall())
                .putString(SETTINGS_DRIVER_PASSWORD, settings.getPassword())
                .putString(SETTINGS_PHONE_NUMBER, settings.getPhone())
                .putString(SETTINGS_SERVER_CUSTOM, settings.getServer())
                .apply();

        CrashlyticsConfigurator.setDriverName(settings.getCall());
    }

    public Settings loadApplicationSettings() {
        return new Settings(
                mSharedPreferences.getString(SETTINGS_DRIVER_CALL, ""),
                mSharedPreferences.getString(SETTINGS_DRIVER_PASSWORD, ""),
                mSharedPreferences.getString(SETTINGS_SERVER_CUSTOM, ""),
                mSharedPreferences.getString(SETTINGS_PHONE_NUMBER, ""));
    }

    public String loadDriverCall() {
        return mSharedPreferences.getString(SETTINGS_DRIVER_CALL, "");
    }

    public String loadDriverPassword() {
        return mSharedPreferences.getString(SETTINGS_DRIVER_PASSWORD, "");
    }

    public String loadDriverPhone() {
        return mSharedPreferences.getString(SETTINGS_PHONE_NUMBER, "");
    }

    public String loadServerName() {
        return mSharedPreferences.getString(SETTINGS_SERVER_CUSTOM, "");
    }

    public void saveChatSettings(ChatSettings settings) {
        if (settings != null) {
            mSharedPreferences
                    .edit()
                    .putInt(SETTINGS_CHAT_TYPE_NOTIFICATION,
                            settings.getNotificationType().getValue())
                    .apply();
        }
    }

    public ChatSettings loadChatSettings() {
        return new ChatSettings(NotificationType.getType(mSharedPreferences.getInt(
                SETTINGS_CHAT_TYPE_NOTIFICATION, NotificationType.Sound.getValue())));
    }

    //endregion

}
