package com.rosa.swift.mvp.history;

import android.view.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.rosa.swift.mvp.history.repository.DeliveryDto;

import java.util.List;

@StateStrategyType(SkipStrategy.class)
public interface IDeliveriesHistoryView extends MvpView {

    void showHistoryList(List<DeliveryDto> deliveries);

    void showDeliverySearchDialog();

    void showDetailInfo(DeliveryDto delivery);

    void showMessage(String message);

    void showPopupMenuForDelivery(String tkNumber, View clickedView);

    void showLoad();

    void hideLoad();
}
