package com.rosa.swift.mvvm.price;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.rosa.motocross.BR;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.dto.routing.RoutePath;
import com.rosa.swift.core.data.dto.routing.RoutePrice;
import com.rosa.swift.core.data.dto.transports.TransportType;

public class PriceViewModel extends BaseObservable {

    // TODO: 27.03.2017 ipopov перевести на MVP

    private Context mContext;

    private GeoObject mStartAddress;

    private GeoObject mEndAddress;

    private TransportType mTransport;

    private RoutePath mRoutePath;

    private RoutePrice mRoutePrice;

    public GeoObject[] getGeoObjects() {
        return new GeoObject[]{mStartAddress, mEndAddress};
    }

    public boolean isRequestPriceDataCorrect() {
        if (mStartAddress == null) {
            showErrorMessage(R.string.price_start_address_empty_error_msg);
            return false;
        }

        if (mEndAddress == null) {
            showErrorMessage(R.string.price_end_address_empty_error_msg);
            return false;
        }

        if (mTransport == null) {
            showErrorMessage(R.string.price_type_transport_empty_error_msg);
            return false;
        }

        return true;
    }

    private boolean isRoutePathCorrect() {
        return mRoutePath != null && mRoutePath.getLength() > 0 && mRoutePath.getSeconds() > 0;
    }

    private boolean isRoutePriceCorrect() {
        return mRoutePrice != null && mRoutePrice.getPrice() > 0 && mRoutePrice.getCostPoint() > 0;
    }

    public boolean isRouteInfoExist() {
        return isRoutePathCorrect() || isRoutePriceCorrect();
    }

    public PriceViewModel(Context context) {
        mContext = context;
    }

    public void showErrorMessage(int stringId) {
        CommonUtils.ShowErrorMessage(mContext, mContext.getString(stringId));
    }

    public void showErrorMessage(String string) {
        CommonUtils.ShowErrorMessage(mContext, string);
    }

    //region Binding

    @Bindable
    public String getTitle() {
        return mRoutePrice != null && mRoutePrice.getPrice() > 0 ?
                String.format("Стоимость маршрута %s", mRoutePrice.getStringPrice()) :
                mContext.getString(R.string.price_cost_not_define);
    }

    @Bindable
    public GeoObject getStartAddress() {
        return mStartAddress;
    }

    @Bindable
    public GeoObject getEndAddress() {
        return mEndAddress;
    }

    @Bindable
    public TransportType getTransport() {
        return mTransport;
    }

    @Bindable
    public RoutePrice getRoutePrice() {
        return mRoutePrice;
    }

    @Bindable
    public RoutePath getRoutePath() {
        return mRoutePath ;
    }


    public void setStartAddress(GeoObject startAddress) {
        mStartAddress = startAddress;
        notifyPropertyChanged(BR.startAddress);
    }

    public void setEndAddress(GeoObject endAddress) {
        mEndAddress = endAddress;
        notifyPropertyChanged(BR.endAddress);
    }

    public void setTransport(TransportType transport) {
        mTransport = transport;
        notifyPropertyChanged(BR.transport);
    }

    public void setRoutePrice(RoutePrice routePrice) {
        mRoutePrice = routePrice;
        notifyPropertyChanged(BR.routePrice);
        notifyPropertyChanged(BR.title);
    }

    public void setRoutePath(RoutePath routePath) {
        mRoutePath = routePath;
        notifyPropertyChanged(BR.routePath);
    }

    //endregion

}
