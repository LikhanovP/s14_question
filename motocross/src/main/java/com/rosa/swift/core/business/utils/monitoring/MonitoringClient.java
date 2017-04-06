package com.rosa.swift.core.business.utils.monitoring;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;

/**
 * Created by yalang on 08.08.13.
 */
public class MonitoringClient {

    private static MonitoringClient instance;

    private List<SendData> sq;
    private String appName;
    private String id;
    private Timer tmr;
    private boolean running;

    /// <summary>
    /// Constructor
    /// </summary>
    protected MonitoringClient() {
        sq = new ArrayList<SendData>();
        tmr = new Timer(true);
    }

    public static MonitoringClient GetInstance() {
        if (instance == null)
            instance = new MonitoringClient();

        return instance;
    }


    public void Send() {
        List<SendData> to_send = new ArrayList<SendData>();
        synchronized (this) {
            for (int i = sq.size() - 1; i >= 0; i--) {
                to_send.add(sq.get(i));
                if (sq.get(i).opFinished) {
                    sq.remove(i);
                }
            }
        }
        if (to_send.size() == 0) {
            to_send.add(getDefSendData());
        }
        long now = currentTimeMillis();
        for (int i = 0; i < to_send.size(); i++) {
            SendData sd = to_send.get(i);
            if (!sd.opFinished) {
                sd.lastTime = (int) ((now - sd.lastTime) / 1000);
            }
            sendOne(sd);
        }
    }

    private void sendOne(SendData sd) {

        try {
            String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><soap:Header><wsa:Action>http://tempuri.org/IServiceContract/SendIamAlive</wsa:Action><wsa:ReplyTo><wsa:Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</wsa:Address></wsa:ReplyTo><wsa:To>http://hypersrv02.sdvor.local:5556/MonitoringService/IamAliveHttp/IamAliveHttp</wsa:To></soap:Header><soap:Body><SendIamAlive xmlns=\"http://tempuri.org/\"><data><AppName xmlns=\"http://schemas.datacontract.org/2004/07/MonitoringServices\">$APP_NAME$</AppName><Description xmlns=\"http://schemas.datacontract.org/2004/07/MonitoringServices\">$DESCRIPTION$</Description><MachineName xmlns=\"http://schemas.datacontract.org/2004/07/MonitoringServices\">$MACHINE_NAME$</MachineName><OperationTime xmlns=\"http://schemas.datacontract.org/2004/07/MonitoringServices\">$OPERATION_TIME$</OperationTime><OperationToken xmlns=\"http://schemas.datacontract.org/2004/07/MonitoringServices\">$OPERATION_TOKEN$</OperationToken></data></SendIamAlive></soap:Body></soap:Envelope>";

            content = content.replace("$APP_NAME$", sd.app).replace("$DESCRIPTION$", sd.description).replace("$MACHINE_NAME$", sd.machine).replace("$OPERATION_TIME$", Integer.toString(sd.lastTime)).replace("$OPERATION_TOKEN$", Long.toString(sd.token));

            HttpPost post = new HttpPost("http://hypersrv02.sdvor.local:5556/MonitoringService/IamAliveHttp/IamAliveHttp");
            post.addHeader("Content-Type", "text/xml; charset=utf-8");
            post.addHeader("SOAPAction", "http://tempuri.org/IServiceContract/SendIamAlive");
            HttpClient httpclient = new DefaultHttpClient();
            post.setEntity(new StringEntity(content));

            HttpResponse resp = httpclient.execute(post);
        } catch (Exception e) {

        }
    }


    public static void Run(String appName, String id) {
        GetInstance().runInternal(appName, id);
    }

    public static void Stop() {
        GetInstance().stopInternal();
    }

    public static long StartOperation(String descr) {
        SendData sd = new SendData();
        sd.token = currentTimeMillis();
        sd.description = descr;
        MonitoringClient.GetInstance().startOperationIntrenal(sd);
        return sd.token;
    }

    public static void EndOperation(long token) {
        MonitoringClient.GetInstance().endOperationInternal(token);
    }

    public static void SetOperationDescription(long token, String descr) {
        MonitoringClient.GetInstance().setOperationDescription(token, descr);
    }

    private void startOperationIntrenal(SendData sd) {
        sd.app = appName;
        sd.machine = id;
        sd.opFinished = false;
        synchronized (this) {
            sq.add(sd);
        }
    }

    private void endOperationInternal(long token) {
        synchronized (this) {
            for (SendData sd : sq) {
                if (sd.token == token) {
                    sd.opFinished = true;
                    sd.lastTime = (int) ((currentTimeMillis() - sd.token) / 1000);
                }
            }
        }
    }

    private void setOperationDescription(long token, String descr) {
        synchronized (this) {
            for (SendData sd : sq) {
                if (sd.token == token) {
                    sd.description = descr;
                }
            }
        }
    }

    private void runInternal(String appName, String id) {
        if (!running) {
            this.appName = appName;
            this.id = id;
            tmr.schedule(new TimerTask() {
                @Override
                public void run() {
                    MonitoringClient.GetInstance().Send();
                }
            }, 10000, 2 * 60 * 60 * 1000);
            running = true;
        }
    }

    private SendData getDefSendData() {
        SendData sd = new SendData();
        sd.app = appName;
        sd.machine = id;
        sd.opFinished = true;
        sd.description = "IamAlive";
        sd.token = -1;
        return sd;
    }

    private void stopInternal() {
        tmr.cancel();
        running = false;
    }
}
