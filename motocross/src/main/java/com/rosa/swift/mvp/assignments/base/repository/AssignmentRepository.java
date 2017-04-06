package com.rosa.swift.mvp.assignments.base.repository;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

public class AssignmentRepository implements IAssignmentRepository {

    @Override
    public AssignmentDto getAssignmentByNumber(String number, TransportationType type) {
        return new AssignmentDto(DataRepository.getInstance().getTransportation(number, type));
    }

}
