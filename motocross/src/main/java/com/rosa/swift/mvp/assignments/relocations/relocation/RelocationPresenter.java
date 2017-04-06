package com.rosa.swift.mvp.assignments.relocations.relocation;

import com.arellomobile.mvp.InjectViewState;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.mvp.assignments.base.current.AssignmentPresenter;
import com.rosa.swift.mvp.assignments.base.current.IAssignmentView;

@InjectViewState
public class RelocationPresenter extends AssignmentPresenter {

    private String mRelocationNumber;

    public RelocationPresenter(String relocationNumber) {
        this.mRelocationNumber = relocationNumber;
    }

    @Override
    public void attachView(IAssignmentView view) {
        super.attachView(view);
        showAssignment();
    }

    private void showAssignment() {
        super.showAssignmentDescription(mRelocationNumber, TransportationType.Relocation);
    }
}
