package com.rosa.swift.mvp.assignments.base;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.repository.AssignmentListRepository;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;
import com.rosa.swift.mvp.assignments.base.repository.IAssignmentListRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class AssignmentListPresenter extends MvpPresenter<IAssignmentListView> {

    private IAssignmentListRepository mRepository = new AssignmentListRepository();
    private List<AssignmentDto> mAssignments;

    //region Lifecycle

    @Override
    public void attachView(IAssignmentListView view) {
        super.attachView(view);
        initView();
    }

    //endregion

    private void initView() {
        mRepository.getAssignmentsByType(TransportationType.Relocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(assignments -> {
                    mAssignments = assignments;
                    getViewState().showItems(assignments);
                });
    }

    public void onAssignmentClick(int position) {
        getViewState().showRelocation(mAssignments.get(position).getNumber());
    }
}
