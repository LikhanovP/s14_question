package com.rosa.swift.core.network.services.sap;

public interface ServiceCallback {
    void onEndedRequest();

    void onFinished(String response);

    void onFinishedWithException(WSException ex);

    void onCancelled();
}

