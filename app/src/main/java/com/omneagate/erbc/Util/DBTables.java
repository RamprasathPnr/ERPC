package com.omneagate.erbc.Util;

/**
 * Created by user1 on 6/5/16.
 */
public class DBTables {

    //Key for id in tables
    public final static String KEY_ID = "_id";

    // fpsUsers table name
    public static final String TABLE_USERS = "users";


    // marital_status table name
    public static final String TABLE_MARITAL_STATUS = "marital_status";

    // occupation table name
    public static final String TABLE_OCCUAPTION = "occupation";

    //country table name
    public static final String TABLE_COUNTRY = "country";

    // state table name
    public static final String TABLE_STATE = "state";

    // district table name
    public static final String TABLE_DISTRICT = "district";

    // taluk table name
    public static final String TABLE_TALUK = "taluk";

    public static final String TABLE_CONFIG_TABLE = "configuration";

    public static final String TABLE_APPLIANCE = "appliance";

    public static final String TABLE_STAR_RATING="star_rating";


    public static final String CREATE_OCCPATIONS_TABLE = "CREATE TABLE " + TABLE_OCCUAPTION + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_MARITAL_STATUS_TABLE = "CREATE TABLE " + TABLE_MARITAL_STATUS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_DISTRICT_TABLE = "CREATE TABLE " + TABLE_DISTRICT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_TALUK_TABLE = "CREATE TABLE " + TABLE_TALUK + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,id INTEGER  UNIQUE" + " )";

    public static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY, id INTEGER UNIQUE,"
            + DBConstants.KEY_FIRST_NAME + " VARCHAR(150) NOT NULL ," + DBConstants.KEY_LAST_NAME + " VARCHAR(150),"
            + DBConstants.KEY_MIDDLE_NAME + " VARCHAR(150), " + DBConstants.KEY_EMAIL_NAME + " VARCHAR(30),"
            + DBConstants.KEY_ADDRESS1 + "  VARCHAR(30), " + DBConstants.KEY_ADDRESS2 + " VARCHAR(15),"
            + DBConstants.KEY_DOB + " VARCHAR(60), " + DBConstants.KEY_COUNTRY_CODE + " INTEGER,"
            + DBConstants.KEY_MOBILE_NUMBER + " VARCHAR(30), " + DBConstants.KEY_PINCODE + " VARCHAR(30), "
            + DBConstants.KEY_STATUS + " VARCHAR(150)," + DBConstants.KEY_GENDER + " VARCHAR(60), "
            + DBConstants.KEY_MSTATUS + " VARCHAR(60)," + DBConstants.KEY_OCCUAPTION + " VARCHAR(60), "
            + DBConstants.KEY_COUNTRY + " VARCHAR(60)," + DBConstants.KEY_STATE + " VARCHAR(15),"
            + DBConstants.KEY_DISTRICT + " VARCHAR(60), " + DBConstants.KEY_TALUK + " VARCHAR(60)," + DBConstants.KEY_VILLAGE + " VARCHAR(15),"
            + DBConstants.KEY_MSTATUS_ID + " INTEGER," + DBConstants.KEY_OCCUAPTION_ID + " INTEGER, "
            + DBConstants.KEY_COUNTRY_ID + " INTEGER," + DBConstants.KEY_STATE_ID + " INTEGER,"
            + DBConstants.KEY_DISTRICT_ID + " INTEGER, " + DBConstants.KEY_TALUK_ID + " INTEGER," + DBConstants.KEY_VILLAGE_ID + " INTEGER" +" )";

    // card type table with card types
    public static final String CREATE_MASTER_TABLE = "CREATE TABLE " + TABLE_CONFIG_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,value VARCHAR(150)  UNIQUE" + " )";

    public static final String CREATE_APPLIANCE="CREATE TABLE " + TABLE_APPLIANCE + "(" + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            +"appliance_code VARCHAR(50) NOT NULL UNIQUE ,appliance_name VARCHAR(50) NOT NULL,capacity_in_watts INTEGER,is_star_rated INTEGER,units_per_month DOUBLE " +" )";

    public static final String CREATE_STAR_RATING ="CREATE TABLE " + TABLE_STAR_RATING  +"(" + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            +"star_rating INTEGER, percentage_of_savings INTEGER " +" )";

}
