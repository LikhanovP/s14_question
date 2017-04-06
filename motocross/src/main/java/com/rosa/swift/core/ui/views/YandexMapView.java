package com.rosa.swift.core.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.dto.geo.GeoPoint;
import com.rosa.swift.core.network.requests.price.RouteCoordRequest;

import java.util.List;

public class YandexMapView extends WebView {

    public YandexMapView(Context context) {
        super(context);
    }

    public YandexMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YandexMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void executeScript(final String jsScript) {
        loadUrl(String.format("javascript:%s", jsScript));
    }

    public void setMapCallback(MapCallback mapCallback) {
        addJavascriptInterface(mapCallback, "Android");
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void loadMap() {
        loadUrl("file:///android_asset/html/yandex_map.html");
    }

    public void addRoute(GeoPoint[] points) {
        RouteCoordRequest routeCoordReq = new RouteCoordRequest(points);
        String jsonPoints = new Gson().toJson(routeCoordReq.getRoutePoints());
        executeScript(String.format("route('%s')", jsonPoints));
    }

    public void addPoint(int pointCode, GeoObject geoObject) {
        if (geoObject != null && geoObject.getLongitude() > 0 && geoObject.getLatitude() > 0) {
            executeScript(String.format("addPoint('%1$s', %2$s, %3$s)",
                    pointCode, geoObject.getLatitude(), geoObject.getLongitude()));
        }
    }

    public void setCenterMap(double longitude, double latitude) {
        executeScript(String.format("setCenterMap(%1$s, %2$s)", latitude, longitude));
    }

    public void addPoints(List<GeoObject> addresses) {
        if (addresses != null && addresses.size() > 0) {
            String jsonPoints = new Gson().toJson(addresses);
            executeScript(String.format("addPoints('%s')", jsonPoints));
        }
    }

    public interface MapCallback {

        @JavascriptInterface
        void onLoaded();

        @JavascriptInterface
        void onRouteAdded(String jsonString);

        @JavascriptInterface
        void onPointAdded();

        @JavascriptInterface
        void onClicked(String jsonString, double longitude, double latitude);

        @JavascriptInterface
        void onError(String caption, String error);

    }

}
