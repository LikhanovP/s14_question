package com.rosa.swift.core.data.repositories.databases.swift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SwiftDataBaseHelper extends SQLiteOpenHelper {

    public SwiftDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Текущая версия базы данных
     */
    private static final int VERSION = 4;

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "swiftDatabase.db";

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1:
                upgradeToVersion2(database);
            case 2:
                upgradeToVersion3(database);
            case 3:
                upgradeToVersion4(database);
        }

        database.setVersion(VERSION);
    }

    //region Tables

    private void createTables(SQLiteDatabase database) {
        createDelivery(database);
        createDeliveryAddress(database);
        createDeliveryWarehouse(database);
        createDeliveryTemplate(database);
        createTransportLocation(database);
        createPlant(database);
        createSession(database);
    }

    private void createDelivery(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` TEXT, " +
                        "`%5$s` TEXT, " +
                        "`%6$s` TEXT, " +
                        "`%7$s` REAL, " +
                        "`%8$s` TEXT, " +
                        "`%9$s` TEXT, " +
                        "`%10$s` TEXT, " +
                        "`%11$s` TEXT, " +
                        "`%12$s` INTEGER, " +
                        "`%13$s` REAL, " +
                        "`%14$s` REAL, " +
                        "`%15$s` TEXT, " +
                        "`%16$s` REAL, " +
                        "`%17$s` REAL, " +
                        "`%18$s` REAL, " +
                        "`%19$s` TEXT, " +
                        "`%20$s` TEXT, " +
                        "`%21$s` TEXT, " +
                        "`%22$s` TEXT, " +
                        "`%23$s` TEXT, " +
                        "`%24$s` TEXT, " +
                        "`%25$s` TEXT, " +
                        "`%26$s` REAL, " +
                        "`%27$s` REAL, " +
                        "`%28$s` TEXT, " +
                        "`%29$s` REAL, " +
                        "`%30$s` REAL, " +
                        "`%31$s` INTEGER, " +
                        "`%32$s` INTEGER, " +
                        "`%33$s` INTEGER)",
                SwiftDatabaseSchema.Tables.Delivery.NAME,
                SwiftDatabaseSchema.Tables.Delivery.Columns.NUMBER,
                SwiftDatabaseSchema.Tables.Delivery.Columns.SET_NUMBER,
                SwiftDatabaseSchema.Tables.Delivery.Columns.COMMENTS,
                SwiftDatabaseSchema.Tables.Delivery.Columns.FIRST_PHONE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.SECOND_PHONE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.COST,
                SwiftDatabaseSchema.Tables.Delivery.Columns.CUSTOMER,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LOGISTICIAN,
                SwiftDatabaseSchema.Tables.Delivery.Columns.MANAGER,
                SwiftDatabaseSchema.Tables.Delivery.Columns.MANAGER_PHONE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.STATUS,
                SwiftDatabaseSchema.Tables.Delivery.Columns.WEIGHT,
                SwiftDatabaseSchema.Tables.Delivery.Columns.VOLUME,
                SwiftDatabaseSchema.Tables.Delivery.Columns.PAY_ON_PLACE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LOADER_COST,
                SwiftDatabaseSchema.Tables.Delivery.Columns.OVER_WEIGHT_COST,
                SwiftDatabaseSchema.Tables.Delivery.Columns.ADDITION_COST,
                SwiftDatabaseSchema.Tables.Delivery.Columns.ADDITION_COST_INFO,
                SwiftDatabaseSchema.Tables.Delivery.Columns.PLANT_CODE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.TRANSPORT_CODE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.SET_ON_WAREHOUSE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.SCHEMA_EXIST,
                SwiftDatabaseSchema.Tables.Delivery.Columns.TYPE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.ADDRESS_FROM,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LONGITUDE_FROM,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LATITUDE_FROM,
                SwiftDatabaseSchema.Tables.Delivery.Columns.ADDRESS_TO,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LONGITUDE_TO,
                SwiftDatabaseSchema.Tables.Delivery.Columns.LATITUDE_TO,
                SwiftDatabaseSchema.Tables.Delivery.Columns.START_DATE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.FINISH_DATE,
                SwiftDatabaseSchema.Tables.Delivery.Columns.STATUS_DATE));
    }

    private void createDeliveryAddress(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` REAL, " +
                        "`%5$s` REAL, " +
                        "`%6$s` INTEGER, " +
                        "`%7$s` TEXT, " +
                        "`%8$s` TEXT)",
                SwiftDatabaseSchema.Tables.DeliveryAddress.NAME,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.DELIVERY_NUMBER,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.ADDRESS,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.LONGITUDE,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.LATITUDE,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.POSITION,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.CONTACT_NAME,
                SwiftDatabaseSchema.Tables.DeliveryAddress.Columns.CONTACT_PHONE));
    }

    private void createDeliveryTemplate(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` TEXT, " +
                        "`%5$s` INTEGER)",
                SwiftDatabaseSchema.Tables.DeliveryTemplate.NAME,
                SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.TEXT,
                SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.TYPE,
                SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.EVENING_TIME,
                SwiftDatabaseSchema.Tables.DeliveryTemplate.Columns.LAST_UPDATED));
    }

    private void createDeliveryWarehouse(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` TEXT)",
                SwiftDatabaseSchema.Tables.DeliveryWarehouse.NAME,
                SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.CODE,
                SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.NAME,
                SwiftDatabaseSchema.Tables.DeliveryWarehouse.Columns.DELIVERY_NUMBER));
    }

    private void createTransportLocation(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` REAL, " +
                        "`%5$s` REAL, " +
                        "`%6$s` INTEGER, " +
                        "`%7$s` TEXT)",
                SwiftDatabaseSchema.Tables.TransportLocation.NAME,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSPORTATION_ID,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSPORTATION_STATUS,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.LONGITUDE,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.LATITUDE,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.TIME,
                SwiftDatabaseSchema.Tables.TransportLocation.Columns.TRANSFERRED));
    }

    private void createPlant(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` TEXT, " +
                        "`%5$s` REAL, " +
                        "`%6$s` REAL, " +
                        "`%7$s` INTEGER, " +
                        "`%8$s` TEXT) ",
                SwiftDatabaseSchema.Tables.Plant.NAME,
                SwiftDatabaseSchema.Tables.Plant.Columns.CODE,
                SwiftDatabaseSchema.Tables.Plant.Columns.NAME,
                SwiftDatabaseSchema.Tables.Plant.Columns.ADDRESS,
                SwiftDatabaseSchema.Tables.Plant.Columns.LONGITUDE,
                SwiftDatabaseSchema.Tables.Plant.Columns.LATITUDE,
                SwiftDatabaseSchema.Tables.Plant.Columns.SELECTED,
                SwiftDatabaseSchema.Tables.Plant.Columns.TPLST_CODE));
    }

    private void createSession(SQLiteDatabase database) {
        database.execSQL(String.format(
                "CREATE TABLE `%1$s` (" +
                        "`%2$s` TEXT, " +
                        "`%3$s` TEXT, " +
                        "`%4$s` INTEGER, " +
                        "`%5$s` INTEGER) ",
                SwiftDatabaseSchema.Tables.Session.NAME,
                SwiftDatabaseSchema.Tables.Session.Columns.SESSION_ID,
                SwiftDatabaseSchema.Tables.Session.Columns.TYPE_TRANSPORT,
                SwiftDatabaseSchema.Tables.Session.Columns.LAST_QUEUE_ID,
                SwiftDatabaseSchema.Tables.Session.Columns.IS_LOGIN_ONLY));
    }

    //endregion

    //region Upgrades

    private void upgradeToVersion2(SQLiteDatabase database) {
        createDeliveryTemplate(database);
    }

    private void upgradeToVersion3(SQLiteDatabase database) {
        createDelivery(database);
        createDeliveryAddress(database);
        createDeliveryWarehouse(database);
    }

    private void upgradeToVersion4(SQLiteDatabase database) {
        createPlant(database);
        createSession(database);
    }

    //endregion

}
