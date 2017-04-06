package com.rosa.swift.core.data.repositories.databases.swift.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.text.TextUtils;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema;

import java.util.Date;

public class DeliveryWrapper extends CursorWrapper {
    public DeliveryWrapper(Cursor cursor) {
        super(cursor);
    }

    public Delivery getDelivery() {
        try {
            Delivery delivery = new Delivery();

            delivery.setNumber(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.NUMBER)));
            delivery.setSetNumber(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.SET_NUMBER)));
            delivery.setCustomerName(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.CUSTOMER)));
            delivery.setFirstPhone(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.FIRST_PHONE)));
            delivery.setSecondPhone(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.SECOND_PHONE)));
            delivery.setManagerName(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.MANAGER)));
            delivery.setManagerPhone(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.MANAGER_PHONE)));
            delivery.setLogisticianName(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LOGISTICIAN)));
            delivery.setStatus(getInt(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.STATUS)));
            delivery.setWeight(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.WEIGHT)));
            delivery.setVolume(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.VOLUME)));
            delivery.setCost(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.COST)));
            delivery.setLoaderCost(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LOADER_COST)));

            delivery.setOverWeightCost(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.OVER_WEIGHT_COST)));
            delivery.setAddCost(getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.ADDITION_COST)));

            delivery.setAddCostInfo(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.ADDITION_COST_INFO)));
            delivery.setComments(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.COMMENTS)));
            delivery.setPlantCode(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.PLANT_CODE)));
            delivery.setTransportCode(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.TRANSPORT_CODE)));

            delivery.setPayOnPlace(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.PAY_ON_PLACE))
                    .equals(Constants.SAP_TRUE_FLAG));
            delivery.setSetOnWarehouse(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.SET_ON_WAREHOUSE))
                    .equals(Constants.SAP_TRUE_FLAG));
            delivery.setSchemaExist(getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.SCHEMA_EXIST))
                    .equals(Constants.SAP_TRUE_FLAG));

            String typeString = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.TYPE));
            delivery.setType(TransportationType.getType(typeString));

            String addressFrom = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.ADDRESS_FROM));
            double longitudeFrom = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LONGITUDE_FROM));
            double latitudeFrom = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LATITUDE_FROM));
            if (!TextUtils.isEmpty(addressFrom)) {
                delivery.setFullAddressFrom(new GeoObject(addressFrom, longitudeFrom, latitudeFrom));
            }

            String addressTo = getString(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.ADDRESS_TO));
            double longitudeTo = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LONGITUDE_TO));
            double latitudeTo = getDouble(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.LATITUDE_TO));
            if (!TextUtils.isEmpty(addressTo)) {
                delivery.setFullAddressTo(new GeoObject(addressTo, longitudeTo, latitudeTo));
            }

            long startDateMillis = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.START_DATE));
            long finishDateMillis = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.FINISH_DATE));
            long statusDateMillis = getLong(getColumnIndex(SwiftDatabaseSchema.Tables.Delivery.Columns.STATUS_DATE));

            delivery.setStartDateTime(startDateMillis > 0 ? new Date(startDateMillis) : null);
            delivery.setFinishDateTime(finishDateMillis > 0 ? new Date(finishDateMillis) : null);
            delivery.setStatusDateTime(statusDateMillis > 0 ? new Date(statusDateMillis) : null);

            delivery.updateSummaryCost();

            return delivery;

        } catch (Exception exception) {
            Log.e("Ошибка считывания доставки из базы данных", exception);
            return null;
        }

        //mWarehouseList = getWarehouseList(deliveryRes.getWarehouses());

        //mOtherAddressList = getOtherAddressList(deliveryRes.getOtherAddressesInJson());
    }

}
