package com.rosa.swift.core.data.repositories;

import android.support.annotation.Nullable;

import com.rosa.swift.core.business.Session;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.settings.ChatSettings;
import com.rosa.swift.core.data.dto.settings.Settings;

public interface PreferencesRepository {

    //region Session

    Session loadSession();

    void saveSession(Session session);

    void removeSession();

    @Nullable
    String loadSessionId();

    @Nullable
    String loadTypeTransport();

    boolean loadLoginOnly();

    void saveLoginOnly(boolean loginOnly);

    long loadLastMessageQueueId();

    void saveLastMessageQueueId(long messageId);

    String loadLastTemplateDate();

    void saveLastTemplateDate(String lastTemplateDate);

    String loadLastTemplateTime();

    void saveLastTemplateTime(String lastTemplateTime);

    //endregion

    //region Town

    //TODO: ipopov 29.03.2017 хранить каждый параметр отдельно, а не сериализовать весь объект

    void saveSelectedTown(Tplst town);

    Tplst loadSelectedTown();

    //endregion

    //region Settings

    void saveApplicationSettings(Settings settings);

    Settings loadApplicationSettings();

    String loadDriverCall();

    String loadDriverPassword();

    String loadDriverPhone();

    String loadServerName();

    //endregion

    //region Chat

    void saveChatSettings(ChatSettings settings);

    ChatSettings loadChatSettings();

    //endregion

}
