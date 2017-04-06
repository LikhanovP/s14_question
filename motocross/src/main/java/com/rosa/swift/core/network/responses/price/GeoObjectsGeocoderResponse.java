package com.rosa.swift.core.network.responses.price;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет класс, соотвествующий сообщению ответа от геокодера Яндекса в формате JSON
 */
public class GeoObjectsGeocoderResponse {

    @SerializedName("response")
    @Expose
    private Response mResponse;

    public Response getResponse() {
        return mResponse;
    }

    //region Response

    public class Response {

        @SerializedName("GeoObjectCollection")
        @Expose
        private ResponseGeoObjectCollection mGeoObjectCollection;

        public ResponseGeoObjectCollection getGeoObjectCollection() {
            return mGeoObjectCollection;
        }
    }

    public class ResponseGeoObjectCollection {

        @SerializedName("metaDataProperty")
        @Expose
        private ResponseMetaDataProperty mMetaDataProperty;

        @SerializedName("featureMember")
        @Expose
        private List<FeatureMember> mFeatureMembers = new ArrayList<>();

        public List<FeatureMember> getFeatureMembers() {
            return mFeatureMembers;
        }
    }

    public class ResponseMetaDataProperty {

        @SerializedName("GeocoderResponseMetaData")
        @Expose
        private ResponseGeocoderMetaData mGeocoderMetaData;

    }

    public class ResponseGeocoderMetaData {

        @SerializedName("request")
        @Expose
        private String mRequest;

        @SerializedName("found")
        @Expose
        private String mFound;

        @SerializedName("results")
        @Expose
        private String mResults;

    }

    //endregion

    //region GeoObjectCollection

    public class FeatureMember {

        @SerializedName("GeoObject")
        @Expose
        private GeoObject mGeoObject;

        public GeoObject getGeoObject() {
            return mGeoObject;
        }
    }

    public class MetaDataProperty {

        @SerializedName("GeocoderMetaData")
        @Expose
        private GeocoderMetaData mGeocoderMetaData;

        public GeocoderMetaData getGeocoderMetaData() {
            return mGeocoderMetaData;
        }
    }

    public class GeoObject {

        @SerializedName("metaDataProperty")
        @Expose
        private MetaDataProperty mMetaDataProperty;

        @SerializedName("description")
        @Expose
        private String mDescription;

        @SerializedName("name")
        @Expose
        private String mName;

        @SerializedName("boundedBy")
        @Expose
        private BoundedBy mBoundedBy;

        @SerializedName("Point")
        @Expose
        private Point mPoint;

        public MetaDataProperty getMetaDataProperty() {
            return mMetaDataProperty;
        }

        public Point getPoint() {
            return mPoint;
        }
    }

    public class GeocoderMetaData {

        @SerializedName("kind")
        @Expose
        private String mKind;

        @SerializedName("text")
        @Expose
        private String mText;

        @SerializedName("precision")
        @Expose
        private String mPrecision;

        @SerializedName("AddressDetails")
        @Expose
        private AddressDetails mAddressDetails;

        public String getText() {
            return mText;
        }

        public AddressDetails getAddressDetails() {
            return mAddressDetails;
        }
    }

    public class AddressDetails {

        @SerializedName("Country")
        @Expose
        private Country mCountry;

        public Country getCountry() {
            return mCountry;
        }
    }

    public class Country {

        @SerializedName("AddressLine")
        @Expose
        private String mAddressLine;

        @SerializedName("CountryNameCode")
        @Expose
        private String mCountryNameCode;

        @SerializedName("CountryName")
        @Expose
        private String mCountryName;

        @SerializedName("AdministrativeArea")
        @Expose
        private AdministrativeArea mAdministrativeArea;

        public String getAddressLine() {
            return mAddressLine;
        }

        public AdministrativeArea getAdministrativeArea() {
            return mAdministrativeArea;
        }
    }

    public class AdministrativeArea {

        @SerializedName("AdministrativeAreaName")
        @Expose
        private String mAdministrativeAreaName;

        @SerializedName("Locality")
        @Expose
        private Locality mLocality;

        @SerializedName("SubAdministrativeArea")
        @Expose
        private SubAdministrativeArea mSubAdministrativeArea;

        public Locality getLocality() {
            return mLocality;
        }

    }

    public class Locality {

        @SerializedName("LocalityName")
        @Expose
        private String mLocalityName;

        @SerializedName("Thoroughfare")
        @Expose
        private Thoroughfare mThoroughfare;

    }

    public class Thoroughfare {

        @SerializedName("ThoroughfareName")
        @Expose
        private String mThoroughfareName;

        @SerializedName("Premise")
        @Expose
        private Premise mPremise;

    }

    public class Premise {

        @SerializedName("PremiseNumber")
        @Expose
        private String mPremiseNumber;

    }

    public class BoundedBy {

        @SerializedName("Envelope")
        @Expose
        private Envelope mEnvelope;

    }

    public class Point {

        @SerializedName("pos")
        @Expose
        private String mPosition;

        public String getPosition() {
            return mPosition;
        }
    }

    public class SubAdministrativeArea {

        @SerializedName("SubAdministrativeAreaName")
        @Expose
        private String mSubAdministrativeAreaName;

        @SerializedName("Locality")
        @Expose
        private Locality mLocality;

        public String getSubAdministrativeAreaName() {
            return mSubAdministrativeAreaName;
        }
    }

    public class Envelope {

        @SerializedName("lowerCorner")
        @Expose
        private String mLowerCorner;

        @SerializedName("upperCorner")
        @Expose
        private String mUpperCorner;

    }

    //endregion

}
