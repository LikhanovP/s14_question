package com.rosa.swift.core.business.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rosa.swift.core.business.DeliverySearch;
import com.rosa.swift.core.business.Session;
import com.rosa.swift.core.network.json.sap.photoBase.PhotoDocumentRequest;
import com.rosa.swift.core.network.json.sap.queue.QueueRequest;
import com.rosa.swift.core.network.json.sap.security.SendEventRequest;
import com.rosa.swift.core.network.json.sap.userdata.UserDataResponse;
import com.rosa.swift.core.network.requests.chat.ChatMessageRequest;
import com.rosa.swift.core.network.requests.chat.RoomRequest;
import com.rosa.swift.core.network.requests.delivery.ChangeDeliveryRequest;
import com.rosa.swift.core.network.requests.function.CallRequest;
import com.rosa.swift.core.network.requests.function.GateRequest;
import com.rosa.swift.core.network.requests.function.WaistRequest;
import com.rosa.swift.core.network.requests.logon.UserDataRequest;
import com.rosa.swift.core.network.requests.message.DriverMessageRequest;
import com.rosa.swift.core.network.requests.price.PriceRouteRequest;
import com.rosa.swift.core.network.requests.price.TransportLocationsRequest;
import com.rosa.swift.core.network.requests.quest.DriverAnswerRequest;
import com.rosa.swift.core.network.requests.rating.CupPhotoRequest;
import com.rosa.swift.core.network.requests.rating.DocumentPhotoRequest;
import com.rosa.swift.core.network.requests.structure.PlantsRequest;
import com.rosa.swift.core.network.requests.terminal.PosTerminalDataRequest;
import com.rosa.swift.core.network.requests.terminal.RegisterPaymentRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.ServiceFunction;
import com.rosa.swift.core.network.services.sap.ServiceParamWrapper;
import com.rosa.swift.core.network.services.sap.ServiceTask;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.network.services.sap.ZMotoService;

public class SapRequestUtils {

    public static Session logon(UserDataRequest requestModel, boolean loginOnly) throws WSException {
        String response = ZMotoService.getInstance().callService(ServiceFunction.Login, requestModel);
        try {
            UserDataResponse responseModel = new Gson().fromJson(response, UserDataResponse.class);
            String errorMessage = responseModel.getErrorMessage();
            if (!TextUtils.isEmpty(errorMessage)) {
                throw new WSException(WSException.ServiceExceptionType.SessionError, errorMessage);
            }
            return new Session(responseModel, loginOnly);
        } catch (JsonSyntaxException e) {
            throw new WSException(WSException.ServiceExceptionType.DataError, e.getMessage());
        }
    }

    public static void logout() throws WSException {
        String response = ZMotoService.getInstance().callService(ServiceFunction.Logout);
        if (!TextUtils.isEmpty(response)) {
            throw new WSException(WSException.ServiceExceptionType.LogoutFailed, response);
        }
    }

    public static void getQueue(QueueRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetQueue, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void assignDelivery(ChangeDeliveryRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.AssignDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void finishDelivery(ChangeDeliveryRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.FinishDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void updateDelivery(ChangeDeliveryRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.UpdateDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void rejectDelivery(ChangeDeliveryRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.RejectDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void printDelivery(ChangeDeliveryRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.PrintDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getQueueNum(ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetQueueNum, events);
        new ServiceTask().execute(spw);
    }

    public static void startWaisting(WaistRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.StartWaisting, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void endWaisting(WaistRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.EndWaisting, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void sendDriverMessage(DriverMessageRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendMessage, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void sendDriverAnswer(DriverAnswerRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendAnswerQuest, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void openGate(GateRequest requestModel, ServiceCallback events) throws WSException {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GateOpen, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void delayCup(ServiceCallback events) throws WSException {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.DelayCup, events);
        new ServiceTask().execute(spw);
    }

    public static void getCupTasks(ServiceCallback events) throws WSException {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetCupTasks, events);
        new ServiceTask().execute(spw);
    }

    public static void getCupStatus(ServiceCallback events) throws WSException {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetCupStatus, events);
        new ServiceTask().execute(spw);
    }

    public static void getPhotoDocument(PhotoDocumentRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetPhotoDocument, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void sendTransportLocations(TransportLocationsRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendTransportLocation, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void markRoomAsRead(RoomRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.MarkRoomAsRead, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getChatRooms(RoomRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetChatRooms, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getChatRoomComplete(RoomRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetChatRoomComplete, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void addChatMessage(ChatMessageRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.AddChatMessage, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getTypeTransports(String tplstCode, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetTypeTransports, tplstCode, events);
        new ServiceTask().execute(spw);
    }

    public static void getPriceRoute(PriceRouteRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetPriceRoute, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getDriverRecordInfo(String recordId, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetDriverRecordInfo, recordId, events);
        new ServiceTask().execute(spw);
    }

    public static void getCupSet(String cupId, ServiceCallback events) {
        ServiceParamWrapper swp = new ServiceParamWrapper(ServiceFunction.GetCupSet, cupId, events);
        new ServiceTask().execute(swp);
    }

    public static void getAllCupForPeriod(ServiceCallback events) {
        ServiceParamWrapper swp = new ServiceParamWrapper(ServiceFunction.GetAllCupForPeriod, events);
        new ServiceTask().execute(swp);
    }

    public static void getDriverRecords(ServiceCallback events) {
        ServiceParamWrapper swp = new ServiceParamWrapper(ServiceFunction.GetDriverRecords, events);
        new ServiceTask().execute(swp);
    }

    public static void getAllDocsList(String deliveryNumber, ServiceCallback events) {
        ServiceParamWrapper swp = new ServiceParamWrapper(ServiceFunction.GetAllDocsList, deliveryNumber, events);
        new ServiceTask().execute(swp);
    }

    //TODO: ipopov 25.03.2017 создать для DeliverySearch.SearchOptions модель запроса
    public static void findDelivery(DeliverySearch.SearchOptions requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.FindDelivery, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void requestCall(CallRequest requestModel) {
        ServiceParamWrapper swp = new ServiceParamWrapper(ServiceFunction.Call, requestModel, null);
        new ServiceTask().execute(swp);
    }

    public static void getTowns(ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetPlaces, events, "dump param");
        new ServiceTask().execute(spw);
    }

    public static void getPlants(PlantsRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetPlaces, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void getPosTerminalData(PosTerminalDataRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.GetPosTerminalData, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void registerPayment(RegisterPaymentRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.RegisterPayment, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void sendEvent(SendEventRequest requestModel) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendEvent, requestModel, null);
        new ServiceTask().execute(spw);
    }

    public static void sendDriverDocumentPhoto(DocumentPhotoRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendDriverDocumentPhoto, requestModel, events);
        new ServiceTask().execute(spw);
    }

    public static void sendDriverCupPhoto(CupPhotoRequest requestModel, ServiceCallback events) {
        ServiceParamWrapper spw = new ServiceParamWrapper(ServiceFunction.SendDriverCupPhoto, requestModel, events);
        new ServiceTask().execute(spw);
    }

}
