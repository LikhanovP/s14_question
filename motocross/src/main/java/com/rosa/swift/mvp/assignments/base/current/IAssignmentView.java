package com.rosa.swift.mvp.assignments.base.current;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;
import com.rosa.swift.mvp.views.IView;

import java.util.List;

@StateStrategyType(SkipStrategy.class)
public interface IAssignmentView extends IView {

    //region Show

    void showDescription(String description);

    //endregion

    //region CompleteAssignment

    void completeAssignment();

    void showConfirmCompleteDialog();

    //endregion

    //region SendMessage

    void sendMessage();

    void showMessageTypesDialog(List<DriverMessageDto> messages);

    void showCommentDialog();

    //endregion

    //region OpenGate

    void openGate();

    void showOpenGateDialog();

    //endregion

    //region SendPrint

    void sendPrint();

    void showConfirmSendPrintDialog();

    //endregion

    void createIncidentPhoto();

    void openPersonalCabinet();

    void showDocuments();

    void showAddressesMap();

    void showLocationMap();

    void update();

    void close();

}
