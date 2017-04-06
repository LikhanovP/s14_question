package com.rosa.swift.core.network.services.sap;

import com.rosa.swift.core.business.utils.KSoapHelper;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.business.utils.monitoring.MonitoringClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZSvcHelper {

    static final String HEXES = "0123456789ABCDEF";

    private class SvcConnection {

        private String mUrl;

        private String mUserName;

        private String mPassword;

        private SvcConnection(String url, String userName, String password) {
            mUrl = url;
            mUserName = userName;
            mPassword = password;
        }

        private String getUrl() {
            return mUrl;
        }

        private String getUserName() {
            return mUserName;
        }

        private String getPassword() {
            return mPassword;
        }

    }

    private ZService zconsService;
    private SvcConnection connection;
    private Boolean ivCompression = true;

    public void setIvVersion(String ivVersion) {
        this.ivVersion = ivVersion;
    }

    private String ivVersion;

    public void setConnection(String url, String userName, String password) {
        this.connection = new SvcConnection(url, userName, password);
    }

    public ZSvcHelper() {
    }

    public void setCompression(Boolean ivCompression) {
        this.ivCompression = ivCompression;
    }


    public String CallService(ServiceFunction svcFunction, String ivParams) throws Exception {
        long token = MonitoringClient.StartOperation(svcFunction.toString());
        try {
            String ret;

            zconsService = new ZService();
            zconsService.setUrl(connection.getUrl());
            zconsService.setTimeOut(svcFunction.getTimeout());
            ZServiceRequest request = new ZServiceRequest();
            request.ivFunctionName = svcFunction.toString();
            request.ivVersion = ivVersion;

            if (ivCompression && ivParams != null) {
                request.ivCompress = "X";
                request.ivParams = getHex(deflate(ivParams));
            } else {
                request.ivParams = ivParams;
            }

            ZServiceResponse response = zconsService.ZconsService(request, KSoapHelper.getHeaderPropertyListForBasicAuth(connection.getUserName(), connection.getPassword()));
            if (ivCompression && !StringUtils.isNullOrEmpty(response.evParams)) {
                ret = inflate(hexStringToByteArray(response.evParams));
            } else {
                ret = response.evParams;
            }
            return ret;
        } finally {
            MonitoringClient.EndOperation(token);
        }
    }


    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String inflate(byte[] compressedData) throws DataFormatException, IOException {
        Inflater decompressor = new Inflater(true);
        decompressor.setInput(compressedData);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            int count = decompressor.inflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();
        return new String(bos.toByteArray());
    }

    private static byte[] deflate(String str) throws IOException {
        byte[] bytes = str.getBytes();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(bytes);
        deflater.finish();

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int bytesCompressed = deflater.deflate(buffer);
            bos.write(buffer, 0, bytesCompressed);
        }
        bos.close();

        byte[] compressed = bos.toByteArray();
        return compressed;
    }

    public void cancelRequest() {
        if (zconsService != null) {
            try {
                zconsService.cancelRequest();
            } catch (Exception ignored) {
            }
        }
    }
}



