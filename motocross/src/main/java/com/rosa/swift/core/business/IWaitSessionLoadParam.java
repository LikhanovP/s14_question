package com.rosa.swift.core.business;

/**
 * Created by inurlikaev on 08.06.2016.
 */
public interface IWaitSessionLoadParam {
    void execute();

    void onException(Exception ex);

    void onCancelled();
}
