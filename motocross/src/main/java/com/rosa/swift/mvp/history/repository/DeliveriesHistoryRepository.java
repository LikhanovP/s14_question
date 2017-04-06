package com.rosa.swift.mvp.history.repository;

import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.DeliverySearch;
import com.rosa.swift.core.data.dto.common.Delivery;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static android.content.Context.MODE_PRIVATE;

public class DeliveriesHistoryRepository {

    private List<Delivery> mCachedDeliveries;

    public Single<List<DeliveryDto>> getAllDeliveries() {
        DeliverySearch.SearchOptions searchOptions = new DeliverySearch.SearchOptions(getDriverName()).forToday();

        return createDeliverySearchObservable(searchOptions)
                .map(DeliverySearch.SearchResult::getDeliveryList)
                .doOnSuccess(list -> mCachedDeliveries = list)
                .flatMap(deliveries -> Observable.fromIterable(deliveries)
                        .map(DeliveryDto::new)
                        .toList());
    }

    private Single<DeliverySearch.SearchResult> createDeliverySearchObservable(DeliverySearch.SearchOptions searchOptions) {
        return Single.create(emitter -> {
            DeliverySearch.SearchCallback callback = new DeliverySearch.SearchCallback() {
                @Override
                public void onSearchCompleted(DeliverySearch.SearchResult result, DeliverySearch.SearchOptions options) {
                    emitter.onSuccess(result);
                }

                @Override
                public void onSearchCompletedError(Exception e) {
                    emitter.onError(e);
                }
            };
            DeliverySearch.doSearch(
                    searchOptions,
                    callback
            );
        });
    }

    public Single<Delivery> getCachedDeliveryByNumber(String number) {
        return Observable.fromIterable(mCachedDeliveries)
                .filter(delivery -> number.equals(delivery.getNumber()))
                .firstOrError();
    }

    public Maybe<Delivery> getDeliveryByNumber(String number) {
        //TODO: fixme ipopov 10.03.2017 передавать дто модели в презентер
        DeliverySearch.SearchOptions options = new DeliverySearch.SearchOptions(getDriverName(), number);
        return createDeliverySearchObservable(options)
                .toMaybe()
                .map(DeliverySearch.SearchResult::getDeliveryList)
                .filter(deliveries -> !deliveries.isEmpty())
                .map(deliveries -> deliveries.get(0));
    }

    public String getDriverName() {
        return SwiftApplication.getApplication()
                .getSharedPreferences("com.rosa.motocross", MODE_PRIVATE)
                .getString("driverCall", "");
    }
}
