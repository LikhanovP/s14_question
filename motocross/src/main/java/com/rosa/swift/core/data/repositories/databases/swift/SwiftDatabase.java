package com.rosa.swift.core.data.repositories.databases.swift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.common.DeliveryAddress;
import com.rosa.swift.core.data.dto.common.DeliveryWarehouse;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.data.dto.common.Warehouse;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.repositories.DatabaseRepository;
import com.rosa.swift.core.data.repositories.databases.swift.SwiftDatabaseSchema.Tables;
import com.rosa.swift.core.data.repositories.databases.swift.wrappers.DeliveryAddressWrapper;
import com.rosa.swift.core.data.repositories.databases.swift.wrappers.DeliveryWarehouseWrapper;
import com.rosa.swift.core.data.repositories.databases.swift.wrappers.DeliveryWrapper;
import com.rosa.swift.core.data.repositories.databases.swift.wrappers.PlantWrapper;
import com.rosa.swift.core.data.repositories.databases.swift.wrappers.TransportLocationWrapper;

import java.util.ArrayList;
import java.util.List;

public class SwiftDatabase implements DatabaseRepository {

    private final SQLiteDatabase mDatabase;

    public SwiftDatabase(Context context) {
        mDatabase = new SwiftDataBaseHelper(context).getWritableDatabase();
    }

    //region Delivery

    @Override
    public List<Delivery> selectTransportations(TransportationType type) {
        return selectDeliveries(Tables.Delivery.Columns.TYPE + " = ?",
                new String[]{type.name()});
    }

    @Override
    public void insertDeliveries(List<Delivery> deliveries) {
        if (deliveries != null) {
            for (Delivery delivery : deliveries) {
                insertDelivery(delivery);
            }
        }
    }

    @Override
    public void insertOrUpdateDelivery(Delivery delivery) {
        if (delivery != null && !TextUtils.isEmpty(delivery.getNumber())) {
            boolean isDeliveryExist = selectTransportation(delivery.getNumber(), delivery.getType()) != null;
            if (isDeliveryExist) {
                deleteTransportation(delivery.getNumber(), delivery.getType());
                deleteDeliveryAddress(delivery.getNumber());
                deleteDeliveryWarehouse(delivery.getNumber());
            }
            insertDelivery(delivery);
        }
    }

    @Override
    public Delivery selectTransportation(String number, TransportationType type) {
        List<Delivery> deliveries = selectDeliveries(String.format("%s='%s' AND %s='%s'",
                Tables.Delivery.Columns.NUMBER, number,
                Tables.Delivery.Columns.TYPE, type.toString()), null);
        return deliveries.size() == 1 ? deliveries.get(0) : null;
    }

    @Override
    public void deleteTransportation(String number, TransportationType type) {
        if (!TextUtils.isEmpty(number)) {
            deleteDeliveryAddress(number);
            deleteDeliveryWarehouse(number);
            mDatabase.delete(Tables.Delivery.NAME,
                    Tables.Delivery.Columns.NUMBER + " = ?",
                    new String[]{number});
        }
    }

    /*
    @Override
    public void deleteTransportation(String number, TransportationType type) {
        if (!TextUtils.isEmpty(number)) {
            deleteDeliveryAddress(number);
            deleteDeliveryWarehouse(number);
            mDatabase.execSQL(String.format("DELETE FROM %s WHERE %s='%s' AND %s='%s'",
                    SwiftDatabaseSchema.Tables.Delivery.NAME,
                    SwiftDatabaseSchema.Tables.Delivery.Columns.NUMBER, number,
                    SwiftDatabaseSchema.Tables.Delivery.Columns.TYPE, type.toString()));
        }
    }
    */

    @Override
    public void deleteTransportations(TransportationType type) {
        mDatabase.delete(Tables.Delivery.NAME, Tables.Delivery.Columns.TYPE + " = ?",
                new String[]{type.toString()});
    }

    @Override
    public void deleteTransportations() {
        mDatabase.delete(Tables.Delivery.NAME, null, null);
    }

    @NonNull
    private List<Delivery> selectDeliveries(String whereClause, String[] whereArgs) {
        List<Delivery> deliveries = new ArrayList<>();
        DeliveryWrapper cursor = queryDeliveries(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Delivery delivery = cursor.getDelivery();
                if (delivery != null) {
                    delivery.setOtherAddressList(selectDeliveryAddresses(delivery.getNumber()));
                    delivery.setWarehouseList(selectDeliveryWarehouses(delivery.getNumber()));
                    deliveries.add(delivery);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return deliveries;
    }

    private void insertDelivery(Delivery delivery) {
        if (delivery != null) {
            mDatabase.insert(Tables.Delivery.NAME, null,
                    getDeliveriesContentValues(delivery));

            insertDeliveryAddresses(delivery);
            insertDeliveryWarehouse(delivery);
        }
    }

    private void insertDeliveryAddresses(Delivery delivery) {
        List<DeliveryAddress> addresses = delivery.getOtherAddressList();
        if (addresses != null) {
            for (DeliveryAddress address : addresses) {
                insertDeliveryAddress(address);
            }
        }
    }

    private void insertDeliveryWarehouse(Delivery delivery) {
        List<Warehouse> warehouses = delivery.getWarehouseList();
        if (warehouses != null) {
            for (Warehouse warehouse : warehouses) {
                insertDeliveryWarehouse(new DeliveryWarehouse(delivery.getNumber(), warehouse));
            }
        }
    }

    private DeliveryWrapper queryDeliveries(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Tables.Delivery.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                Tables.Delivery.Columns.START_DATE // orderBy
        );
        return new DeliveryWrapper(cursor);
    }

    private static ContentValues getDeliveriesContentValues(Delivery delivery) {
        ContentValues values = new ContentValues();
        values.put(Tables.Delivery.Columns.NUMBER, delivery.getNumber());
        values.put(Tables.Delivery.Columns.SET_NUMBER, delivery.getSetNumber());
        values.put(Tables.Delivery.Columns.CUSTOMER, delivery.getCustomerName());
        values.put(Tables.Delivery.Columns.FIRST_PHONE, delivery.getFirstPhone());
        values.put(Tables.Delivery.Columns.SECOND_PHONE, delivery.getSecondPhone());
        values.put(Tables.Delivery.Columns.MANAGER, delivery.getManagerName());
        values.put(Tables.Delivery.Columns.MANAGER_PHONE, delivery.getManagerPhone());
        values.put(Tables.Delivery.Columns.LOGISTICIAN, delivery.getLogisticianName());
        values.put(Tables.Delivery.Columns.STATUS, delivery.getStatus());
        values.put(Tables.Delivery.Columns.WEIGHT, delivery.getWeight());
        values.put(Tables.Delivery.Columns.VOLUME, delivery.getVolume());
        values.put(Tables.Delivery.Columns.COST, delivery.getCost());
        values.put(Tables.Delivery.Columns.LOADER_COST, delivery.getLoaderCost());
        values.put(Tables.Delivery.Columns.OVER_WEIGHT_COST, delivery.getOverWeightCost());
        values.put(Tables.Delivery.Columns.ADDITION_COST, delivery.getAddCost());
        values.put(Tables.Delivery.Columns.ADDITION_COST_INFO, delivery.getAddCostInfo());
        values.put(Tables.Delivery.Columns.COMMENTS, delivery.getComments());
        values.put(Tables.Delivery.Columns.PLANT_CODE, delivery.getPlantCode());
        values.put(Tables.Delivery.Columns.TRANSPORT_CODE, delivery.getTransportCode());

        values.put(Tables.Delivery.Columns.PAY_ON_PLACE, delivery.isPayOnPlace() ?
                Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG);
        values.put(Tables.Delivery.Columns.SET_ON_WAREHOUSE, delivery.isSetOnWarehouse() ?
                Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG);
        values.put(Tables.Delivery.Columns.SCHEMA_EXIST, delivery.isSchemaExist() ?
                Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG);

        if (delivery.getType() != null) {
            values.put(Tables.Delivery.Columns.TYPE, delivery.getType().toString());
        }

        GeoObject startAddress = delivery.getStartAddress();
        if (startAddress != null) {
            values.put(Tables.Delivery.Columns.ADDRESS_FROM, startAddress.getAddress());
            values.put(Tables.Delivery.Columns.LONGITUDE_FROM, startAddress.getLongitude());
            values.put(Tables.Delivery.Columns.LATITUDE_FROM, startAddress.getLatitude());
        }
        GeoObject finishAddress = delivery.getFinishAddress();
        if (finishAddress != null) {
            values.put(Tables.Delivery.Columns.ADDRESS_TO, finishAddress.getAddress());
            values.put(Tables.Delivery.Columns.LONGITUDE_TO, finishAddress.getLongitude());
            values.put(Tables.Delivery.Columns.LATITUDE_TO, finishAddress.getLatitude());
        }

        if (delivery.getStartDate() != null) {
            values.put(Tables.Delivery.Columns.START_DATE, delivery.getStartDate().getTime());
        }
        if (delivery.getFinishDate() != null) {
            values.put(Tables.Delivery.Columns.FINISH_DATE, delivery.getFinishDate().getTime());
        }
        if (delivery.getStatusDate() != null) {
            values.put(Tables.Delivery.Columns.STATUS_DATE, delivery.getStatusDate().getTime());
        }

        return values;
    }

    //endregion

    //region DeliveryWarehouse

    private List<Warehouse> selectDeliveryWarehouses(String deliveryNumber) {
        List<Warehouse> warehouses = new ArrayList<>();
        DeliveryWarehouseWrapper cursor = queryDeliveryWarehouses(
                Tables.DeliveryWarehouse.Columns.DELIVERY_NUMBER + " = ?",
                new String[]{deliveryNumber});
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                warehouses.add(cursor.getDeliveryWarehouse());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return warehouses;
    }

    private void insertDeliveryWarehouse(DeliveryWarehouse warehouse) {
        ContentValues values = getDeliveryWarehouseContentValues(warehouse);
        mDatabase.insert(Tables.DeliveryWarehouse.NAME, null, values);
    }

    private void deleteDeliveryWarehouse(String number) {
        mDatabase.delete(Tables.DeliveryWarehouse.NAME,
                Tables.DeliveryWarehouse.Columns.DELIVERY_NUMBER + " = ?",
                new String[]{number});
    }

    private DeliveryWarehouseWrapper queryDeliveryWarehouses(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Tables.DeliveryWarehouse.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                Tables.DeliveryWarehouse.Columns.CODE // orderBy
        );
        return new DeliveryWarehouseWrapper(cursor);
    }

    private static ContentValues getDeliveryWarehouseContentValues(DeliveryWarehouse address) {
        ContentValues values = new ContentValues();
        values.put(Tables.DeliveryWarehouse.Columns.CODE, address.getCode());
        values.put(Tables.DeliveryWarehouse.Columns.NAME, address.getName());
        values.put(Tables.DeliveryWarehouse.Columns.DELIVERY_NUMBER, address.getDeliveryNumber());
        return values;
    }

    //endregion

    //region DeliveryAddress

    private List<DeliveryAddress> selectDeliveryAddresses(String deliveryNumber) {
        List<DeliveryAddress> addresses = new ArrayList<>();
        DeliveryAddressWrapper cursor = queryDeliveryAddresses(
                Tables.DeliveryAddress.Columns.DELIVERY_NUMBER + " = ?",
                new String[]{deliveryNumber});
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                addresses.add(cursor.getDeliveryAddress());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return addresses;
    }

    private void insertDeliveryAddress(DeliveryAddress address) {
        ContentValues values = getDeliveryAddressContentValues(address);
        mDatabase.insert(Tables.DeliveryAddress.NAME, null, values);
    }

    private void deleteDeliveryAddress(String number) {
        mDatabase.delete(Tables.DeliveryAddress.NAME, Tables.DeliveryAddress.Columns.DELIVERY_NUMBER + " = ?",
                new String[]{number});
    }

    private DeliveryAddressWrapper queryDeliveryAddresses(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Tables.DeliveryAddress.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                Tables.DeliveryAddress.Columns.POSITION // orderBy
        );
        return new DeliveryAddressWrapper(cursor);
    }

    private static ContentValues getDeliveryAddressContentValues(DeliveryAddress address) {
        ContentValues values = new ContentValues();
        values.put(Tables.DeliveryAddress.Columns.DELIVERY_NUMBER, address.getTknum());
        values.put(Tables.DeliveryAddress.Columns.ADDRESS, address.getAddress());
        values.put(Tables.DeliveryAddress.Columns.LONGITUDE, address.getLongitude());
        values.put(Tables.DeliveryAddress.Columns.LATITUDE, address.getLatitude());
        values.put(Tables.DeliveryAddress.Columns.POSITION, address.getPosition());
        values.put(Tables.DeliveryAddress.Columns.CONTACT_NAME, address.getContactName());
        values.put(Tables.DeliveryAddress.Columns.CONTACT_PHONE, address.getPhone());
        return values;
    }

    //endregion

    //region DeliveryLocation

    @Override
    public List<DeliveryLocation> selectDeliveryLocations() {
        return selectDeliveryLocations(null, null);
    }

    private List<DeliveryLocation> selectDeliveryLocations(String whereClause, String[] whereArgs) {
        List<DeliveryLocation> locationList = new ArrayList<>();
        TransportLocationWrapper cursor = queryTransportLocations(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locationList.add(cursor.getTransportLocation());
                cursor.moveToNext();
            }
        } finally {
            //закрываем курсор
            cursor.close();
        }
        return locationList;
    }

    @Override
    public void insertDeliveryLocation(DeliveryLocation location) {
        ContentValues values = getTransportLocationContentValues(location);
        mDatabase.insert(Tables.TransportLocation.NAME, null, values);
    }

    @Override
    public void deleteDeliveryLocations() {
        mDatabase.delete(Tables.TransportLocation.NAME, null, null);
    }

    private TransportLocationWrapper queryTransportLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Tables.TransportLocation.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                Tables.TransportLocation.Columns.TIME // orderBy
        );

        return new TransportLocationWrapper(cursor);
    }

    private static ContentValues getTransportLocationContentValues(DeliveryLocation location) {
        ContentValues values = new ContentValues();
        values.put(Tables.TransportLocation.Columns.TRANSPORTATION_ID, location.getIdTransportation());
        values.put(Tables.TransportLocation.Columns.TRANSPORTATION_STATUS, location.getStatusTransportation());
        values.put(Tables.TransportLocation.Columns.LONGITUDE, location.getLongitude());
        values.put(Tables.TransportLocation.Columns.LATITUDE, location.getLatitude());
        values.put(Tables.TransportLocation.Columns.TIME, location.getTime());
        values.put(Tables.TransportLocation.Columns.TRANSFERRED, location.isTransferred() ?
                Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG);
        return values;
    }

    //endregion

    //region Plant

    @Nullable
    @Override
    public TplstCollection selectPlants(String parentCode) {
        List<Tplst> plants = selectPlants(Tables.Plant.Columns.TPLST_CODE + " = ?",
                new String[]{parentCode});
        return plants != null ? new TplstCollection(parentCode, plants) : null;
    }

    @Nullable
    @Override
    public void updatePlants(TplstCollection plants) {
        deletePlants();
        insertPlants(plants);
    }

    @Nullable
    @Override
    public Tplst selectPlant(String code, String parentCode) {
        List<Tplst> plants = selectPlants(String.format("%s='%s' AND %s='%s'",
                Tables.Plant.Columns.CODE, code,
                Tables.Plant.Columns.TPLST_CODE, parentCode), null);

        return plants.size() == 1 ? plants.get(0) : null;
    }

    @Override
    public void insertPlants(TplstCollection plants) {
        if (plants != null && plants.size() > 0) {
            for (Tplst plant : plants) {
                mDatabase.insert(Tables.Plant.NAME, null, getPlantContentValues(plant));
            }
        }
    }

    @Override
    public void deletePlants() {
        mDatabase.delete(Tables.Plant.NAME, null, null);
    }

    private List<Tplst> selectPlants(String whereClause, String[] whereArgs) {
        List<Tplst> plants = new ArrayList<>();
        PlantWrapper cursor = queryPlants(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tplst plant = cursor.getPlant();
                if (plant != null) {
                    plants.add(plant);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return plants;
    }

    private PlantWrapper queryPlants(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Tables.Plant.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                Tables.Plant.Columns.NAME // orderBy
        );

        return new PlantWrapper(cursor);
    }

    private static ContentValues getPlantContentValues(Tplst plant) {
        ContentValues values = new ContentValues();
        values.put(Tables.Plant.Columns.CODE, plant.getCode());
        values.put(Tables.Plant.Columns.NAME, plant.getName());
        values.put(Tables.Plant.Columns.ADDRESS, plant.getAddress());
        values.put(Tables.Plant.Columns.LONGITUDE, plant.getLongitude());
        values.put(Tables.Plant.Columns.LATITUDE, plant.getLatitude());
        values.put(Tables.Plant.Columns.SELECTED, plant.isSelected() ?
                Constants.SAP_TRUE_FLAG : Constants.SAP_FALSE_FLAG);
        values.put(Tables.Plant.Columns.TPLST_CODE, plant.getParentCode());
        return values;
    }

    //endregion

}
