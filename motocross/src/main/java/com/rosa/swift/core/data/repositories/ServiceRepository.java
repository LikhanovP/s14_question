package com.rosa.swift.core.data.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;
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

import java.util.List;

public interface ServiceRepository {

    void setDriverSessionData(DriverSessionResponse sessionData);

    @Nullable
    ShiftSettingsDto getShiftSettings();

    @Nullable
    GateKeeperDto getGateKeeperInfo();

    @Nullable
    CupDelayDto getCupDelayInfo();

    @NonNull
    List<QuestDto> getRequiredQuests();

    @NonNull
    List<QuestDto> getOptionalQuests();

    @NonNull
    List<AnswerDto> getAnswers();

    @NonNull
    List<DriverMessageDto> getQuestDtos();

    @NonNull
    List<RejectionReasonDto> getRejectionReasons();

    @NonNull
    List<SecurityTask> getSecurityTasks();

    //region Templates

    @Nullable
    DeliveryTemplateDto getDeliveryTemplateByType(TemplateType type);

    void removeDeliveryTemplates();

    void addOrUpdateDeliveryTemplate(DeliveryTemplateDto template);

    //endregion

    //region PhotoSessionTask

    @Nullable
    PhotoSessionTaskDto getPhotoSessionTask(int taskIndex);

    @NonNull
    List<PhotoSessionTaskDto> getPhotoSessionTasks();

    int getIndexForNextPhotoSessionTask();

    void removePhotoSessionTask(PhotoSessionTaskDto task);

    //endregion

    //region PhotoDocumentTask

    @Nullable
    PhotoDocumentTaskDto getPhotoDocumentTask(int taskIndex);

    @NonNull
    List<PhotoDocumentTaskDto> getPhotoDocumentTasks();

    int getIndexForNextPhotoDocumentTask();

    void completePhotoDocumentTask(PhotoDocumentTaskDto task);

    //endregion

}
