package com.rosa.swift.mvp.assignments.base.repository;

import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;

import java.util.List;

public interface IDriverMessagesRepository {

    List<DriverMessageDto> getMessages();

}
