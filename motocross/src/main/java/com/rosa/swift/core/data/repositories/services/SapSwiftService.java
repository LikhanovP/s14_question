package com.rosa.swift.core.data.repositories.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.data.repositories.ServiceRepository;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.Answer;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.DriverMessage;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.PhotoDocumentTask;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.PhotoSessionTask;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.Quest;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.RejectionReason;
import com.rosa.swift.core.network.json.sap.security.SecurityTask;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;
import com.rosa.swift.mvp.assignments.base.repository.Dto.RejectionReasonDto;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.CupDelayDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;
import com.rosa.swift.mvp.shift.ShiftSettingsDto;
import com.rosa.swift.mvp.shift.gate.repository.GateKeeperDto;
import com.rosa.swift.mvp.shift.quiz.repositories.AnswerDto;
import com.rosa.swift.mvp.shift.quiz.repositories.QuestDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto.EMPTY_TASK;

public class SapSwiftService implements ServiceRepository {

    @Nullable
    private ShiftSettingsDto mShiftSettings;

    @Nullable
    private GateKeeperDto mGateKeeper;

    @Nullable
    private CupDelayDto mCupDelay;

    @NonNull
    private HashMap<TemplateType, DeliveryTemplateDto> mDeliveryTemplates = new HashMap<>();

    @NonNull
    private List<QuestDto> mRequiredQuests = new ArrayList<>();

    @NonNull
    private List<QuestDto> mNonRequiredQuests = new ArrayList<>();

    @NonNull
    private List<AnswerDto> mAnswers = new ArrayList<>();

    @NonNull
    private List<DriverMessageDto> mDriverMessages = new ArrayList<>();

    @NonNull
    private List<RejectionReasonDto> mRejectionReasons = new ArrayList<>();

    @NonNull
    private List<SecurityTask> mSecurityTasks = new ArrayList<>();

    @NonNull
    private List<PhotoSessionTaskDto> mPhotoSessionTasks = new ArrayList<>();

    @NonNull
    private List<PhotoDocumentTaskDto> mPhotoDocumentTasks = new ArrayList<>();

    @Override
    public void setDriverSessionData(DriverSessionResponse response) {
        setDefault();
        if (response != null) {

            //shift settings
            mShiftSettings = new ShiftSettingsDto(response.getIdleTime());

            //gate keeper
            mGateKeeper = new GateKeeperDto(response.isGateKeeperActive());

            //cup delay info
            mCupDelay = new CupDelayDto(response.isCupWasDelayed(), response.getCupDaysToDelay());

            //quests
            for (Quest quest : response.getQuests()) {
                QuestDto questDto = new QuestDto(quest);
                if (questDto.isRequired()) {
                    mRequiredQuests.add(questDto);
                } else {
                    mNonRequiredQuests.add(questDto);
                }
            }

            //answers
            for (Answer answer : response.getAnswers()) {
                mAnswers.add(new AnswerDto(answer));
            }

            //driver messages
            for (DriverMessage message : response.getDriverMessages()) {
                mDriverMessages.add(new DriverMessageDto(message));
            }

            //rejection reasons
            for (RejectionReason reason : response.getRejectionReasons()) {
                mRejectionReasons.add(new RejectionReasonDto(reason));
            }

            // TODO: 28.03.2017 ipopov dto
            //security tasks
            mSecurityTasks = response.getSecurityTasks();

            //photo session tasks
            int i = 1;//TODO: ipopov 28.03.2017 индекс создается для вьюшки, переделать отображение индекса во вьюшке
            for (PhotoSessionTask task : response.getPhotoSessionTasks()) {
                mPhotoSessionTasks.add(new PhotoSessionTaskDto(task, i));
                i++;
            }

            //photo document tasks
            for (PhotoDocumentTask task : response.getPhotoDocumentTasks()) {
                if (task != null && !TextUtils.isEmpty(task.getPhotoDocumentType())) {
                    mPhotoDocumentTasks.add(new PhotoDocumentTaskDto(task));
                }
            }

        }
    }

    private void setDefault() {
        mShiftSettings = null;
        mGateKeeper = null;
        mCupDelay = null;
        mRequiredQuests.clear();
        mNonRequiredQuests.clear();
        mDriverMessages.clear();
        mRejectionReasons.clear();
        mSecurityTasks.clear();
        mPhotoSessionTasks.clear();
        mPhotoDocumentTasks.clear();
        mDeliveryTemplates.clear();
    }

    //region Templates

    @Nullable
    @Override
    public DeliveryTemplateDto getDeliveryTemplateByType(TemplateType type) {
        return mDeliveryTemplates.get(type);
    }

    @Override
    public void addOrUpdateDeliveryTemplate(DeliveryTemplateDto template) {
        if (template != null) {
            TemplateType type = template.getType();
            if (mDeliveryTemplates.containsKey(type)) {
                mDeliveryTemplates.remove(type);
            }
            mDeliveryTemplates.put(type, template);
        }
    }

    @Override
    public void removeDeliveryTemplates() {
        mDeliveryTemplates.clear();
    }

    //endregion

    //region Quiz

    @NonNull
    @Override
    public List<QuestDto> getRequiredQuests() {
        return mRequiredQuests;
    }

    @NonNull
    @Override
    public List<QuestDto> getOptionalQuests() {
        return mNonRequiredQuests;
    }

    @NonNull
    @Override
    public List<AnswerDto> getAnswers() {
        return mAnswers;
    }

    //endregion

    //region ShiftSettings

    @Nullable
    @Override
    public ShiftSettingsDto getShiftSettings() {
        return mShiftSettings;
    }

    //endregion

    //region DriverMessages

    @NonNull
    public List<DriverMessageDto> getQuestDtos() {
        return mDriverMessages;
    }

    //endregion

    //region RejectReasons

    @NonNull
    @Override
    public List<RejectionReasonDto> getRejectionReasons() {
        return mRejectionReasons;
    }

    //endregion

    //region GateKeeper

    @Nullable
    @Override
    public GateKeeperDto getGateKeeperInfo() {
        return mGateKeeper;
    }

    //endregion

    //region CupDelay

    @Nullable
    @Override
    public CupDelayDto getCupDelayInfo() {
        return mCupDelay;
    }

    //endregion

    //region SecurityTasks

    @NonNull
    @Override
    public List<SecurityTask> getSecurityTasks() {
        return mSecurityTasks;
    }

    //endregion

    //region PhotoSessionTask

    @Override
    public int getIndexForNextPhotoSessionTask() {
        return mPhotoSessionTasks.size() > 0 ? 0 : EMPTY_TASK;
    }

    @Nullable
    @Override
    public PhotoSessionTaskDto getPhotoSessionTask(int taskIndex) {
        return mPhotoSessionTasks.size() > taskIndex ? mPhotoSessionTasks.get(taskIndex) : null;
    }

    @NonNull
    @Override
    public List<PhotoSessionTaskDto> getPhotoSessionTasks() {
        return mPhotoSessionTasks;
    }

    @Override
    public void removePhotoSessionTask(PhotoSessionTaskDto task) {
        if (mPhotoSessionTasks.contains(task)) {
            mPhotoSessionTasks.remove(task);
        }
    }

    //endregion

    //region PhotoDocumentTask

    @Override
    public int getIndexForNextPhotoDocumentTask() {
        //TODO: 04.04.2017 ipopov получать все задачи за раз, а не поштучно
        for (int i = 0; i < mPhotoDocumentTasks.size(); i++) {
            if (mPhotoDocumentTasks.get(i).isUnhandled()) {
                return i;
            }
        }

        return EMPTY_TASK;
    }

    @Nullable
    @Override
    public PhotoDocumentTaskDto getPhotoDocumentTask(int taskIndex) {
        return mPhotoDocumentTasks.size() > taskIndex ? mPhotoDocumentTasks.get(taskIndex) : null;
    }

    @NonNull
    @Override
    public List<PhotoDocumentTaskDto> getPhotoDocumentTasks() {
        return mPhotoDocumentTasks;
    }

    @Override
    public void completePhotoDocumentTask(PhotoDocumentTaskDto task) {
        if (mPhotoDocumentTasks.contains(task)) {
            task.markAsCompleted();
            int index = mPhotoDocumentTasks.indexOf(task);
            mPhotoDocumentTasks.set(index, task);
        }
    }

    //endregion

}
