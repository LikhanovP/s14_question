package com.rosa.swift.core.business.utils;


import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class KSoapHelper {


    public static void serializable2Request(SoapObject soapRequest, KvmSerializable kvmSerializable) {

        long startAt = System.currentTimeMillis();
        serializable2RequestInternal(soapRequest, kvmSerializable);
        long endAt = System.currentTimeMillis();

        //Log.i("Формирование envelop: " + (endAt - startAt) + " мс.");
    }

    private static void serializable2RequestInternal(SoapObject soapRequest, KvmSerializable kvmSerializable) {
        try {
            for (int i = 0; i < kvmSerializable.getPropertyCount(); i++) {
                PropertyInfo info = new PropertyInfo();
                Hashtable hashtable = new Hashtable();
                kvmSerializable.getPropertyInfo(i, hashtable, info);

                if (info.type == PropertyInfo.STRING_CLASS ||
                        info.type == PropertyInfo.BOOLEAN_CLASS ||
                        info.type == PropertyInfo.INTEGER_CLASS ||
                        info.type == PropertyInfo.LONG_CLASS) {

                    Object propValue = kvmSerializable.getProperty(i);

                    SoapPrimitive primitive = new SoapPrimitive("", info.name, propValue != null ? propValue.toString() : "");
                    primitive.addAttribute("xmlns", "");
                    soapRequest.addProperty(info.name, primitive);

                } else if (info.type == PropertyInfo.VECTOR_CLASS) {

                    SoapObject soapObject = new SoapObject("", info.name);
                    soapObject.addAttribute("xmlns", "");
                    Vector vector = (Vector) kvmSerializable.getProperty(i);
                    if (vector != null && vector instanceof KvmSerializable) {

                        KvmSerializable vectorItemType = (KvmSerializable) vector;

                        PropertyInfo vectorPropertyInfo = new PropertyInfo();
                        vectorItemType.getPropertyInfo(0, new Hashtable(), vectorPropertyInfo);

                        for (Object item : vector) {

                            if (vectorPropertyInfo.type == PropertyInfo.STRING_CLASS ||
                                    vectorPropertyInfo.type == PropertyInfo.BOOLEAN_CLASS ||
                                    vectorPropertyInfo.type == PropertyInfo.INTEGER_CLASS ||
                                    vectorPropertyInfo.type == PropertyInfo.LONG_CLASS) {

                                SoapPrimitive primitive = new SoapPrimitive("", "item", item.toString());
                                primitive.addAttribute("xmlns", "");
                                soapObject.addProperty("item", primitive);

                            } else if (info.type == Double.class) {

                                Double propValue = (Double) kvmSerializable.getProperty(i);
                                String sValue = propValue != null ? String.format(Locale.ENGLISH, "%.2f", propValue) : "";
                                SoapPrimitive primitive = new SoapPrimitive("", info.name, sValue);
                                primitive.addAttribute("xmlns", "");
                                soapRequest.addProperty(info.name, primitive);

                            } else if (item instanceof KvmSerializable) {

                                SoapObject sObjectItem = new SoapObject("", "item");
                                sObjectItem.addAttribute("xmlns", "");
                                serializable2Request(sObjectItem, (KvmSerializable) item);
                                soapObject.addSoapObject(sObjectItem);

                            }

                        }
                    }

                    soapRequest.addProperty(info.name, soapObject);
                } else if (info.type == Double.class) {

                    Double propValue = (Double) kvmSerializable.getProperty(i);
                    String sValue = propValue != null ? String.format(Locale.ENGLISH, "%.2f", propValue) : "";
                    SoapPrimitive primitive = new SoapPrimitive("", info.name, sValue);
                    primitive.addAttribute("xmlns", "");
                    soapRequest.addProperty(info.name, primitive);

                } else {

                    Object obj = kvmSerializable.getProperty(i);
                    if (obj instanceof KvmSerializable) {

                        SoapObject soapObject = new SoapObject("", info.name);
                        soapObject.addAttribute("xmlns", "");

                        serializable2Request(soapObject, (KvmSerializable) obj);

                        soapRequest.addProperty(info.name, soapObject);
                    }
                }
            }
        } catch (Exception ex) {

            Log.e(ex.getMessage(), ex);

        }
    }


    public static List<HeaderProperty> getHeaderPropertyListForBasicAuth(String username, String password) {
        List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
        headerList.add(getHeaderPropertyForBasicAuth(username, password));
        return headerList;
    }


    public static HeaderProperty getHeaderPropertyForBasicAuth(String username, String password) {
        return new HeaderProperty("Authorization", "Basic " + encodeCredentialsForBasicAuth(username, password));
    }


    protected static String encodeCredentialsForBasicAuth(String username, String password) {
        return org.kobjects.base64.Base64.encode((username + ":" + password).getBytes());
    }

}
