package com.rosa.swift.core.network.services.sap;

import android.os.AsyncTask;

public class ServiceTask extends AsyncTask<ServiceParamWrapper, Void, Boolean> {

    private WSException mException;

    private String mResult;

    private ServiceParamWrapper mWrapper;

    private ZMotoService mService;

    @Override
    protected Boolean doInBackground(ServiceParamWrapper... serviceParamWrappers) {
        try {
            mWrapper = serviceParamWrappers[0];
            mService = ZMotoService.getInstance();
            mResult = mService.callService(mWrapper.getFunction(), mWrapper.getParameter());
            return true;
        } catch (WSException e) {
            mException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean finishedWithoutError) {
        if (mWrapper.getCallback() != null) {
            mWrapper.getCallback().onEndedRequest();
            if (finishedWithoutError) {
                mWrapper.getCallback().onFinished(mResult);
            } else {
                mWrapper.getCallback().onFinishedWithException(mException);
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (mService != null) {
            mService.cancelRequest();
        }
        if (mWrapper.getCallback() != null) {
            mWrapper.getCallback().onCancelled();
        }
    }
}
