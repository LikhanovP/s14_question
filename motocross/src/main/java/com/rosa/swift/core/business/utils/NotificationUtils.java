package com.rosa.swift.core.business.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import com.rosa.motocross.R;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.settings.ChatSettings;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.ui.activities.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by yalang on 23.09.13.
 */
public class NotificationUtils {

    private static NotificationUtils instance;

    private WeakReference _context;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private SparseArray<Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private NotificationUtils(Context context) {
        _context = new WeakReference<>(context);
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new SparseArray<>();
    }

    /**
     * Получение ссылки на синглтон
     */
    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            instance._context = new WeakReference<>(context);
        }
        return instance;
    }

    //простое уведомление о новых сообщениях
    public int createChatNewMessageNotification(JChatMessage message) {
        if (_context == null) return -1;
        try {
            ChatSettings settings = DataRepository.getInstance().getChatSettings();
            Context cntx = (Context) _context.get();
            if (cntx == null) return -1;
            Intent toLaunch = new Intent(cntx.getApplicationContext(), MainActivity.class);
            if (StringUtils.isNullOrEmpty(message.message_type)) {
                toLaunch.setAction("com.rosa.motocross.chat.open");
                toLaunch.putExtra("room_id", message.room_id);
            } else {
                toLaunch.setAction("android.intent.action.MAIN");
            }
            PendingIntent intentBack = PendingIntent.getActivity(cntx, 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder nb = new NotificationCompat.Builder(cntx)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true) //уведомление закроется по клику на него
                    .setTicker("Новые сообщение в Стриж.Чат") //текст, который отобразится вверху статус-бара при создании уведомления
                    .setContentText(message.text) // Основной текст уведомления
                    .setContentIntent(intentBack)
                    .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                    .setContentTitle(cntx.getString(R.string.notification_title)); //заголовок уведомления
            switch (settings.getNotificationType()) {
                case NoNotification:
                    //nb.setVibrate(null);
                    //nb.setSound(null);
                    break;
                case Vibrate:
                    nb.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                    break;
                case Sound:
                    nb.setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию
                    break;
            }
            Notification notification = nb.build(); //генерируем уведомление
            int id = lastId++; //getId("Новые сообщение в Стриж.Чат");
//            if (id == -1){
//                id = lastId++;
//            }
            manager.notify(id, notification); // отображаем его пользователю.
            notifications.put(id, notification); //теперь мы можем обращаться к нему по id
            return id;
        } catch (Exception ignored) {
            return -1;
        }
    }

    //простое уведомление о новых сообщениях
    public int createChatNewMessagesInRoomsNotification() {
        if (_context == null) return -1;
        try {
            ChatSettings settings = DataRepository.getInstance().getChatSettings();
            Context cntx = (Context) _context.get();
            if (cntx == null) return -1;
            Intent toLaunch = new Intent(cntx.getApplicationContext(), MainActivity.class);
            toLaunch.setAction("com.rosa.motocross.chat.open");
            PendingIntent intentBack = PendingIntent.getActivity(cntx, 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder nb = new NotificationCompat.Builder(cntx)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true) //уведомление закроется по клику на него
                    .setTicker("Новые сообщение в Стриж.Чат") //текст, который отобразится вверху статус-бара при создании уведомления
                    .setContentText("Новые сообщение в Стриж.Чат") // Основной текст уведомления
                    .setContentIntent(intentBack)
                    .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                    .setContentTitle(cntx.getString(R.string.notification_title)); //заголовок уведомления
            switch (settings.getNotificationType()) {
                case NoNotification:
                    //nb.setVibrate(null);
                    //nb.setSound(null);
                    break;
                case Vibrate:
                    nb.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                    break;
                case Sound:
                    nb.setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию
                    break;
            }
            Notification notification = nb.build(); //генерируем уведомление
            int id = getId("Новые сообщение в Стриж.Чат");
            if (id == -1) {
                id = lastId++;
            }
            manager.notify(id, notification); // отображаем его пользователю.
            notifications.put(id, notification); //теперь мы можем обращаться к нему по id
            return id;
        } catch (Exception ignored) {
            return -1;
        }
    }

    public int createInfoNotification(String message) {
        return createInfoNotification(message, false);
    }

    public int createInfoNotification(String message, boolean isOngoingEvent) {
        if (_context == null) return -1;
        try {
            Context context = (Context) _context.get();
            if (context == null) return -1;
            PackageManager pm = context.getPackageManager();
            if (pm == null) return -1;
            Intent notificationIntent = pm.getLaunchIntentForPackage(context.getPackageName());

            NotificationCompat.BigTextStyle textStyle =
                    new NotificationCompat.BigTextStyle().bigText(message);

            NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true) //уведомление закроется по клику на него
                    .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                    .setStyle(textStyle) //отображение большого текста
                    .setContentText(message) //основной текст уведомления
                    .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, 0))
                    .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                    .setContentTitle(context.getString(R.string.notification_title)) //заголовок уведомления
                    .setOngoing(isOngoingEvent) //уведомление должно быть закреплено
                    .setDefaults(Notification.DEFAULT_ALL); //звук, вибро и диодный индикатор выставляются по умолчанию

            Notification notification = nb.build(); //генерируем уведомление

            int id = getId(message);
            if (id == -1) {
                id = lastId++;
            }
            manager.notify(id, notification); // отображаем его пользователю.
            notifications.put(id, notification); //теперь мы можем обращаться к нему по id
            return id;
        } catch (Exception ignored) {
            return -1;
        }
    }

    private int getId(String message) {
        try {
            for (int i = 0; i < notifications.size(); i++) {
                Notification n = notifications.valueAt(i);
                if (n.tickerText != null && n.tickerText.equals(message))
                    return notifications.keyAt(i);
            }
        } catch (Exception ignored) {
        }
        return -1;
    }
}