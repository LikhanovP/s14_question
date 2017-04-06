package com.rosa.swift.core.data.dto.settings;

/**
 * Created by yalang on 23.01.2015.
 */
public class ChatSettings {

    private NotificationType mNotificationType = NotificationType.Sound;

    public NotificationType getNotificationType() {
        return mNotificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        mNotificationType = notificationType;
    }

    public ChatSettings(NotificationType notificationType) {
        mNotificationType = notificationType;
    }

    public enum NotificationType {

        /**
         * Без уведомления
         */
        NoNotification(0),
        /**
         * Только вибрация
         */
        Vibrate(1),
        /**
         * Вибрация и звук
         */
        Sound(2);

        private int mValue;

        NotificationType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static NotificationType getType(int value) {
            for (NotificationType type : NotificationType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return Sound;
        }

    }

}
