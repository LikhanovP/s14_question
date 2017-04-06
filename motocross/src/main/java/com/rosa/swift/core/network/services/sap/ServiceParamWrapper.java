package com.rosa.swift.core.network.services.sap;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.DataRepository;

public class ServiceParamWrapper {

    private ServiceCallback mCallback;

    private ServiceFunction mFunction;

    private String mParameter;

    public ServiceParamWrapper(ServiceFunction function, ServiceCallback callback) {
        this(function, callback, DataRepository.getInstance().getSessionId());
    }

    public ServiceParamWrapper(ServiceFunction function, ServiceCallback callback, String parameter) {
        mFunction = function;
        mParameter = parameter;
        mCallback = callback;
    }

    public ServiceParamWrapper(ServiceFunction function, Object object, ServiceCallback callback) {
        try {
            if (object == null) {
                throw new Exception("Parameters is not initialized");
            }

            String jsonParameter = new Gson().toJson(object, object.getClass());
            if (TextUtils.isEmpty(jsonParameter)) {
                throw new Exception("Serializing is failed");
            }

            mFunction = function;
            mParameter = jsonParameter;
            mCallback = callback;
        } catch (Exception exception) {
            Log.e(String.format("Function wrapping error: %s. %s",
                    function.name(), exception.getMessage()));
        }
    }

    public ServiceParamWrapper(ServiceFunction function, String parameter, ServiceCallback callback) {
        try {
            if (TextUtils.isEmpty(parameter)) {
                throw new Exception("Parameters is not initialized");
            }
            mFunction = function;
            mParameter = parameter;
            mCallback = callback;
        } catch (Exception exception) {
            Log.e(String.format("Function wrapping error: %s. %s",
                    function.name(), exception.getMessage()));
        }
    }

    public ServiceFunction getFunction() {
        return mFunction;
    }

    public String getParameter() {
        return mParameter;
    }

    public ServiceCallback getCallback() {
        return mCallback;
    }

}
