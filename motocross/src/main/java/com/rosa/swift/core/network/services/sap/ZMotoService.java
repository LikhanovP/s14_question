package com.rosa.swift.core.network.services.sap;


import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.network.servers.ServerCollection;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class ZMotoService {

    private static ZMotoService sInstance;

    private final String mUserName = "STRIZH";
    private final String mUserPwd = "123qweasd";

    private String[] mServers = {"", ""};

    private int mDefaultServer = 0;
    private int mRepeatCnt;

    private ZSvcHelper mSvcHelper;

    public static ZMotoService getInstance() {
        if (sInstance == null) {
            sInstance = new ZMotoService();
        }
        return sInstance;
    }

    private ZMotoService() {
        mRepeatCnt = 1;
        mSvcHelper = new ZSvcHelper();
        mSvcHelper.setCompression(true);
        resetServers();
    }

    public synchronized void resetServers() {
        mDefaultServer = 0;
        String[] servers = ServerCollection.getInstance().getServerAddresses();
        resetServers(servers);
    }

    private void resetServers(String[] sapServers) {
        mServers = new String[0];
        if (sapServers != null) {
            mServers = new String[sapServers.length];

            for (int i = 0; i < sapServers.length; i++) {
                mServers[i] = sapServers[i] != null ? sapServers[i] : "";
            }
        }
    }

    private String convertToJason(Object requestModel) throws JsonIOException {
        return new Gson().toJson(requestModel, requestModel.getClass());
    }

    public String callService(ServiceFunction svcFunction) throws WSException {
        return this.callService(svcFunction, DataRepository.getInstance().getSessionId());
    }

    public String callService(ServiceFunction svcFunction, Object requestModel)
            throws WSException, JsonIOException {
        return this.callService(svcFunction, convertToJason(requestModel));
    }

    public String callService(ServiceFunction svcFunction, String ivParams) throws WSException {
        //не логируем отправку логов, чтобы не получилось масло-масленное
        if ((svcFunction != ServiceFunction.Logs) && (svcFunction != ServiceFunction.Incident))
            Log.i(String.format("Service call: %s, params %s", svcFunction.toString(), ivParams));

        //счетчик серверов
        int serverRepeats = 0;
        //счетчик повторов для серевера
        int repeats;
        while (serverRepeats != mServers.length) {
            serverRepeats++;
            repeats = 0;
            mSvcHelper.setConnection(getConnectionString(), mUserName, mUserPwd);
            mSvcHelper.setIvVersion(SwiftApplication.getVersion());
            while (repeats != mRepeatCnt) {
                repeats++;
                try {
                    String ret = mSvcHelper.CallService(svcFunction, ivParams);
                    Log.i(String.format("Service call: %s, result %s", svcFunction.toString(), ret));
                    return ret;
                } catch (SocketException | UnknownHostException | SocketTimeoutException sex) {
                    if (!onIOException(serverRepeats, repeats, sex))
                        throw new WSException(sex);
                } catch (IOException iex) {
                    if (iex.getMessage().contains("HTTP status:"))
                        throw new WSException(iex);
                    else if (!onIOException(serverRepeats, repeats, iex))
                        throw new WSException(iex);
                } catch (Exception ex) {
                    throw new WSException(ex);
                }
            }
        }

        return null;
    }

    private boolean onIOException(int serverRepeats, int repeats, @SuppressWarnings("unused") Exception e) {
        if (repeats == mRepeatCnt) {
            if (serverRepeats != mServers.length) {
                synchronized (ZMotoService.class) {
                    if (mDefaultServer != (mServers.length - 1))
                        mDefaultServer++;
                    else
                        mDefaultServer = 0;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private String getConnectionString() {
        return "http://" + mServers[mDefaultServer] + "/zr_driver/service";
    }

    public void cancelRequest() {
        if (mSvcHelper != null)
            mSvcHelper.cancelRequest();
    }

}
