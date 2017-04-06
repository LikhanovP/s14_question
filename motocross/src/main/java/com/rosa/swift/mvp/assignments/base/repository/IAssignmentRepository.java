package com.rosa.swift.mvp.assignments.base.repository;

import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

public interface IAssignmentRepository {

    AssignmentDto getAssignmentByNumber(String number, TransportationType type);

}
