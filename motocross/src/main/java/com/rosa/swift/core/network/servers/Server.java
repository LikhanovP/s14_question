package com.rosa.swift.core.network.servers;

import com.rosa.swift.core.business.utils.StringUtils;

/**
 * Предоставляет настройки SAP-сервера
 */
public class Server {

    //Имя сервера
    private String mName;
    //IP-адрес сервера
    private String mIpAddress;
    //Порт сервера
    private int mPort;
    //Приоритет сервера
    private int mOrder;

    /**
     * Инициализирует экземпляр класса настроек SAP-сервера
     *
     * @param name      Имя сервера
     * @param ipAddress IP-адрес сервера
     * @param port      Порт сервера
     */
    public Server(String name, String ipAddress, int port) {
        this(name, ipAddress, port, 10);
    }

    /**
     * Инициализирует экземпляр класса настроек SAP-сервера
     *
     * @param name      Имя сервера
     * @param ipAddress IP-адрес сервера
     * @param port      Порт сервера
     * @param order     Приоритет сервера
     */
    public Server(String name, String ipAddress, int port, int order) {
        mName = name.trim();
        mIpAddress = ipAddress.trim();
        mPort = port;
        mOrder = order;
    }

    /**
     * Возвращает имя сервера
     */
    public String getName() {
        return mName;
    }

    /**
     * Возвращает IP-адрес сервера
     */
    public String getIpAddress() {
        return mIpAddress;
    }

    /**
     * Возвращает порт сервера
     */
    public int getPort() {
        return mPort;
    }

    /**
     * Возвращает приоритет сервера
     */
    public int getOrder() {
        return mOrder;
    }

    /**
     * Возвращает полный адрес сервера
     */
    public String getAddress() {
        return mPort > 0 ? String.format("%1$s:%2$s", mIpAddress, mPort) : mIpAddress;
    }

    @Override
    public String toString() {
        return !StringUtils.isNullOrEmpty(mName) ? String.format("%1$s [%2$s]", mName, getAddress())
                : getAddress();
    }
}
