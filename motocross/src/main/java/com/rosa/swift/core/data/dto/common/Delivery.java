package com.rosa.swift.core.data.dto.common;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.annotations.TemplateParameters;
import com.rosa.swift.core.business.utils.JsonUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateFieldCategory;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.network.responses.delivery.DeliveryResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Представляет транспортную доставку
 */
public class Delivery /*implements Serializable*/ {

    //region Fields

    //region Template Fields

    /**
     * Номер комплекта
     */
    @TemplateParameters(
            name = "Комплект",
            category = TemplateFieldCategory.Default)
    @SerializedName("setNumber")
    private String mSetNumber;

    /**
     * ФИО контактного лица
     */
    @TemplateParameters(
            name = "ФИО клиента",
            category = TemplateFieldCategory.Default)
    @SerializedName("contact")
    private String mCustomerName;

    /**
     * Плановая дата начала транспортировки
     */
    @TemplateParameters(
            name = "Дата",
            category = TemplateFieldCategory.Default)
    @SerializedName("startDateString")
    private String mStartDate;

    /**
     * Плановое время начала транспортировки
     */
    @TemplateParameters(
            name = "Время",
            category = TemplateFieldCategory.Default)
    @SerializedName("startTimeString")
    private String mStartTime;

    /**
     * Номер транспортировки
     */
    @TemplateParameters(
            name = "Транспортировка",
            category = TemplateFieldCategory.Default)
    @SerializedName("tknum")
    private String mNumber;

    /**
     * Комментарий
     */
    @TemplateParameters(
            name = "Комментарий",
            category = TemplateFieldCategory.Default)
    @SerializedName("comment")
    private String mComments;

    /**
     * Первый номер телефона
     */
    @TemplateParameters(
            name = "Телефон клиента",
            category = TemplateFieldCategory.Phone)
    @SerializedName("telNumber")
    private String mFirstPhone;

    /**
     * Второй номер телефона
     */
    @TemplateParameters(
            name = "Телефон клиента 2",
            category = TemplateFieldCategory.Phone)
    @SerializedName("telNumber2")
    private String mSecondPhone;

    /**
     * Стоимость нетто в валюте позиции фрахтовых расходов
     */
    @TemplateParameters(
            name = "Фрахт",
            category = TemplateFieldCategory.Currency)
    @SerializedName("cost")
    private double mCost;

    /**
     * ФИО транспортного логиста
     */
    @TemplateParameters(
            name = "Логист",
            category = TemplateFieldCategory.Default)
    @SerializedName("logistFIO")
    private String mLogisticianName;

    /**
     * ФИО сервис-менеджера
     */
    @TemplateParameters(
            name = "ФИО СМ",
            category = TemplateFieldCategory.Default)
    @SerializedName("smFIO")
    private String mManagerName;

    /**
     * Номер телефона сервис-менеджера
     */
    @TemplateParameters(
            name = "Телефон СМ",
            category = TemplateFieldCategory.Phone)
    @SerializedName("smTel")
    private String mManagerPhone;

    /**
     * Статус транспортировки
     */
    @TemplateParameters(
            name = "Статус",
            category = TemplateFieldCategory.Default)
    @SerializedName("trStatus")
    private String mStatusName;

    /**
     * Дата изменения статуса транспортировки
     */
    @TemplateParameters(
            name = "Дата статуса",
            category = TemplateFieldCategory.Default)
    @SerializedName("statusDateString")
    private String mStatusDate;

    /**
     * Время изменения статуса транспортировки
     */
    @TemplateParameters(
            name = "Время статуса",
            category = TemplateFieldCategory.Default)
    @SerializedName("statusTimeString")
    private String mStatusTime;

    /**
     * Вес
     */
    @TemplateParameters(
            name = "Вес",
            category = TemplateFieldCategory.Weight)
    @SerializedName("weight")
    private double mWeight;

    /**
     * Объем
     */
    @TemplateParameters(
            name = "Объем",
            category = TemplateFieldCategory.Volume)
    @SerializedName("volume")
    private double mVolume;

    /**
     * Оплата на месте (изображение)
     */
    @TemplateParameters(
            name = "Оплата на месте",
            category = TemplateFieldCategory.Icon)
    @SerializedName("payOnPlace")
    private boolean mPayOnPlace;

    /**
     * Стоимость услуги грузчиков
     */
    @TemplateParameters(
            name = "Услуга грузчиков",
            category = TemplateFieldCategory.Currency)
    @SerializedName("loaderCost")
    private double mLoaderCost;

    /**
     * Признак комплектации на складе, если товар собран на складе
     */
    @TemplateParameters(
            name = "Признак комплектации",
            category = TemplateFieldCategory.Icon)
    @SerializedName("setOnWarehouse")
    private boolean mSetOnWarehouse;

    /**
     * Стоимость перевеса
     */
    @TemplateParameters(
            name = "Перевес",
            category = TemplateFieldCategory.Currency)
    @SerializedName("overCost")
    private double mOverWeightCost;

    /**
     * Стоимость дополнительных выгрузок
     */
    @TemplateParameters(
            name = "Доп.заезд",
            category = TemplateFieldCategory.Currency)
    @SerializedName("addCost")
    private double mAddCost;

    /**
     * Суммарноя стоимость транспортировки
     */
    @TemplateParameters(
            name = "ИТОГО",
            category = TemplateFieldCategory.Currency)
    @SerializedName("totalCost")
    private double mSummaryCost;

    /**
     * Информация по дополнительным выгрузкам
     */
    @TemplateParameters(
            name = "Инфо по доп.заезду",
            category = TemplateFieldCategory.Default)
    @SerializedName("addCostInfo")
    private String mAddCostInfo;

    /**
     * Адрес начального направления
     */
    @TemplateParameters(
            name = "Адрес",
            category = TemplateFieldCategory.Default)
    @SerializedName("addrFrom")
    private String mAddressFrom;

    /**
     * Адрес конечного направления
     */
    @TemplateParameters(
            name = "Адрес куда",
            category = TemplateFieldCategory.Default)
    @SerializedName("addrTo")
    private String mAddressTo;

    /**
     * Информация по дополнительным выгрузкам
     */
    @TemplateParameters(
            name = "Доп. выгрузки",
            category = TemplateFieldCategory.Default)
    @SerializedName("otherAddresses")
    private String mOtherAddresses;

    //endregion

    /**
     * Тип транспортировки
     */
    @SerializedName("type")
    private TransportationType mType;

    /**
     * Номер завода
     */
    @SerializedName("werks")
    private String mPlantCode;

    /**
     * Код вида транспорта
     */
    @SerializedName("vsart")
    private String mTransportCode;

    /**
     * Признак, находится ли доставка на простое
     */
    @SerializedName("waisting")
    private boolean mIdling;

    /**
     * Список складов
     */
    @SerializedName("lgorts")
    private List<Warehouse> mWarehouseList;

    /**
     * Время смещения в часах
     */
    @SerializedName("time_shift")
    private int mTimeShift;

    /**
     * Код статуса транспортировки
     */
    @SerializedName("status")
    private int mStatus;

    /**
     * Дата изменения статуса транспортировки
     */
    @SerializedName("statusDate")
    private Date mStatusDateTime;

    /**
     * Планируемое время начала доставки
     */
    @SerializedName("planStart")
    private Date mStartDateTime;

    /**
     * Планируемое время завершения доставки
     */
    @SerializedName("planFinish")
    private Date mFinishDateTime;

    /**
     * Информация по начальному адресу
     */
    @SerializedName("addressFrom")
    private GeoObject mFullAddressFrom;

    /**
     * Информация по конечному адресу
     */
    @SerializedName("addressTo")
    private GeoObject mFullAddressTo;

    /**
     * Признак, задана ли схема проезда
     */
    @SerializedName("locationSchema")
    private boolean mSchemaExist;

    /**
     * Список дополнительных адресов
     */
    @SerializedName("additionAddresses")
    private List<DeliveryAddress> mOtherAddressList;

    /**
     * Признак, является ли доставка новой
     */
    @SerializedName("new")
    private boolean mNew = true;

    //endregion

    //region Getters and setters

    /**
     * Возвращает список складов
     */
    public List<Warehouse> getWarehouseList() {
        return mWarehouseList;
    }

    /**
     * Возвращает дату и время изменения статуса транспортировки
     */
    public Date getStatusDate() {
        return mStatusDateTime;
    }

    /**
     * Задает дату и время изменения статуса транспортировки
     */
    public void setStatusDateTime(Date date) {
        mStatusDateTime = date;
        if (date != null) {
            mStatusDate = StringUtils.getFormatStringFromDate(date, "dd.MM");
            mStatusTime = StringUtils.getFormatStringFromDate(date, "HH:mm");
        } else {
            mStatusDate = null;
            mStatusTime = null;
        }
    }

    /**
     * Возвращает плановую дату и время начала транспортировки
     */
    public Date getStartDate() {
        return mStartDateTime;
    }

    /**
     * Задает плановую дату и время начала транспортировки
     */
    public void setStartDateTime(Date date) {
        mStartDateTime = date;
        if (date != null) {
            mStartDate = StringUtils.getFormatStringFromDate(date, "dd.MM");
            mStartTime = StringUtils.getFormatStringFromDate(date, "HH:mm");
        } else {
            mStartDate = null;
            mStartTime = null;
        }
    }

    /**
     * Возвращает плановую дату и время завершения транспортировки
     */
    public Date getFinishDate() {
        return mFinishDateTime;
    }

    /**
     * Задает плановую дату и время завершения транспортировки
     */
    public void setFinishDate(Date time) {
        mFinishDateTime = time;
    }

    /**
     * Возвращает интервал смещения времени в часах для доставки
     */
    public int getTimeShift() {
        return mTimeShift;
    }

    /**
     * Возвращает номер транспортировки
     */
    public String getNumber() {
        return mNumber;
    }

    /**
     * Возвращает код статуса транспортировки
     */
    public int getStatus() {
        return mStatus;
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
     * Возвращает значение, показывающее, является ли доставка новой
     */
    public boolean isNew() {
        return mNew;
    }

    /**
     * Задает значение новой доставки
     */
    public void setNew(boolean isNew) {
        mNew = isNew;
    }

    /**
     * Возвращает номер комплекта
     */
    public String getSetNumber() {
        return mSetNumber;
    }

    /**
     * Возвращает значение, показывающее, находится ли доставка на простое
     */
    public boolean isIdling() {
        return mIdling;
    }

    /**
     * Задает начало простоя для доставки
     */
    public void setIdling(boolean value) {
        mIdling = value;
    }

    /**
     * Возвращает значение, показывающе, признак оплаты на месте
     */
    public boolean isPayOnPlace() {
        return mPayOnPlace;
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
     * Возвращает значение, показывающее, задана ли схема проезда
     */
    public boolean isSchemaExist() {
        return mSchemaExist;
    }

    /**
     * Возвращает ФИО контактного лица
     */
    public String getCustomerName() {
        return mCustomerName;
    }

    /**
     * Возвращает комментарий
     */
    public String getComments() {
        return mComments;
    }

    /**
     * Возвращает стоимость нетто в валюте позиции фрахтовых расходов
     */
    public double getCost() {
        return mCost;
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
     * Возвращает статус транспортировки
     */
    public String getStatusName() {
        return mStatusName;
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
     * Возвращает стоимость услуги грузчиков
     */
    public double getLoaderCost() {
        return mLoaderCost;
    }

    /**
     * Возвращает значение, показывающее, был ли товар собран на складе
     */
    public boolean isSetOnWarehouse() {
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
     * Возвращает суммарную стоимость доставки
     */
    public double getSummaryCost() {
        return mSummaryCost;
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
     * Возвращает адрес конечного направления
     */
    public String getAddressTo() {
        return mAddressTo;
    }

    /**
     * Возвращает информацию по начальному адресу
     */
    public GeoObject getStartAddress() {
        return mFullAddressFrom;
    }

    /**
     * Возвращает информацию по конечного адресу
     */
    public GeoObject getFinishAddress() {
        return mFullAddressTo;
    }

    public TransportationType getType() {
        return mType;
    }

    /**
     * Возвращает список адресов дополнительных выгрузок
     */
    public List<DeliveryAddress> getOtherAddressList() {
        return mOtherAddressList;
    }

    public void updateSummaryCost() {
        mSummaryCost = getSummaryCost();
    }

    public void setAddCost(double addCost) {
        mAddCost = addCost;
    }

    public void setAddCostInfo(String addCostInfo) {
        mAddCostInfo = addCostInfo;
    }

    public void setComments(String comments) {
        mComments = comments;
    }

    public void setSetNumber(String complementNumber) {
        mSetNumber = complementNumber;
    }

    public void setCost(double cost) {
        mCost = cost;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public void setFinishDateTime(Date finishDateTime) {
        mFinishDateTime = finishDateTime;
    }

    public void setFirstPhone(String firstPhone) {
        mFirstPhone = firstPhone;
    }

    public void setFullAddressFrom(GeoObject fullAddressFrom) {
        if (fullAddressFrom != null) {
            mAddressFrom = fullAddressFrom.getAddress();
        }
        mFullAddressFrom = fullAddressFrom;
    }

    public void setFullAddressTo(GeoObject fullAddressTo) {
        if (fullAddressTo != null) {
            mAddressTo = fullAddressTo.getAddress();
        }
        mFullAddressTo = fullAddressTo;
    }

    public void setLoaderCost(double loaderCost) {
        mLoaderCost = loaderCost;
    }

    public void setSchemaExist(boolean schemaExist) {
        mSchemaExist = schemaExist;
    }

    public void setLogisticianName(String logisticianName) {
        mLogisticianName = logisticianName;
    }

    public void setManagerName(String managerName) {
        mManagerName = managerName;
    }

    public void setManagerPhone(String managerPhone) {
        mManagerPhone = managerPhone;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public void setOverWeightCost(double overWeightCost) {
        mOverWeightCost = overWeightCost;
    }

    public void setPayOnPlace(boolean payOnPlace) {
        mPayOnPlace = payOnPlace;
    }

    public void setPlantCode(String plantCode) {
        mPlantCode = plantCode;
    }

    public void setSecondPhone(String secondPhone) {
        mSecondPhone = secondPhone;
    }

    public void setSetOnWarehouse(boolean setOnWarehouse) {
        mSetOnWarehouse = setOnWarehouse;
    }

    public void setStatus(int status) {
        mStatus = status;
        mStatusName = getStatusDescription();
    }

    public void setTransportCode(String transportCode) {
        mTransportCode = transportCode;
    }

    public void setType(TransportationType type) {
        mType = type;
    }

    public void setVolume(double volume) {
        mVolume = volume;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        mWarehouseList = warehouseList;
    }

    public void setOtherAddressList(List<DeliveryAddress> otherAddressList) {
        mOtherAddressList = otherAddressList;
        mOtherAddresses = getOtherAddresses();
    }

    //endregion

    //region Methods

    /**
     * Возвращает значение, показывающее, относится ли текущая доставка к комплекту
     */
    public boolean inSet() {
        return !TextUtils.isEmpty(mSetNumber);
    }

    /**
     * Возвращает значение, показывающее, относится ли текущая и
     * указанная доставки к одному комплекту
     *
     * @param delivery доставка для сравнения
     */
    public boolean inSameSet(Delivery delivery) {
        return this.inSet() && delivery.getSetNumber().equals(mSetNumber);
    }

    /**
     * Возвращает значение, показывающее, заданы ли для доставки координаты
     */
    public boolean hasCoordinates() {
        return mFullAddressTo != null && mFullAddressTo.isCorrect()
                && mFullAddressFrom != null && mFullAddressFrom.isCorrect();
    }

    /**
     * Возвращает полную стоимость доставки
     */
    public double getTotalCost() {
        return mCost + mOverWeightCost + mLoaderCost + mAddCost;
    }

    /**
     * Возвращает список адресов, представленный начальным, дополнительными и конечным адресами
     */
    public List<GeoObject> getAddresses() {
        ArrayList<GeoObject> addresses = new ArrayList<>();
        //добавляем начальный адрес, если не задан, то не добавляем остальные
        if (mFullAddressFrom != null && mFullAddressFrom.isCorrect()) {
            addresses.add(mFullAddressFrom);
            //дополнительные адреса
            if (mOtherAddressList != null && mOtherAddressList.size() > 0) {
                for (GeoObject object : mOtherAddressList) {
                    if (object.isCorrect()) {
                        addresses.add(object);
                    }
                }
            }
            //конечный адрес
            if (mFullAddressTo != null && mFullAddressTo.isCorrect()) {
                addresses.add(mFullAddressTo);
            }
        }
        return addresses.size() > 0 ? addresses : null;
    }

    /**
     * Обновляет данные текущей доставки данными указанной доставки
     *
     * @param delivery доставка с обновленными данными
     */
    public boolean update(Delivery delivery) {
        boolean result = false;
        //пока не выполняем проверку на хэшкод
        if (delivery != null /*&& this.hashCode() != delivery.hashCode()*/) {
            mSetNumber = delivery.getSetNumber();
            mCustomerName = delivery.getCustomerName();
            mComments = delivery.getComments();
            mAddressFrom = delivery.getAddressFrom();
            mAddressTo = delivery.getAddressTo();
            mFirstPhone = delivery.getFirstPhone();
            mSecondPhone = delivery.getSecondPhone();
            mCost = delivery.getCost();
            mLogisticianName = delivery.getLogisticianName();
            mManagerName = delivery.getManagerName();
            mManagerPhone = delivery.getManagerPhone();
            mStatus = delivery.getStatus();
            mStatusName = delivery.getStatusName();
            mWeight = delivery.getWeight();
            mVolume = delivery.getVolume();
            mPayOnPlace = delivery.isPayOnPlace();
            mLoaderCost = delivery.getLoaderCost();
            mSetOnWarehouse = delivery.isSetOnWarehouse();
            mOverWeightCost = delivery.getOverWeightCost();
            mAddCost = delivery.getAddCost();
            mSummaryCost = delivery.getSummaryCost();
            mPlantCode = delivery.getPlantCode();
            mWarehouseList = delivery.getWarehouseList();
            mAddCostInfo = delivery.getAddCostInfo();
            mTimeShift = delivery.getTimeShift();
            mSchemaExist = delivery.isSchemaExist();
            mOtherAddressList = delivery.getOtherAddressList();
            mOtherAddresses = delivery.getOtherAddresses();
            mFullAddressFrom = delivery.getStartAddress();
            mFullAddressTo = delivery.getFinishAddress();

            setStartDateTime(delivery.getStartDate());
            setFinishDate(delivery.getFinishDate());
            setStatusDateTime(delivery.getStatusDate());

            result = true;
        }
        return result;
    }

    /**
     * Смещает при необходимости время доставки на заданный интервал
     */
    private void shiftTime() {
        if (mTimeShift != 0) {
            shiftTime(mStartDateTime, mTimeShift);
            shiftTime(mFinishDateTime, mTimeShift);
            shiftTime(mStatusDateTime, mTimeShift);
        }
    }

    /**
     * Возвращает адрес транспортировки
     *
     * @param address   Адресс
     * @param longitude Долгота
     * @param latitude  Широта
     */
    private GeoObject getAddress(String address, String longitude, String latitude) {
        double parsedLongitude = !TextUtils.isEmpty(longitude) ? Double.parseDouble(longitude) : 0;
        double parsedLatitude = !TextUtils.isEmpty(latitude) ? Double.parseDouble(latitude) : 0;
        return new GeoObject(address, parsedLongitude, parsedLatitude);
    }

    /**
     * Смещает заданное время на указанный интервал смещения
     *
     * @param timeToShift      время, которое необходимо сместить
     * @param timeShiftInHours интервал смещения в часах
     */
    private void shiftTime(Date timeToShift, int timeShiftInHours) {
        long timeShiftInMillis = timeShiftInHours * 1000 * 60 * 60;
        long timeToShiftInMillis = timeToShift.getTime();
        timeToShift.setTime(timeToShiftInMillis + timeShiftInMillis);
    }

    /**
     * Возвращает список складов
     *
     * @param warehouses строка со списком складов
     */
    private List<Warehouse> getWarehouseList(String warehouses) {
        List<Warehouse> warehouseList = null;
        if (!TextUtils.isEmpty(warehouses)) {
            String[] listWarehouses = warehouses.split(",");
            if (listWarehouses.length > 0) {
                warehouseList = new ArrayList<>();
                for (String warehouse : listWarehouses) {
                    String[] parameters = warehouse.split(":");
                    if (parameters.length == 2 && !TextUtils.isEmpty(parameters[0])) {
                        warehouseList.add(new Warehouse(parameters[0], parameters[1]));
                    }
                }
            }
        }
        return warehouseList;
    }

    /**
     * Возвращает список дополнительных адресов
     *
     * @param jsonAddresses список дополнительных адресов в формате Json строки
     * @throws JsonSyntaxException исключение, возникающие при ошибке считывания структуры из Json
     */
    private List<DeliveryAddress> getOtherAddressList(String jsonAddresses)
            throws JsonSyntaxException {
        ArrayList<DeliveryAddress> addressList = null;
        if (!TextUtils.isEmpty(jsonAddresses)) {
            jsonAddresses = "{ ADDITION_ADDRESS : " + jsonAddresses + "}";
            //получение списка адресов из модели ответа
            DeliveryResponse.AdditionAddressList addressStruct = new Gson().fromJson(
                    jsonAddresses, DeliveryResponse.AdditionAddressList.class);
            List<DeliveryResponse.AdditionAddress> additionAddresses = addressStruct.getAddresses();
            if (additionAddresses != null && additionAddresses.size() > 0) {
                addressList = new ArrayList<>();
                //преобразуем адреса к адресом модели данных
                for (DeliveryResponse.AdditionAddress address : additionAddresses) {
                    addressList.add(new DeliveryAddress(address));
                }
            }
        }
        return addressList;
    }

    /**
     * Возвращает строку в формате HTML для отображения дополнительных адресов в шаблоне доставки
     */
    public String getOtherAddresses() {
        String otherAddresses = null;
        if (mOtherAddressList != null && mOtherAddressList.size() > 0) {
            ArrayList<String> htmlLines = new ArrayList<>();
            for (DeliveryAddress address : mOtherAddressList) {
                //порядковый номер и адрес
                htmlLines.add(String.format("<br>&emsp;%s. %s",
                        address.getPosition(),
                        address.getAddress()));
                //имя контакта
                String name = address.getContactName();
                if (!TextUtils.isEmpty(name)) {
                    htmlLines.add(String.format("<br>&emsp;Контакт: %s", name));
                }
                //телефон контакта
                String phone = address.getPhone();
                if (!TextUtils.isEmpty(phone)) {
                    htmlLines.add(String.format("<br>&emsp;Телефон: <a href='tel:%1$s'>%1$s</a>",
                            phone));
                }
            }
            otherAddresses = TextUtils.join("", htmlLines);
        }
        return otherAddresses;
    }

    /**
     * Возвращает описание кода статуса
     */
    private String getStatusDescription() {
        switch (mStatus) {
            case 0:
                return "Новая";
            case 1:
                return "Назначен водитель";
            case 2:
                return "Выданы документы";
            case 3:
                return "Погрузка";
            case 4:
                return "";
            case 5:
                return "";
            case 6:
                return "Выехал с базы";
            case 7:
                return "Исполненно";
            default:
                return "";
        }
    }

    //endregion

    //region Comparing

    @Override
    public boolean equals(Object object) {
        if (object instanceof Delivery) {
            Delivery delivery = (Delivery) object;
            String deliveryNumber = delivery.getNumber();
            return !TextUtils.isEmpty(deliveryNumber) && deliveryNumber.equals(mNumber);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mSetNumber != null ? mSetNumber.hashCode() : 0;
        result = 31 * result + (mCustomerName != null ? mCustomerName.hashCode() : 0);
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        result = 31 * result + (mStartTime != null ? mStartTime.hashCode() : 0);
        result = 31 * result + (mNumber != null ? mNumber.hashCode() : 0);
        result = 31 * result + (mComments != null ? mComments.hashCode() : 0);
        result = 31 * result + (mFirstPhone != null ? mFirstPhone.hashCode() : 0);
        result = 31 * result + (mSecondPhone != null ? mSecondPhone.hashCode() : 0);
        temp = Double.doubleToLongBits(mCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mLogisticianName != null ? mLogisticianName.hashCode() : 0);
        result = 31 * result + (mManagerName != null ? mManagerName.hashCode() : 0);
        result = 31 * result + (mManagerPhone != null ? mManagerPhone.hashCode() : 0);
        result = 31 * result + (mStatusName != null ? mStatusName.hashCode() : 0);
        result = 31 * result + (mStatusDate != null ? mStatusDate.hashCode() : 0);
        result = 31 * result + (mStatusTime != null ? mStatusTime.hashCode() : 0);
        temp = Double.doubleToLongBits(mWeight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mVolume);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mPayOnPlace ? 1 : 0);
        temp = Double.doubleToLongBits(mLoaderCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mSetOnWarehouse ? 1 : 0);
        temp = Double.doubleToLongBits(mOverWeightCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mAddCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mSummaryCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mAddCostInfo != null ? mAddCostInfo.hashCode() : 0);
        result = 31 * result + (mAddressFrom != null ? mAddressFrom.hashCode() : 0);
        result = 31 * result + (mAddressTo != null ? mAddressTo.hashCode() : 0);
        result = 31 * result + (mOtherAddresses != null ? mOtherAddresses.hashCode() : 0);
        result = 31 * result + (mPlantCode != null ? mPlantCode.hashCode() : 0);
        result = 31 * result + (mTransportCode != null ? mTransportCode.hashCode() : 0);
        result = 31 * result + (mIdling ? 1 : 0);
        result = 31 * result + (mWarehouseList != null ? mWarehouseList.hashCode() : 0);
        result = 31 * result + mTimeShift;
        result = 31 * result + mStatus;
        result = 31 * result + (mStatusDateTime != null ? mStatusDateTime.hashCode() : 0);
        result = 31 * result + (mStartDateTime != null ? mStartDateTime.hashCode() : 0);
        result = 31 * result + (mFinishDateTime != null ? mFinishDateTime.hashCode() : 0);
        result = 31 * result + (mFullAddressFrom != null ? mFullAddressFrom.hashCode() : 0);
        result = 31 * result + (mFullAddressTo != null ? mFullAddressTo.hashCode() : 0);
        result = 31 * result + (mSchemaExist ? 1 : 0);
        result = 31 * result + (mOtherAddressList != null ? mOtherAddressList.hashCode() : 0);
        result = 31 * result + (mNew ? 1 : 0);
        return result;
    }

    //endregion

    //region Constructors

    public Delivery() {
    }

    public Delivery(String jsonDelivery) {
        this(jsonDelivery, TransportationType.Delivery);
    }

    public Delivery(DeliveryResponse deliveryRes) {
        this(deliveryRes, TransportationType.Delivery);
    }

    /**
     * Инициализирует новый экземпляр класса
     *
     * @param jsonDelivery модель ответа от сервера Доставки в формате Json
     */
    public Delivery(String jsonDelivery, TransportationType type) {
        this(JsonUtils.deserializeDelivery(jsonDelivery), type);
    }

    /**
     * Инициализирует новый экземпляр класса
     *
     * @param deliveryRes модель ответа от сервера Доставки
     */
    public Delivery(DeliveryResponse deliveryRes, TransportationType type) {
        if (deliveryRes != null) {
            try {
                mType = type;
                mNumber = deliveryRes.getNumber();
                mSetNumber = deliveryRes.getComplementNumber();
                mComments = deliveryRes.getComments();
                mAddressFrom = deliveryRes.getAddressFrom();
                mAddressTo = deliveryRes.getAddressTo();
                mFirstPhone = deliveryRes.getFirstPhone();
                mSecondPhone = deliveryRes.getSecondPhone();
                mCost = deliveryRes.getCost();
                mCustomerName = deliveryRes.getCustomerName();
                mLogisticianName = deliveryRes.getLogisticianName();
                mManagerName = deliveryRes.getManagerName();
                mManagerPhone = deliveryRes.getManagerPhone();
                mStatus = deliveryRes.getStatus();
                mStatusName = getStatusDescription();
                mWeight = deliveryRes.getWeight();
                mVolume = deliveryRes.getVolume();
                mPayOnPlace = deliveryRes.getPayOnPlace() == 'X';
                mLoaderCost = deliveryRes.getLoaderCost();
                mSetOnWarehouse = deliveryRes.getSetOnWarehouse() == 'X';
                mOverWeightCost = deliveryRes.getOverWeightCost();
                mAddCost = deliveryRes.getAddCost();
                mSummaryCost = getTotalCost();
                mPlantCode = deliveryRes.getPlantCode();
                mTransportCode = deliveryRes.getTransportCode();
                mWarehouseList = getWarehouseList(deliveryRes.getWarehouses());
                mTimeShift = deliveryRes.getTimeShift();
                mAddCostInfo = deliveryRes.getAddCostInfo();
                mSchemaExist = StringUtils.FlagToBool(deliveryRes.getLocationSchema());

                //приведение времени
                Date startTime = StringUtils.getDateFromSapDateTime(
                        deliveryRes.getStartDate(), deliveryRes.getStartTime());
                Date finishTime = StringUtils.getDateFromSapDateTime(
                        deliveryRes.getFinishDate(), deliveryRes.getFinishTime());
                Date statusTime = StringUtils.getDateFromSapDateTime(
                        deliveryRes.getStatusDate(), deliveryRes.getStatusTime());

                setStartDateTime(startTime);
                setFinishDate(finishTime);
                setStatusDateTime(statusTime);
                shiftTime();

                mFullAddressFrom = getAddress(deliveryRes.getAddressFrom(),
                        deliveryRes.getLongitudeFrom(), deliveryRes.getLatitudeFrom());
                mFullAddressTo = getAddress(deliveryRes.getAddressTo(),
                        deliveryRes.getLongitudeTo(), deliveryRes.getLatitudeTo());

                //получение информации по дополнительным адресам
                mOtherAddressList = getOtherAddressList(deliveryRes.getOtherAddressesInJson());
                mOtherAddresses = getOtherAddresses();
            } catch (Exception exception) {
                Log.e("Не удалось получить данные по доставке", exception);
            }
        } else {
            mNumber = "";
        }
    }

    //endregion

}
