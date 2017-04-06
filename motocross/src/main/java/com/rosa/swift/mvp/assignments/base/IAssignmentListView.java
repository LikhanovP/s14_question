package com.rosa.swift.mvp.assignments.base;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

import java.util.List;

@StateStrategyType(SkipStrategy.class)
public interface IAssignmentListView extends MvpView {

    void showItems(List<AssignmentDto> assignments);

    void showRelocation(String number);

    void close();

}
