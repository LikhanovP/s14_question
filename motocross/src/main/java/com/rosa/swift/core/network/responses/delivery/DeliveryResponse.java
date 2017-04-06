package com.rosa.swift.core.network.responses.delivery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Представляет класс, соотвествующий сообщению ответа от сервера по транспортной доставке
 */
public class DeliveryResponse {

    //region Fields

    /**
     * Номер транспортировки
     */
    @SerializedName("TKNUM")
    private String mNumber;

    /**
     * Плановая дата начала транспортировки
     */
    @SerializedName("DPTBG")
    private String mStartDate;

    /**
     * Плановое время начала транспортировки
     */
    @SerializedName("UPTBG")
    private String mStartTime;

    /**
     * Комментарий
     */
    @SerializedName("COMMENTS")
    private String mComments;

    /**
     * Плановая дата завершения транспортировки
     */
    @SerializedName("DPTEN")
    private String mFinishDate;

    /**
     * Плановое время завершения транспортировки
     */
    @SerializedName("UPTEN")
    private String mFinishTime;

    /**
     * Первый номер телефона
     */
    @SerializedName("TEL")
    private String mFirstPhone;

    /**
     * Второй номер телефона
     */
    @SerializedName("TEL2")
    private String mSecondPhone;

    /**
     * Стоимость нетто в валюте позиции фрахтовых расходов
     */
    @SerializedName("COST")
    private double mCost;

    /**
     * ФИО контактного лица
     */
    @SerializedName("CONTACT")
    private String mCustomerName;

    /**
     * Номер комплекта
     */
    @SerializedName("COMPLECT")
    private String mComplementNumber;

    /**
     * ФИО транспортного логиста
     */
    @SerializedName("TR_LOGIST_FIO")
    private String mLogisticianName;

    /**
     * ФИО сервис-менеджера
     */
    @SerializedName("SM_FIO")
    private String mManagerName;

    /**
     * Номер телефона сервис-менеджера
     */
    @SerializedName("SM_TEL")
    private String mManagerPhone;

    /**
     * Код статуса транспортировки
     */
    @SerializedName("STTRG")
    private int mStatus;

    /**
     * Дата изменения статуса транспортировки
     */
    @SerializedName("STTRG_DATE")
    private String mStatusDate;

    /**
     * Время изменения статуса транспортировки
     */
    @SerializedName("STTRG_TIME")
    private String mStatusTime;

    /**
     * Вес
     */
    @SerializedName("WEIGHT")
    private double mWeight;

    /**
     * Объем
     */
    @SerializedName("VOLUME")
    private double mVolume;

    /**
     * Оплата на месте (изображение)
     */
    @SerializedName("PAY_ON_SPOT")
    private char mPayOnPlace;

    /**
     * Стоимость услуги грузчиков
     */
    @SerializedName("LOADER_COST")
    private double mLoaderCost;

    /**
     * Признак комплектации на складе, если товар собран на складе
     */
    @SerializedName("MANNED")
    private char mSetOnWarehouse;

    /**
     * Стоимость перевеса
     */
    @SerializedName("OVERWEIGHT_COST")
    private double mOverWeightCost;

    /**
     * Стоимость дополнительных выгрузок
     */
    @SerializedName("ADD_COST")
    private double mAddCost;

    /**
     * Номер завода
     */
    @SerializedName("WERKS")
    private String mPlantCode;

    /**
     * Код вида транспорта
     */
    @SerializedName("VSART")
    private String mTransportCode;

    /**
     * Список складов в формате строки Json
     */
    @SerializedName("LGORTS")
    private String mWarehouses;

    /**
     * Время смещения в часах
     */
    @SerializedName("TIME_SHIFT")
    private int mTimeShift;

    /**
     * Информация по дополнительным выгрузкам
     */
    @SerializedName("ADD_COST_INFO")
    private String mAddCostInfo;

    /**
     * Адрес начального направления
     */
    @SerializedName("ADDR_FROM")
    private String mAddressFrom;

    /**
     * Широта адреса начального направления
     */
    @SerializedName("LATITUDE_FROM")
    private String mLatitudeFrom;

    /**
     * Долгота адреса начального направления
     */
    @SerializedName("LONGITUDE_FROM")
    private String mLongitudeFrom;

    /**
     * Адрес конечного направления
     */
    @SerializedName("ADDR_TO")
    private String mAddressTo;

    /**
     * Широта адреса конечного направления
     */
    @SerializedName("LATITUDE")
    private String mLatitudeTo;

    /**
     * Долгота адреса конечного направления
     */
    @SerializedName("LONGITUDE")
    private String mLongitudeTo;

    /**
     * Признак наличия схемы проезда
     */
    @SerializedName("LOCATION_SCHEMA")
    private String mLocationSchema;

    /**
     * Список адресов дополнительных выгрузок в строке Json
     */
    @SerializedName("ADR_OTHER_T")
    private String mOtherAddressesInJson;

    //endregion

    //region Getters

    /**
     * Возвращает номер транспортировки
     */
    public String getNumber() {
        return mNumber;
    }

    /**
     * Возвращает плановую дату начала транспортировки
     */
    public String getStartDate() {
        return mStartDate;
    }

    /**
     * Возвращает плановое время начала транспортировки
     */
    public String getStartTime() {
        return mStartTime;
    }

    /**
     * Возвращает комментарий
     */
    public String getComments() {
        return mComments;
    }

    /**
     * Возвращает плановую дату завершения транспортировки
     */
    public String getFinishDate() {
        return mFinishDate;
    }

    /**
     * Возвращает плановое время завершения транспортировки
     */
    public String getFinishTime() {
        return mFinishTime;
    }

    /**
     * Возвращает первый номер телефона
     */
    public String getFirstPhone() {
        return mFirstPhone;
    }

    /**
     * Возвращает второй номер телефона
     */
    public String getSecondPhone() {
        return mSecondPhone;
    }

    /**
     * Возвращает стоимость нетто в валюте позиции фрахтовых расходов
     */
    public double getCost() {
        return mCost;
    }

    /**
     * Возвращает ФИО контактного лица
     */
    public String getCustomerName() {
        return mCustomerName;
    }

    /**
     * Возвращает номер комплекта
     */
    public String getComplementNumber() {
        return mComplementNumber;
    }

    /**
     * Возвращает ФИО транспортного логиста
     */
    public String getLogisticianName() {
        return mLogisticianName;
    }

    /**
     * Возвращает ФИО сервис-менеджера
     */
    public String getManagerName() {
        return mManagerName;
    }

    /**
     * Возвращает номер телефона сервис-менеджера
     */
    public String getManagerPhone() {
        return mManagerPhone;
    }

    /**
     * Возвращает код статуса транспортировки
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Возвращает дату изменения статуса транспортировки
     */
    public String getStatusDate() {
        return mStatusDate;
    }

    /**
     * Возвращает время изменения статуса транспортировки
     */
    public String getStatusTime() {
        return mStatusTime;
    }

    /**
     * Возвращает вес товаров транспортировки
     */
    public double getWeight() {
        return mWeight;
    }

    /**
     * Возвращает объем товаров транспортировки
     */
    public double getVolume() {
        return mVolume;
    }

    /**
     * Возвращает признак оплаты на месте
     */
    public char getPayOnPlace() {
        return mPayOnPlace;
    }

    /**
     * Возвращает стоимость услуги грузчиков
     */
    public double getLoaderCost() {
        return mLoaderCost;
    }

    /**
     * Возвращает признак комплектации на складе,
     * если товар собран на складе
     */
    public char getSetOnWarehouse() {
        return mSetOnWarehouse;
    }

    /**
     * Возвращает стоимость перевеса
     */
    public double getOverWeightCost() {
        return mOverWeightCost;
    }

    /**
     * Возвращает стоимость дополнительных выгрузок
     */
    public double getAddCost() {
        return mAddCost;
    }

    /**
     * Возвращает номер завода
     */
    public String getPlantCode() {
        return mPlantCode;
    }

    /**
     * Возвращает код вида транспорта
     */
    public String getTransportCode() {
        return mTransportCode;
    }

    /**
     * Возвращает список складов в формате строки Json
     */
    public String getWarehouses() {
        return mWarehouses;
    }

    /**
     * Возвращает время смещения в часах
     */
    public int getTimeShift() {
        return mTimeShift;
    }

    /**
     * Возвращает информацию по дополнительным выгрузкам
     */
    public String getAddCostInfo() {
        return mAddCostInfo;
    }

    /**
     * Возвращает адрес стартового направления
     */
    public String getAddressFrom() {
        return mAddressFrom;
    }

    /**
     * Возвращает широту адреса стартового направления
     */
    public String getLatitudeFrom() {
        return mLatitudeFrom;
    }

    /**
     * Возвращает долготу адреса стартового направления
     */
    public String getLongitudeFrom() {
        return mLongitudeFrom;
    }

    /**
     * Возвращает адрес конечного направления
     */
    public String getAddressTo() {
        return mAddressTo;
    }

    /**
     * Возвращает широту адреса конечного направления
     */
    public String getLatitudeTo() {
        return mLatitudeTo;
    }

    /**
     * Возвращает долготу адреса конечного направления
     */
    public String getLongitudeTo() {
        return mLongitudeTo;
    }

    /**
     * Возвращает признак наличия схемы проезда
     */
    public String getLocationSchema() {
        return mLocationSchema;
    }

    /**
     * Возвращает список адресов дополнительных выгрузок в строке Json
     */
    public String getOtherAddressesInJson() {
        return mOtherAddressesInJson;
    }

    //endregion

    //region Comparing

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DeliveryResponse)) {
            return false;
        }
        DeliveryResponse jDelivery = (DeliveryResponse) object;
        return jDelivery.mNumber != null &&
                jDelivery.mNumber.equals(this.mNumber);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mNumber != null ? mNumber.hashCode() : 0;
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        result = 31 * result + (mStartTime != null ? mStartTime.hashCode() : 0);
        result = 31 * result + (mComments != null ? mComments.hashCode() : 0);
        result = 31 * result + (mFinishDate != null ? mFinishDate.hashCode() : 0);
        result = 31 * result + (mFinishTime != null ? mFinishTime.hashCode() : 0);
        result = 31 * result + (mFirstPhone != null ? mFirstPhone.hashCode() : 0);
        result = 31 * result + (mSecondPhone != null ? mSecondPhone.hashCode() : 0);
        temp = Double.doubleToLongBits(mCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mCustomerName != null ? mCustomerName.hashCode() : 0);
        result = 31 * result + (mComplementNumber != null ? mComplementNumber.hashCode() : 0);
        result = 31 * result + (mLogisticianName != null ? mLogisticianName.hashCode() : 0);
        result = 31 * result + (mManagerName != null ? mManagerName.hashCode() : 0);
        result = 31 * result + (mManagerPhone != null ? mManagerPhone.hashCode() : 0);
        result = 31 * result + mStatus;
        result = 31 * result + (mStatusDate != null ? mStatusDate.hashCode() : 0);
        result = 31 * result + (mStatusTime != null ? mStatusTime.hashCode() : 0);
        temp = Double.doubleToLongBits(mWeight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mVolume);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) mPayOnPlace;
        temp = Double.doubleToLongBits(mLoaderCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) mSetOnWarehouse;
        temp = Double.doubleToLongBits(mOverWeightCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mAddCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mPlantCode != null ? mPlantCode.hashCode() : 0);
        result = 31 * result + (mTransportCode != null ? mTransportCode.hashCode() : 0);
        result = 31 * result + (mWarehouses != null ? mWarehouses.hashCode() : 0);
        result = 31 * result + mTimeShift;
        result = 31 * result + (mAddCostInfo != null ? mAddCostInfo.hashCode() : 0);
        result = 31 * result + (mAddressFrom != null ? mAddressFrom.hashCode() : 0);
        result = 31 * result + (mLatitudeFrom != null ? mLatitudeFrom.hashCode() : 0);
        result = 31 * result + (mLongitudeFrom != null ? mLongitudeFrom.hashCode() : 0);
        result = 31 * result + (mAddressTo != null ? mAddressTo.hashCode() : 0);
        result = 31 * result + (mLatitudeTo != null ? mLatitudeTo.hashCode() : 0);
        result = 31 * result + (mLongitudeTo != null ? mLongitudeTo.hashCode() : 0);
        result = 31 * result + (mLocationSchema != null ? mLocationSchema.hashCode() : 0);
        result = 31 * result + (mOtherAddressesInJson != null ? mOtherAddressesInJson.hashCode() : 0);
        return result;
    }

    //endregion

    public class AdditionAddress {

        //region Serialization

        @SerializedName("TKNUM")
        private String mTknum;

        @SerializedName("POSNUM")
        private byte mPosition;

        @SerializedName("LATITUDE")
        private Double mLatitude;

        @SerializedName("LONGITUDE")
        private Double mLongitude;

        @SerializedName("FULL_ADDRESS")
        private String mFullAddress;

        @SerializedName("CONTACT_NAME")
        private String mContactName;

        @SerializedName("TELF1")
        private String mContactPhone;

        //endregion

        //region Getters and setters

        public String getTknum() {
            return mTknum;
        }

        public byte getPosition() {
            return mPosition;
        }

        public Double getLatitude() {
            return mLatitude;
        }

        public Double getLongitude() {
            return mLongitude;
        }

        public String getFullAddress() {
            return mFullAddress;
        }

        public String getContactName() {
            return mContactName;
        }

        public String getContactPhone() {
            return mContactPhone;
        }

        //endregion

    }

    public class AdditionAddressList {

        @SerializedName("ADDITION_ADDRESS")
        private List<AdditionAddress> mAddresses;

        public List<AdditionAddress> getAddresses() {
            return mAddresses;
        }
    }

}
