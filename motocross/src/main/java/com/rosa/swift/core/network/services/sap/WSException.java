package com.rosa.swift.core.network.services.sap;

import com.rosa.swift.core.business.utils.Log;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yalang on 05.08.13.
 */
public class WSException extends Exception {

    //todo новые типы ошибок
    public enum ServiceExceptionType {
        Unknown("Неизвестная ошибка. Обратитесь к разработчику"),
        SAPUnreachable("Сервер не доступен"),
        NetworkFailure("Невозможно соединиться с сервером"),
        AuthInvalid("Неверное имя пользователя или пароль"),
        InternalSAPError("Внутренняя ошибка сервера"),
        VersionMissmatch("Приложение устарело. Требуется обновление"),
        DataError("Ошибка. Некорректные данные"),
        SessionError("Не удалось создать сессию"),
        LogoutFailed("Ошибка завершения сессии");

        private String msg;

        ServiceExceptionType(String msg) {
            this.msg = msg;
        }
    }

    private ServiceExceptionType mType;

    public WSException(ServiceExceptionType type, String message) {
        super(message);
        mType = type;
    }

    public WSException(Exception e) {
        super(e);

        mType = ServiceExceptionType.Unknown;

        try {
            if (e.getClass() == IOException.class) {
                Pattern p = Pattern.compile(".*HTTP status: (\\d+)$");
                Matcher m = p.matcher(e.getMessage());
                if (m.matches()) {
                    int status = Integer.decode(m.group(1));
                    switch (status) {
                        case 400:
                            mType = ServiceExceptionType.InternalSAPError;
                        case 401:
                            mType = ServiceExceptionType.AuthInvalid;
                            break;
                        case 404:
                            mType = ServiceExceptionType.SAPUnreachable;
                            break;
                        case 500:
                            mType = ServiceExceptionType.InternalSAPError;
                            break;
                    }
                } else {
                    mType = ServiceExceptionType.NetworkFailure;
                }
            }
            //todo отдельная реакция на кадый вид ошибки?
            else if (e.getClass() == SocketException.class) {
                mType = ServiceExceptionType.NetworkFailure;
            } else if (e.getClass() == UnknownHostException.class) {
                mType = ServiceExceptionType.NetworkFailure;
            } else if (e.getClass() == SocketTimeoutException.class) {
                mType = ServiceExceptionType.NetworkFailure;
            } else if (e != null) {
                mType = ServiceExceptionType.NetworkFailure;
            } else {
                if (e.getMessage().contains("VersionMissmatch")) {
                    mType = ServiceExceptionType.VersionMissmatch;
                } else {
                    mType = ServiceExceptionType.Unknown;
                }
            }
        } catch (Exception ex) {
            Log.e(ex.getMessage(), ex);
            mType = ServiceExceptionType.Unknown;
        }
    }

    @Override
    public String getMessage() {
        return mType.msg;
        //return getFullMessage();
    }

    public String getFullMessage() {
        return super.getMessage();
    }

    public ServiceExceptionType getType() {
        return mType;
    }

}

