package com.rosa.swift.mvp.history;

import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.ui.activities.MainActivity;
import com.rosa.swift.mvp.history.repository.DeliveriesHistoryRepository;
import com.rosa.swift.mvp.history.repository.DeliveryDto;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class DeliveriesHistoryPresenter extends MvpPresenter<IDeliveriesHistoryView> {
    public static final String TAG = "DeliveriesPresenter";

    private DeliveriesHistoryRepository mRepository = new DeliveriesHistoryRepository();
    private List<DeliveryDto> mDeliveriesList;

    private MainActivity mMainActivity;

    public DeliveriesHistoryPresenter(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    //region ============================== Lifecycle ==============================

    @Override
    public void attachView(IDeliveriesHistoryView view) {
        super.attachView(view);
        subscribeOnDeliveriesObservable();
    }

    //endregion

    private Disposable subscribeOnDeliveriesObservable() {
        return mRepository.getAllDeliveries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                            getViewState().showHistoryList(list);
                            mDeliveriesList = list;
                        },
                        Throwable::printStackTrace);
    }

    public void onSearchClick() {
        getViewState().showDeliverySearchDialog();
    }

    public void findDeliveryByNumber(String number) {
        mRepository.getDeliveryByNumber(number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<Delivery>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        /*empty*/
                    }

                    @Override
                    public void onSuccess(Delivery delivery) {
                        //TODO: fixme ipopov 10.03.2017 исправить передачу модели в мейн активити
                        mMainActivity.setCurrentDelivery(delivery);
                        getViewState().showDetailInfo(new DeliveryDto(delivery));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        getViewState().showMessage("Доставка не найдена");
                    }
                });
    }

    public void onRefreshClick() {
        subscribeOnDeliveriesObservable();
    }

    public void onListItemClick(int position) {
        if (mDeliveriesList.size() > 0) {
            DeliveryDto deliveryDto = mDeliveriesList.get(position);
            mRepository.getCachedDeliveryByNumber(deliveryDto.getTkNum())
                    .subscribe(delivery -> mMainActivity.setCurrentDelivery(delivery));

            getViewState().showDetailInfo(deliveryDto);
        }
    }

    // FIXME: 13.02.2017 избежать передачу вью в этот метод
    public void openOptionsForDelivery(String tkNumber, View clickedView) {
        mRepository.getCachedDeliveryByNumber(tkNumber)
                .subscribe(delivery -> mMainActivity.setCurrentDelivery(delivery));

        getViewState().showPopupMenuForDelivery(tkNumber, clickedView);
    }

    public void onScanClick() {
        mMainActivity.sendTTNScan();
    }

    public void onIncidentClick() {
        mMainActivity.getIncidentPhoto();
    }

    public void onSendMessageClick() {
        mMainActivity.sendMessage();
    }

}