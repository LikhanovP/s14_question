package com.rosa.swift.mvp.assignments.base.repository;

import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class AssignmentListRepository implements IAssignmentListRepository {

    private DataRepository mDataRepository = DataRepository.getInstance();

    @Override
    public Maybe<List<AssignmentDto>> getAssignmentsByType(TransportationType type) {
        return Single.just(mDataRepository.getTransportations(type))
                .flatMapObservable(Observable::fromIterable)
                .map(AssignmentDto::new)
                .toList()
                .filter(assignments -> !assignments.isEmpty());
    }
}
