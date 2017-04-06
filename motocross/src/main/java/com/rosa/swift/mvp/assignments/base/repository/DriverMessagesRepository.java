package com.rosa.swift.mvp.assignments.base.repository;

import android.support.annotation.NonNull;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;

import java.util.List;

public class DriverMessagesRepository implements IDriverMessagesRepository {

    @NonNull
    @Override
    public List<DriverMessageDto> getMessages() {
        return DataRepository.getInstance().getDriverMessages();
    }

}
