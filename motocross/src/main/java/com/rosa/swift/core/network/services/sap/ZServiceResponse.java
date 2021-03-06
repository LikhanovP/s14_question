package com.rosa.swift.core.network.services.sap;

//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.5
//
// Date Of Creation: 7/29/2013 12:01:18 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;

public class ZServiceResponse implements KvmSerializable {

    public String evParams;

    public ZServiceResponse() {
        evParams = "";
    }

    public ZServiceResponse(SoapObject soapObject) {
        this();
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("EvParams")) {
            Object obj = soapObject.getProperty("EvParams");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                evParams = j.toString();
            } else if (obj != null && obj instanceof String) {
                evParams = (String) obj;
            }
        }
    }

    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return evParams;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "EvParams";
                break;
        }
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }

}
