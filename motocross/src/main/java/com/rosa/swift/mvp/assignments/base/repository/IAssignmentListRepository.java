package com.rosa.swift.mvp.assignments.base.repository;

import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

import java.util.List;

import io.reactivex.Maybe;

public interface IAssignmentListRepository {

    Maybe<List<AssignmentDto>> getAssignmentsByType(TransportationType type);
}
