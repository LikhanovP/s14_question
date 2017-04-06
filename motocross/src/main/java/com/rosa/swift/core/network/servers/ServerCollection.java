package com.rosa.swift.core.network.servers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.services.sap.ZMotoService;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.rosa.motocross.R.xml.servers;

/**
 * Представялет класс-менеджер для управления коллекцией настроек SAP-серверов.
 */
public class ServerCollection {

    /*Имена серверов*/
    public static final String CUSTOM_SERVER = "Custom";

    /*Десериализация*/
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PORT = "port";
    private static final String ORDER = "order";
    private static final String SERVER = "server";

    //Instance класса
    private static ServerCollection sServerManager = null;

    //Коллекция настроек серверов
    private final List<Server> sServers = new ArrayList<>();

    /**
     * Возвращает текущую сущность класса
     *
     * @return Класс-менеджер для управления коллекцией настроек SAP-серверов
     */
    public static ServerCollection getInstance() {
        if (sServerManager == null) {
            sServerManager = new ServerCollection(SwiftApplication.getApplication());
            sServerManager.sortByOrder();
        }

        return sServerManager;
    }

    /**
     * Инициализирует новый экземпляр класса
     *
     * @param context
     */
    private ServerCollection(Context context) {
        deserialize(context);
    }

    /**
     * Добавляет сервер в коллекцию по заданным настройкам
     *
     * @param name    Имя сервера
     * @param address Адрес сервера
     * @param order   Приоритет сервера
     */
    public void addServer(String name, String address, int order) {
        //парсинг адреса, отделяем порт, если он задан, от IP
        String separator = ":";
        String ipAddress = "";
        int port = 0;

        //по наличию сепаратора определяем, задан ли порт
        if (address.contains(separator)) {
            int sepPos = address.indexOf(separator);
            port = Integer.parseInt(address.substring(sepPos + 1).trim());
            ipAddress = address.substring(0, sepPos).trim();
        } else {
            ipAddress = address.trim();
        }

        addServer(name, ipAddress, port, order);
    }

    /**
     * Добавляет сервер в коллекцию по заданным настройкам
     *
     * @param name      Имя сервера
     * @param ipAddress IP-адрес сервера
     * @param port      Порт сервера
     * @param order     Приоритет сервера
     */
    public void addServer(String name, String ipAddress, int port, int order) {
        addServer(new Server(name, ipAddress, port, order));
    }

    /**
     * Добавляет указанный сервер в коллекцию
     *
     * @param newServer Добавляемый сервер
     */
    private void addServer(Server newServer) {
        if (newServer != null) {
            for (Server server : sServers) {
                if (newServer.getName().equals(server.getName())) {
                    return;
                }
            }

            sServers.add(newServer);
            sortByOrder();
        }
    }

    /**
     * Возвращает список адресов для заданных SAP-серверов
     *
     * @return Массив адресов SAP-серверов
     */
    public String[] getServerAddresses() {
        List<String> addresses = new ArrayList<>();

        for (Server server : sServers) {
            String address = server.getAddress();
            if (!StringUtils.isNullOrEmpty(address)) {
                addresses.add(address);
            }
        }

        return addresses.toArray(new String[addresses.size()]);
    }

    /**
     * Обновляет настройки сервера по указанному имени. Если для сервера не задан
     * адресс, то он удаляется из коллекции
     *
     * @param name    Имя сервера
     * @param address Адрес сервера
     * @param order   Приоритет сервера
     */
    public void updateServer(String name, String address, int order) {
        removeServer(name);
        if (!StringUtils.isNullOrEmpty(address)) {
            addServer(name, address, order);
        }
        sortByOrder();

        ZMotoService.getInstance().resetServers();
    }

    /**
     * Удаляет сервер из коллекции по заданному имени
     *
     * @param name
     */
    public void removeServer(String name) {
        Iterator<Server> iterator = sServers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equals(name)) {
                iterator.remove();
            }
        }
    }

    /**
     * Выполняет сортировку списка серверов по приоритету
     */
    private void sortByOrder() {
        Collections.sort(sServers, (server1, server2) ->
                ((Integer) server1.getOrder()).compareTo(server2.getOrder()));
    }

    /**
     * Десериализует настройки серверов из XML-файла
     *
     * @param context
     */
    private void deserialize(Context context) {
        try {
            XmlResourceParser xml = context.getResources().getXml(servers);
            try {
                if (xml != null) {
                    while (xml.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xml.getEventType() == XmlPullParser.START_TAG) {
                            if (xml.getName().equals(SERVER)) {
                                String name = xml.getAttributeValue(null, NAME);
                                String ipAddress = xml.getAttributeValue(null, ADDRESS);
                                int port = xml.getAttributeIntValue(null, PORT, 0);
                                int order = xml.getAttributeIntValue(null, ORDER, 0);

                                Server server = new Server(name, ipAddress, port, order);
                                addServer(server);
                            }
                        }
                        xml.next();
                    }
                }
            } catch (Throwable throwable) {
                Log.e("Не удалось загрузить список SAP-серверов", throwable);
            } finally {
                if (xml != null) {
                    xml.close();
                }
            }
        } catch (Resources.NotFoundException exception) {
            Log.e("На найдены настройки SAP-серверов", exception);
        }
    }

}
