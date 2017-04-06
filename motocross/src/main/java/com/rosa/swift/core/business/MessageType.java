package com.rosa.swift.core.business;

import android.text.TextUtils;

/**
 * Представляет тип сообщения от сервера
 */
public enum MessageType {
    Default(""),
    SessionExpired("SEXP"),
    ConnectionLost("CLST"),
    SAPUnreachable("NSAP"),
    QueueNum("QNUM"),
    SendMeLogs("LOGS"),
    Template("TMPL"),
    StuffLoaded("STUFF"),
    ChatRoom("CHRM"),
    ChatMessage("CHMG"),

    /**
     * Добавление доставки в список доступных доставок
     */
    DeliveryAdd("DLV+"),
    /**
     * Удаление доставки из списка доступных доставок
     */
    DeliveryRemove("DLV-"),
    /**
     * Назначение доставки при выходе на смену
     */
    DeliveryCurrent("_DLV"),
    /**
     * Ручное назначение доставки
     */
    DeliveryAssign("DLV_"),
    /**
     * Ручное снятие доставки
     */
    DeliveryUnassign("DLV~"),
    /**
     * Обновление информации по доставке
     */
    DeliveryUpdate("DLV*"),
    /**
     * Назначение перемещения при выходе на смену
     */
    RelocationCurrent("RLC"),
    /**
     * Ручное назначение перемещения
     */
    RelocationAssign("RLC+"),
    /**
     * Ручное снятие перемещения
     */
    RelocationUnassign("RLC-"),
    /**
     * Обновление информации по перемещению
     */
    RelocationUpdate("RLC*");

    private String mValue;

    MessageType(String type) {
        this.mValue = type;
    }

    @Override
    public String toString() {
        return mValue;
    }

    public static MessageType fromString(String type) {
        if (!TextUtils.isEmpty(type)) {
            for (MessageType messageType : MessageType.values()) {
                if (type.equals(messageType.mValue)) {
                    return messageType;
                }
            }
        }
        return Default;
    }

}
