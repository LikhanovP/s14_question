package com.rosa.swift.core.network.responses.price;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PriceTransportsResponse implements Serializable {

    public class TypeTransport implements Serializable {

        @SerializedName("BEZEI")
        private String mName;

        @SerializedName("VSART")
        private String mCode;

        public String getName() {
            return mName;
        }

        public String getCode() {
            return mCode;
        }

    }

    @SerializedName("LT_VSART")
    private List<TypeTransport> mListTransports = null;

    public List<TypeTransport> getListTransports() {
        return mListTransports;
    }
}


