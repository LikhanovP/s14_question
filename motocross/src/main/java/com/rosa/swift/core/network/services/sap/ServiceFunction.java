package com.rosa.swift.core.network.services.sap;

/**
 * Created by inurlikaev on 31.07.13.
 */
public enum ServiceFunction {
    //CheckForUpdates("CHECK_FOR_UPDATES", 20),
    //GetIdleTime("GET_IDLE_TIME", 20),
    //GetReasons("GET_REASONS", 20),
    GetQueue("QUEUE", 300),
    Login("LOGIN", 20),
    Logout("LOGOUT", 20),
    AssignDelivery("ASSIGN_DLV", 20),
    FinishDelivery("FINISH_DLV", 20),
    UpdateDelivery("UPDATE_DLV", 20),
    RejectDelivery("UNASSIGN_DLV", 20),
    PrintDelivery("PRINT", 20),
    /**
     * Получить список мест планирования
     */
    GetPlaces("GET_STRUCT", 20),
    GetQueueNum("QUEUE_NUM", 20),
    Logs("LOGS", 20),
    Incident("INCIDENT", 300),
    Call("CALL", 20),
    StartWaisting("WAIST_START", 20),
    EndWaisting("WAIST_END", 20),
    /**
     * Получить список типов транспорта
     */
    GetTypeTransports("GET_TYPE_AUTO", 30),
    GetSapSessionInfo("GET_INFO", 100),
    /**
     * Открыть ворота
     */
    GateOpen("GATE_OPEN", 20),
    /**
     * Отложить фотосессию
     */
    DelayCup("DELAY_CUP", 20),
    /**
     * Получить стоимость маршрута
     */
    GetPriceRoute("GET_COST", 20),
    /**
     * Отправить местоположение транспорта
     */
    SendTransportLocation("SAVE_DRIVER_LOCATION", 20),
    SendMessage("SEND_MESSAGE", 20),
    SendAnswerQuest("ANSWER_QUEST", 20),
    FindDelivery("FIND_DLV", 20),
    GetChatRoomComplete("GET_ROOM", 5),
    GetChatRooms("GET_ROOMS", 5),
    AddChatMessage("ADD_CHAT_MESSAGE", 5),
    MarkRoomAsRead("MARK_AS_READ", 5),
    GetPosTerminalData("GET_TERMINAL_DATA", 5),
    RegisterPayment("REGISTER_PAYMENT", 20),
    GetAllCupForPeriod("GET_ALL_CUP_FOR_PERIOD", 100),
    GetCupSet("GET_CUP_SET", 5),
    GetDriverRecords("GET_DRIVER_RECORDS", 5),
    GetDriverRecordInfo("GET_DRIVER_RECORD_INFO", 5),
    /**
     * Получить статус запланированной фотосессии ЦУП
     */
    GetCupStatus("GET_NEXT_CUP", 20),
    /**
     * Получить список задач для запланированной фотосессии ЦУП
     */
    GetNextCupTasks("GET_CUP_TASK", 20),
    RefreshPhotoDoc("REFRESH_PHOTO_DOC", 20),
    GetCupTasks("GET_CUP_TASK", 20),
    /**
     * Получить дату сервера
     */
    GetSapDate("GET_CURRENT_DATETIME", 100),
    GetPhotoDocument("GET_PHOTO_DOC", 100),
    GetCupPhoto("GET_CUP_PHOTO", 100),
    SendDriverCupPhoto("SAVE_DRIVER_CUP", 300),
    SendDriverDocumentPhoto("SAVE_DRIVER_PHOTO_DOC", 300),
    GetCupViewsPhoto("GET_CUP_VIEWS_PHOTO", 100),
    SendEvent("SEND_EVENT", 5),
    SecurityCheckSave("SECURITY_CHECK_SAVE", 50),
    GetBannedAppList("GET_BANNED_APP_LIST", 10),
    GetLocationSchema("GET_LOCATION_SCHEMA", 300),
    GetAllDocsList("GET_ALL_DOCS_LIST", 200),
    GetDocument("GET_DOCUMENT", 300);

    private String mValue;

    /**
     * Таймаут выполнения в секундах
     */
    private int mTimeout;

    public int getTimeout() {
        return mTimeout;
    }

    @Override
    public String toString() {
        return mValue;
    }

    ServiceFunction(String value, int timeout) {
        mValue = value;
        mTimeout = timeout;
    }

}
