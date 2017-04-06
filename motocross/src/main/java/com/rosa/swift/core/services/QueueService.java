package com.rosa.swift.core.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.Message;
import com.rosa.swift.core.business.MessageType;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.NotificationUtils;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.SecurityUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;
import com.rosa.swift.core.network.json.sap.common.JMessage;
import com.rosa.swift.core.network.json.sap.queue.QueueRequest;
import com.rosa.swift.core.network.json.sap.queue.QueueResponse;
import com.rosa.swift.core.network.json.sap.security.AppListItem;
import com.rosa.swift.core.network.json.sap.security.JCheckResult;
import com.rosa.swift.core.network.json.sap.security.JCheckType;
import com.rosa.swift.core.network.json.sap.security.JGetBannedAppsIn;
import com.rosa.swift.core.network.json.sap.security.JGetBannedAppsOut;
import com.rosa.swift.core.network.json.sap.security.SecurityTask;
import com.rosa.swift.core.network.json.sap.security.SendEventRequest;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.network.requests.log.LogRequest;
import com.rosa.swift.core.network.requests.logon.UserDataRequest;
import com.rosa.swift.core.network.services.sap.ServiceFunction;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.network.services.sap.WSException.ServiceExceptionType;
import com.rosa.swift.core.network.services.sap.ZMotoService;
import com.rosa.swift.core.ui.activities.MainActivity.MainActivityState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class QueueService extends Service implements Runnable {

    private final IBinder mBinder = new LocalBinder();
    private final List<ServiceMessage> mMessages = new ArrayList<>();

    private Timer mIdleTimer = null;
    private Thread mThread;
    private PowerManager.WakeLock mWakeLock;

    private DataRepository mDataRepository;
    private ZMotoService mMotoService;

    private MainActivityState mMainActivityState;

    private boolean mShowNotifications = false;
    private boolean mTryToLogout;

    @Override
    public void onCreate() {
        super.onCreate();
        mMotoService = ZMotoService.getInstance();
        mDataRepository = DataRepository.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Сервис СТРИЖ запущен");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //saveService();
        stopListening();
    }

    @Override
    public void run() {
        boolean sendNetworkError = true;
        while (isContinuedThread()) {
            try {
                if (isConnected()) {
                    clearMessages();
                    QueueRequest request = new QueueRequest(mDataRepository.getSession());
                    String response = mMotoService.callService(ServiceFunction.GetQueue, request);
                    //there is a connection -> next time can send network error
                    sendNetworkError = true;
                    if (isContinuedThread()) {
                        processQueueMessages(response);
                    } else {
                        Log.i("Thread interrupted - out");
                    }
                } else {
                    if (sendNetworkError) {
                        throw new WSException(ServiceExceptionType.NetworkFailure, "No connection");
                    }
                    try {
                        Thread.sleep(2000);
                        //no connection - let's have a rest
                    } catch (Exception ignored) {
                    }
                }
            } catch (WSException serviceException) {
                if (isContinuedThread() && sendNetworkError) {
                    switch (serviceException.getType()) {
                        case NetworkFailure:
                        case SAPUnreachable:
                            processServiceException(serviceException);
                            sendNetworkError = false;
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void processServiceException(WSException serviceException) {
        MessageType type = serviceException.getType() ==
                ServiceExceptionType.SAPUnreachable ?
                MessageType.SAPUnreachable : MessageType.ConnectionLost;
        Message message = new Message(type);
        mMessages.add(new ServiceMessage(message));
        broadcastMessage(message);
        try {
            Thread.sleep(2000);
            //no connection - let's have a rest
        } catch (Exception ignored) {
        }
    }

    private void processQueueMessages(String responseString) {
        if (!TextUtils.isEmpty(responseString) && !responseString.equals("{}")) {
            QueueResponse responseModel = new Gson().fromJson(responseString, QueueResponse.class);
            String errorMessage = responseModel.getErrorMessage();
            if (!TextUtils.isEmpty(errorMessage) && errorMessage.equals("NO SESSION")) {
                //session is dead
                if (!mTryToLogout) {
                    Message message = new Message(MessageType.SessionExpired);
                    mMessages.add(new ServiceMessage(message));
                    broadcastMessage(message);
                }
            } else {
                //everything fine
                mDataRepository.setLastMessageQueueId(responseModel.getLastQueueId());
                List<JMessage> messages = responseModel.getMessages();
                if (messages != null && messages.size() != 0) {
                    for (JMessage jm : messages) {
                        Message message = new Message(jm);
                        mMessages.add(new ServiceMessage(message));
                        broadcastMessage(message);
                    }
                }
            }
        }
    }

    public void setMainActivityState(MainActivityState mainActivityState) {
        mMainActivityState = mainActivityState;
    }

    public void resetLastId() {
        if (mDataRepository.getSessionId() != null) {
            stopListening();
            mDataRepository.setLastMessageQueueId(Constants.QUEUE_LAST_MESSAGE_ID);
            startListening();
        }
    }

    public void messageReceived(Message m) {
        synchronized (mMessages) {
            for (ServiceMessage message : mMessages) {
                if (message.getMessage().equals(m)) {
                    message.mReceived = true;
                }
            }
        }
    }

    public List<Message> getUnreceivedMessages() {
        List<Message> result = new ArrayList<>();
        synchronized (mMessages) {
            for (ServiceMessage serviceMessage : mMessages) {
                if (!serviceMessage.isReceived()) {
                    result.add(serviceMessage.getMessage());
                    serviceMessage.setReceived(true);
                }
            }
        }
        return result;
    }

    public void setShowNotifications(boolean value) {
        mShowNotifications = value;
        try {
            if (mShowNotifications && mMainActivityState.equals(MainActivityState.Online)) {
                mIdleTimer = new Timer();
                mIdleTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            QueueService.this.endCurrentSession();
                        } catch (Exception ignored) {
                            QueueService.this.endCurrentSessionOffline();
                        }
                        Message message = new Message(MessageType.SessionExpired);
                        mMessages.add(new ServiceMessage(message));
                        broadcastMessage(message);
                        Log.i("Idle time logout");
                    }
                }, mDataRepository.getIdleTimeInMilliseconds());
            } else {
                if (mIdleTimer != null) {
                    mIdleTimer.cancel();
                    mIdleTimer = null;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void startNewSession() throws WSException {
        startNewSessionInternal(false);
    }

    public void startNewCabinetSession() throws WSException {
        startNewSessionInternal(true);
    }

    public void endCurrentSession() throws WSException {
        if (!TextUtils.isEmpty(mDataRepository.getSessionId())) {
            mTryToLogout = true;
            SapRequestUtils.logout();
            stopListening();
            mDataRepository.removeSession();
            mDataRepository.clearTemporaryData();
            mTryToLogout = false;
        }
    }

    public void endCurrentSessionOffline() {
        if (!TextUtils.isEmpty(mDataRepository.getSessionId())) {
            mTryToLogout = true;
            stopListening();
            mDataRepository.removeSession();
            mDataRepository.clearTemporaryData();
            mTryToLogout = false;
        }
    }

    private void startNewSessionInternal(boolean loginOnly) throws WSException {
        if (isRunning()) {
            stopListening();
        }
        String driverCall = mDataRepository.getDriverCall();
        String selectedTownCode = mDataRepository.getSelectedTownCode();
        UserDataRequest request = new UserDataRequest(
                driverCall,
                mDataRepository.getDriverPassword(),
                selectedTownCode,
                getSecurityChecks(driverCall, selectedTownCode));
        mDataRepository.replaceSession(SapRequestUtils.logon(request, loginOnly));
        Completable.fromAction(this::requestSapSessionInfo)
                .subscribeOn(Schedulers.newThread())
                .subscribe();
        sendSecurityEvents();
        startListening();
    }

    private void requestSapSessionInfo() {
        try {
            String response = mMotoService.callService(
                    ServiceFunction.GetSapSessionInfo, mDataRepository.getSessionId());
            DriverSessionResponse responseModel = new Gson().fromJson(response,
                    DriverSessionResponse.class);
            mDataRepository.setDriverSessionData(responseModel);
            Message message = new Message(MessageType.StuffLoaded, String.valueOf(new Random().nextInt()));
            broadcastMessage(message);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void startListening() {
        Log.i("Начинаем прослушку " + mDataRepository.getSessionId());
        setWakeLock();
        //запускаем прослушку
        mThread = new Thread(this);
        mThread.start();
    }

    private void stopListening() {
        releaseWakeLock();
        if (mThread != null) {
            cancelRequest();
            mThread.interrupt();
            mThread = null;
            synchronized (mMessages) {
                mMessages.clear();
            }
            Log.i("Заканчиваем прослушку " + mDataRepository.getSessionId());
        }
    }

    private void setWakeLock() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.rosa.motocross");
            mWakeLock.acquire();
        } catch (Exception exception) {
            Log.e("Не удалось установить блокировку", exception);
        }
    }

    private void releaseWakeLock() {
        try {
            //отпускаем блокировку
            mWakeLock.release();
        } catch (Exception exception) {
            Log.e("Не удалось снять блокировку", exception);
        }
    }

    private void cancelRequest() {
        if (mMotoService != null) {
            mMotoService.cancelRequest();
        }
    }

    private boolean isContinuedThread() {
        return mThread != null && Thread.currentThread().getId() == mThread.getId() &&
                !Thread.currentThread().isInterrupted();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean isRunning() {
        return mThread != null && mThread.isAlive();
    }

    private void clearMessages() {
        List<ServiceMessage> receivedMessages = new ArrayList<>();
        synchronized (mMessages) {
            for (ServiceMessage message : mMessages) {
                if (message.mReceived) {
                    receivedMessages.add(message);
                }
            }
            mMessages.removeAll(receivedMessages);
        }
    }

    private void broadcastMessage(final Message message) {
        Log.i("Broadcast message: " + message.getId() + "/" + message.getType());
        processMessage(message);
        sendNotification(message);
        Intent intent = new Intent(Constants.ACTION_NEW_MESSAGE);
        intent.putExtra(Constants.EXTRA_NEW_MESSAGE, message);
        sendBroadcast(intent);
    }

    private void processMessage(Message message) {
        switch (message.getType()) {
            case SendMeLogs:
                messageReceived(message);
                new Thread(this::sendLogs).run();
                break;
            case SessionExpired:
                sessionExpired();
                break;
            case ChatMessage:
                onNewChatMessage(message.getMessage());
                break;
            case ChatRoom:
                NotificationUtils.getInstance(this).createChatNewMessagesInRoomsNotification();
                break;
        }
    }

    private void onNewChatMessage(String message) {
        try {
            JChatMessage chatMessage = new Gson().fromJson(message, JChatMessage.class);
            NotificationUtils.getInstance(this).createChatNewMessageNotification(chatMessage);
        } catch (Exception exception) {
            Log.e("Не удалось преобразовать сообщение", exception);
        }
    }

    private void sessionExpired() {
        mDataRepository.removeSession();
        Log.i("Сессия устарела");
    }

    private void sendLogs() {
        try {
            mMotoService.callService(ServiceFunction.Logs,
                    new LogRequest(mDataRepository.getSessionId(), CommonUtils.getLogs()));
        } catch (Exception exception) {
            Log.e("Не удалось отправить лог", exception);
        }
    }

    private boolean showDelivery(Delivery delivery) {
        TplstCollection plants = mDataRepository.getPlants();
        if (plants != null && plants.size() > 0 && delivery != null) {
            for (Tplst plant : plants) {
                if (plant.getCode().equals(delivery.getPlantCode())) {
                    return plant.isSelected();
                }
            }
        }
        return true;
    }

    private void sendNotification(Message message) {
        if (mShowNotifications) {
            String notificationText = null;
            switch (message.getType()) {
                case DeliveryAdd:
                    Delivery delivery = new Delivery(message.getMessage());
                    if (showDelivery(delivery)) {
                        notificationText = getString(R.string.new_delivery_message);
                    }
                    break;
                case DeliveryRemove:
                    break;
                case DeliveryAssign:
                    notificationText = getString(R.string.assigned_delivery_message);
                    break;
                case SessionExpired:
                    notificationText = getString(R.string.session_expired_message);
                    break;
                case DeliveryUnassign:
                    notificationText = getString(R.string.unassign_delivery_message);
                    break;
                default:
                    break;
            }
            if (notificationText != null) {
                try {
                    NotificationUtils.getInstance(getApplicationContext())
                            .createInfoNotification(notificationText);
                } catch (Exception exception) {
                    Log.e(exception);
                }
            }
        }
    }

    private void sendSecurityEvents() {
        new Thread(() -> {
            try {
                for (SecurityTask task : mDataRepository.getSecurityTasks()) {
                    if (task != null) {
                        switch (task) {
                            case AppList:
                                List<AppListItem> appListItems = SecurityUtils.getAppList(
                                        SecurityUtils.getSuspectPackageList());
                                SendEventRequest request = new SendEventRequest(
                                        SecurityTask.AppList, appListItems);
                                SapRequestUtils.sendEvent(request);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(e.getMessage(), e);
            }
        }).start();
    }

    private List<JCheckResult> getSecurityChecks(String login, String tplst) {
        JGetBannedAppsIn jgbai_in = new JGetBannedAppsIn();
        jgbai_in.Login = login;
        jgbai_in.Tplst = tplst;

        Gson g = new Gson();

        List<String> bannedAppsList = null;
        try {
            String jgba_res = mMotoService.callService(ServiceFunction.GetBannedAppList, g.toJson(jgbai_in, JGetBannedAppsIn.class));
            if (!StringUtils.isNullOrEmpty(jgba_res)) {
                JGetBannedAppsOut jbai_out = g.fromJson(jgba_res, JGetBannedAppsOut.class);
                bannedAppsList = jbai_out.BannedAppsList;
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
        }

        final boolean isRooted = SecurityUtils.isDeviceRooted();
        final boolean isEmulator = SecurityUtils.isEmulator();
        final boolean hasBannedApps = SecurityUtils.hasBannedApps(bannedAppsList);

        List<JCheckResult> checkResults = null;

        if (isRooted || isEmulator || hasBannedApps) {
            checkResults = new ArrayList<>();
            JCheckResult checkResult;
            if (isRooted) {
                checkResult = new JCheckResult(JCheckType.Root, StringUtils.BoolToFlag(isRooted));
                checkResults.add(checkResult);
            }
            if (isEmulator) {
                checkResult = new JCheckResult(JCheckType.Emulator, StringUtils.BoolToFlag(isEmulator));
                checkResults.add(checkResult);
            }
            if (hasBannedApps) {
                checkResult = new JCheckResult(JCheckType.Clicker, StringUtils.BoolToFlag(hasBannedApps));
                checkResults.add(checkResult);
            }
        }

        return checkResults;
    }

    private class ServiceMessage {

        private Message mMessage;

        private boolean mReceived;

        public Message getMessage() {
            return mMessage;
        }

        private boolean isReceived() {
            return mReceived;
        }

        private void setReceived(boolean received) {
            mReceived = received;
        }

        ServiceMessage(Message message) {
            mReceived = false;
            mMessage = message;
        }
    }

    public class LocalBinder extends Binder {
        public QueueService getService() {
            return QueueService.this;
        }
    }

}
