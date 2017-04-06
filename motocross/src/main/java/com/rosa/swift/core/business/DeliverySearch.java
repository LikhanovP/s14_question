package com.rosa.swift.core.business;

import android.text.format.Time;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.network.responses.delivery.DeliveryResponse;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;

import java.util.ArrayList;
import java.util.List;

public class DeliverySearch {

    public static class SearchResult {

        @SerializedName("DELIVERIES")
        private List<DeliveryResponse> mDeliveryResList = new ArrayList<>();

        private List<Delivery> mDeliveryList;

        public static SearchResult getResult(String evParam) {
            try {
                Gson g = new Gson();
                return g.fromJson(evParam, SearchResult.class);
            } catch (Exception e) {
                return new SearchResult();
            }
        }

        public boolean isEmpty() {
            return mDeliveryResList.isEmpty();
        }

        public List<Delivery> getDeliveryList() {
            if (mDeliveryList == null) {
                mDeliveryList = new ArrayList<>();
                for (DeliveryResponse deliveryRes : mDeliveryResList) {
                    Delivery delivery = new Delivery(deliveryRes);
                    mDeliveryList.add(delivery);
                }
            }
            return mDeliveryList;
        }
    }

    public static class SearchOptions {

        @SerializedName("CLSIGN")
        public String driver;

        @SerializedName("DLV_DATE")
        public String date;

        @SerializedName("TKNUM")
        public String tknum;

        public SearchOptions(String driver) {
            this.driver = driver.toUpperCase();
            Time t = new Time();
            t.setToNow();
            this.date = t.format("%Y%m%d");
            this.tknum = "";
        }

        public SearchOptions(String driver, String tknum) {
            this.driver = driver.toUpperCase();
            this.tknum = ("0000000000" + tknum).substring(tknum.length());
            this.date = "";
        }

        public SearchOptions forToday() {
            Time t = new Time();
            t.setToNow();
            date = t.format("%Y%m%d");
            tknum = "";

            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SearchOptions that = (SearchOptions) o;

            if (date != null ? !date.equals(that.date) : that.date != null) return false;
            if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
            if (tknum != null ? !tknum.equals(that.tknum) : that.tknum != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = driver != null ? driver.hashCode() : 0;
            result = 31 * result + (date != null ? date.hashCode() : 0);
            result = 31 * result + (tknum != null ? tknum.hashCode() : 0);
            return result;
        }
    }

    public interface SearchCallback {
        public void onSearchCompleted(SearchResult result, SearchOptions options);

        public void onSearchCompletedError(Exception e);
    }

    public static void doSearch(final SearchOptions options, final SearchCallback callback) throws IllegalArgumentException {
        if (callback != null) {
            SapRequestUtils.findDelivery(options, new ServiceCallback() {
                @Override
                public void onEndedRequest() {

                }

                @Override
                public void onFinished(String evParams) {
                    try {
                        SearchResult res = SearchResult.getResult(evParams);
                        callback.onSearchCompleted(res, options);
                    } catch (Exception e) {
                        callback.onSearchCompletedError(e);
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    callback.onSearchCompletedError(ex);
                }

                @Override
                public void onCancelled() {
                    callback.onSearchCompleted(null, null);
                }

            });
        } else {
            throw new IllegalArgumentException();
        }
    }


}
