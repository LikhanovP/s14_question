package com.rosa.swift.core.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.ui.views.YandexMapView;

import java.util.ArrayList;

public class DeliveryMapActivity extends AppCompatActivity implements YandexMapView.MapCallback {

    private YandexMapView mYandexMapView;
    private ArrayList<GeoObject> mAddressList;

    public static Intent newIntent(Context context, ArrayList<GeoObject> addresses) {
        //public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DeliveryMapActivity.class);
        intent.putParcelableArrayListExtra(Constants.EXTRA_DELIVERY_ADDRESSES, addresses);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_map);

        mAddressList = getIntent().getParcelableArrayListExtra(
                Constants.EXTRA_DELIVERY_ADDRESSES);

        mYandexMapView = (YandexMapView) findViewById(R.id.delivery_map_view);
        mYandexMapView.setMapCallback(this);
        mYandexMapView.loadMap();
    }

    @Override
    @JavascriptInterface
    public void onLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mYandexMapView.addPoints(mAddressList);
            }
        });
    }

    @Override
    @JavascriptInterface
    public void onRouteAdded(String jsonString) {

    }

    @Override
    @JavascriptInterface
    public void onPointAdded() {

    }

    @Override
    @JavascriptInterface
    public void onClicked(String jsonString, double longitude, double latitude) {

    }

    @Override
    @JavascriptInterface
    public void onError(String caption, String error) {
        String s = caption;
    }
}
