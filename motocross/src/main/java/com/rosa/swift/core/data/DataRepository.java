package com.rosa.swift.core.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.Session;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.data.dto.settings.ChatSettings;
import com.rosa.swift.core.data.dto.settings.Settings;
import com.rosa.swift.core.data.repositories.DatabaseRepository;
import com.rosa.swift.core.data.repositories.PreferencesRepository;
import com.rosa.swift.core.data.repositories.ServiceRepository;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabase;
import com.rosa.swift.core.data.repositories.preferences.LocalPreferences;
import com.rosa.swift.core.data.repositories.services.SapSwiftService;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;
import com.rosa.swift.core.network.json.sap.security.SecurityTask;
import com.rosa.swift.core.network.responses.price.GeoObjectsGeocoderResponse;
import com.rosa.swift.core.network.servers.ServerCollection;
import com.rosa.swift.core.network.services.yandex.ServiceGenerator;
import com.rosa.swift.core.network.services.yandex.YandexMapsService;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;
import com.rosa.swift.mvp.assignments.base.repository.Dto.RejectionReasonDto;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;
import com.rosa.swift.mvp.shift.quiz.repositories.AnswerDto;
import com.rosa.swift.mvp.shift.quiz.repositories.QuestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class DataRepository {

    private static final int EMPTY_NUMBER = -1;

    private static DataRepository sDataRepository = null;

    private PreferencesRepository mLocalPreferences;

    private DatabaseRepository mSwiftDatabase;

    private ServiceRepository mSapSwiftService;

    private YandexMapsService mMapService;

    private DataRepository(Context context) {
        mLocalPreferences = new LocalPreferences(context);
        mSwiftDatabase = new SwiftDatabase(context);
        mSapSwiftService = new SapSwiftService();
        mMapService = ServiceGenerator.createService(YandexMapsService.class);
    }

    public static DataRepository getInstance() {
        if (sDataRepository == null) {
            sDataRepository = new DataRepository(SwiftApplication.getApplication().getApplicationContext());
        }
        return sDataRepository;
    }

    public void clearTemporaryData() {
        mSwiftDatabase.deleteTransportations();
        mSapSwiftService.removeDeliveryTemplates();
        mSwiftDatabase.deletePlants();
    }

    //region Database

    //region Delivery

    public Delivery getTransportation(String number, TransportationType type) {
        return mSwiftDatabase.selectTransportation(number, type);
    }

    public List<Delivery> getDeliveries() {
        return mSwiftDatabase.selectTransportations(TransportationType.Delivery);
    }

    public List<Delivery> getRelocations() {
        return mSwiftDatabase.selectTransportations(TransportationType.Relocation);
    }

    public List<Delivery> getTransportations(TransportationType type) {
        return mSwiftDatabase.selectTransportations(type);
    }

    public void addOrUpdateDelivery(Delivery delivery) {
        mSwiftDatabase.insertOrUpdateDelivery(delivery);
    }

    public void removeDelivery(String number) {
        mSwiftDatabase.deleteTransportation(number, TransportationType.Delivery);
    }

    public void removeRelocation(String number) {
        mSwiftDatabase.deleteTransportation(number, TransportationType.Relocation);
    }

    public void removeTransportation(String number, TransportationType type) {
        mSwiftDatabase.deleteTransportation(number, type);
    }

    public void removeDeliveries() {
        mSwiftDatabase.deleteTransportations(TransportationType.Delivery);
    }

    public void removeRelocations() {
        mSwiftDatabase.deleteTransportations(TransportationType.Relocation);
    }

    //endregion

    //region DeliveryLocation

    public List<DeliveryLocation> getDeliveryLocations() {
        return mSwiftDatabase.selectDeliveryLocations();
    }

    public void addDeliveryLocation(DeliveryLocation location) {
        mSwiftDatabase.insertDeliveryLocation(location);
    }

    public void removeDeliveryLocations() {
        mSwiftDatabase.deleteDeliveryLocations();
    }

    //endregion

    //region Plant

    @Nullable
    public TplstCollection getPlants() {
        Tplst currentTown = mLocalPreferences.loadSelectedTown();
        return currentTown != null ? mSwiftDatabase.selectPlants(currentTown.getCode()) : null;
    }

    public void updatePlantsSelectedFlag(TplstCollection plants){
        mSwiftDatabase.updatePlants(plants);
    }

    public void replacePlants(TplstCollection plants) {
        mSwiftDatabase.deletePlants();
        mSwiftDatabase.insertPlants(plants);
    }

    //endregion

    //endregion

    //region Preferences

    //region Session

    public Session getSession() {
        return mLocalPreferences.loadSession();
    }

    @Nullable
    public String getSessionId() {
        return mLocalPreferences.loadSessionId();
    }

    @Nullable
    public String getTypeTransport() {
        return mLocalPreferences.loadTypeTransport();
    }

    public void setLoginOnly(boolean isLoginOnly) {
        mLocalPreferences.saveLoginOnly(isLoginOnly);
    }

    public void removeSession() {
        mLocalPreferences.removeSession();
    }

    public void replaceSession(Session session) {
        mLocalPreferences.saveSession(session);
        clearTemporaryData();
    }

    public long getLastMessageQueueId() {
        return mLocalPreferences.loadLastMessageQueueId();
    }

    public void setLastMessageQueueId(long queueId) {
        mLocalPreferences.saveLastMessageQueueId(queueId);
    }

    public String getLastTemplateDate() {
        return mLocalPreferences.loadLastTemplateDate();
    }

    public void setLastTemplateDate(String date) {
        mLocalPreferences.saveLastTemplateDate(date);
    }

    public String getLastTemplateTime() {
        return mLocalPreferences.loadLastTemplateTime();
    }

    public void setLastTemplateTime(String time) {
        mLocalPreferences.saveLastTemplateTime(time);
    }

    //endregion

    //region Town

    public void setSelectedTown(Tplst town) {
        mLocalPreferences.saveSelectedTown(town);
    }

    @Nullable
    public Tplst getSelectedTown() {
        return mLocalPreferences.loadSelectedTown();
    }

    @Nullable
    public String getSelectedTownCode() {
        Tplst town = mLocalPreferences.loadSelectedTown();
        return town != null ? town.getCode() : null;
    }

    //endregion

    //region Settings

    public void setSettings(Settings settings) {
        if (settings == null) {
            throw new NullPointerException("Settings must not be null");
        }

        //если позывной отличается от сохраненного ранее, то чистим все локальные данные
        String storedCall = mLocalPreferences.loadDriverCall();
        String newCall = settings.getCall();

        if (!TextUtils.isEmpty(storedCall) && !TextUtils.isEmpty(newCall) &&
                !newCall.equals(storedCall)) {
            Log.i(String.format(Locale.getDefault(),
                    "Задан новый позывной [%s]. Информация для предыдущего позывного [%s] будет удалена",
                    newCall, storedCall));
            clearTemporaryData();
        }

        mLocalPreferences.saveApplicationSettings(settings);
    }

    public Settings getSettings() {
        return mLocalPreferences.loadApplicationSettings();
    }

    public String getDriverCall() {
        return mLocalPreferences.loadDriverCall();
    }

    public String getDriverPassword() {
        return mLocalPreferences.loadDriverPassword();
    }

    public String getDriverPhone() {
        return mLocalPreferences.loadDriverPhone();
    }

    public String getServerName() {
        return mLocalPreferences.loadServerName();
    }

    public void updateCustomServer() {
        String customServer = mLocalPreferences.loadServerName();
        if (!TextUtils.isEmpty(customServer)) {
            ServerCollection.getInstance().updateServer(
                    ServerCollection.CUSTOM_SERVER, customServer, 0);
        }
    }

    public void setChatSettings(ChatSettings settings) {
        mLocalPreferences.saveChatSettings(settings);
    }

    public ChatSettings getChatSettings() {
        return mLocalPreferences.loadChatSettings();
    }

    //endregion

    //endregion

    //region SapService

    public void setDriverSessionData(DriverSessionResponse sessionData) {
        mSapSwiftService.setDriverSessionData(sessionData);
    }

    //region DeliveryTemplate

    @Nullable
    public DeliveryTemplateDto getDeliveryTemplateByType(TemplateType type) {
        return mSapSwiftService.getDeliveryTemplateByType(type);
    }

    public String getDeliveryDataHtml(TemplateType type, Delivery delivery) {
        DeliveryTemplateDto template = mSapSwiftService.getDeliveryTemplateByType(type);
        return template != null ? template.getDeliveryDataHtml(delivery) : delivery.getNumber();
    }

    public void addOrUpdateDeliveryTemplate(DeliveryTemplateDto template) {
        mSapSwiftService.addOrUpdateDeliveryTemplate(template);
    }

    //endregion

    //region Quiz

    @NonNull
    public List<QuestDto> getOptionalQuests() {
        return mSapSwiftService.getOptionalQuests();
    }

    @NonNull
    public List<QuestDto> getRequiredQuests() {
        return mSapSwiftService.getRequiredQuests();
    }

    public List<AnswerDto> getAnswersForQuestId(int questId) {
        ArrayList<AnswerDto> answers = new ArrayList<>();
        for (AnswerDto answer : mSapSwiftService.getAnswers()) {
            if (answer.getQuestId() == questId) {
                answers.add(answer);
            }
        }
        return answers;
    }

    //endregion

    //region ShiftSettings

    public int getIdleTimeInMilliseconds() {
        return mSapSwiftService.getShiftSettings() != null ?
                mSapSwiftService.getShiftSettings().getIdleTimeInMilliseconds() :
                Constants.DEFAULT_IDLE_TIME_IN_MILLISECONDS;
    }

    public int getQueueNumber() {
        return mSapSwiftService.getShiftSettings() != null ?
                mSapSwiftService.getShiftSettings().getQueueNumber() :
                Constants.DEFAULT_QUEUE_NUMBER;
    }

    public void setQueueNumber(String number) {
        if (mSapSwiftService.getShiftSettings() != null) {
            mSapSwiftService.getShiftSettings().setQueueNumber(number);
        }
    }

    public void setDefaultQueueNumber() {
        if (mSapSwiftService.getShiftSettings() != null) {
            mSapSwiftService.getShiftSettings().setDefaultQueueNumber();
        }
    }

    //endregion

    //region DriverMessage

    @NonNull
    public List<DriverMessageDto> getDriverMessages() {
        return mSapSwiftService.getQuestDtos();
    }

    //endregion

    //region RejectionReason

    @NonNull
    public List<RejectionReasonDto> getRejectionReasons() {
        return mSapSwiftService.getRejectionReasons();
    }

    //endregion

    //region GateKeeper

    public boolean isGateKeeperEnabled() {
        return mSapSwiftService.getGateKeeperInfo() != null &&
                mSapSwiftService.getGateKeeperInfo().isEnabled();
    }

    //endregion

    //region CupDelay

    public int getCupDaysToDelay() {
        return mSapSwiftService.getCupDelayInfo() != null ?
                mSapSwiftService.getCupDelayInfo().getDaysToDelay() : EMPTY_NUMBER;
    }

    public boolean isCupDelayed() {
        return mSapSwiftService.getCupDelayInfo() != null &&
                mSapSwiftService.getCupDelayInfo().isDelayed();
    }

    //endregion

    //region SecurityTasks

    @NonNull
    public List<SecurityTask> getSecurityTasks() {
        return mSapSwiftService.getSecurityTasks();
    }

    //endregion

    //region PhotoSessionTask

    public int getIndexForNextPhotoSessionTask() {
        return mSapSwiftService.getIndexForNextPhotoSessionTask();
    }

    @Nullable
    public PhotoSessionTaskDto getPhotoSessionTask(int taskIndex) {
        return mSapSwiftService.getPhotoSessionTask(taskIndex);
    }

    @NonNull
    public List<PhotoSessionTaskDto> getPhotoSessionTasks() {
        return mSapSwiftService.getPhotoSessionTasks();
    }

    public void updatePhotoSessionTask(PhotoSessionTaskDto task) {
        if (task != null) {
            for (PhotoSessionTaskDto updatingTask : mSapSwiftService.getPhotoSessionTasks()) {
                if (updatingTask.getCupViewId().equals(task.getCupViewId())) {
                    updatingTask.setPhotoPath(task.getPhotoPath());
                }
            }
        }
    }

    public void removePhotoSessionTask(PhotoSessionTaskDto task) {
        mSapSwiftService.removePhotoSessionTask(task);
    }

    //endregion

    //region PhotoDocumentTask

    public int getIndexForNextDocumentTask() {
        return mSapSwiftService.getIndexForNextPhotoDocumentTask();
    }

    @Nullable
    public PhotoDocumentTaskDto getPhotoDocumentTask(int taskIndex) {
        return mSapSwiftService.getPhotoDocumentTask(taskIndex);
    }

    @NonNull
    public List<PhotoDocumentTaskDto> getPhotoDocumentTasks() {
        return mSapSwiftService.getPhotoDocumentTasks();
    }

    public void completePhotoDocumentTask(PhotoDocumentTaskDto task) {
        mSapSwiftService.completePhotoDocumentTask(task);
    }

    //endregion

    //endregion

    //region MapService

    public Call<GeoObjectsGeocoderResponse> getGeoObjects(String geoCode, String position) {
        return mMapService.getGeoObjects(
                Constants.SERVICE_YANDEXMAPS_FORMAT, geoCode,
                Constants.SERVICE_YANDEXMAPS_RESULTS, position,
                Constants.SERVICE_YANDEXMAPS_LENGTH_FIELD);
    }

    //endregion

}
