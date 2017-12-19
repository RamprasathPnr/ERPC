package com.omneagate.erbc.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DistrictDto;
import com.omneagate.erbc.Dto.EnumDto.Gender;
import com.omneagate.erbc.Dto.MaritalStatusDto;
import com.omneagate.erbc.Dto.OccupationDto;
import com.omneagate.erbc.Dto.StateDto;
import com.omneagate.erbc.Dto.TalukDto;
import com.omneagate.erbc.Dto.VillageDto;
import com.omneagate.erbc.Dto.countryDto;
import com.omneagate.erbc.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user1 on 6/5/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database Name
    public static final String DATABASE_NAME = "ERBC.db";

    //Key for id in tables
    public final static String KEY_ID = "id";

    // Database Version
    private static final int DATABASE_VERSION = 27;

    // All Static variables
    private static com.omneagate.erbc.Util.DBHelper dbHelper = null;

    private static SQLiteDatabase database = null;

    private static Context contextValue;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this.getWritableDatabase();
        dbHelper = this;
        contextValue = context;
    }


    //Singleton to Instantiate the SQLiteOpenHelper
    public static com.omneagate.erbc.Util.DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new com.omneagate.erbc.Util.DBHelper(context);
            openConnection();
        }
        contextValue = context;
        return dbHelper;
    }

    // It is used to open database
    private static void openConnection() {
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            Log.e("Inside DB", "DB Creation");
            db.execSQL(DBTables.CREATE_MARITAL_STATUS_TABLE);
            db.execSQL(DBTables.CREATE_OCCPATIONS_TABLE);
            db.execSQL(DBTables.CREATE_COUNTRY_TABLE);
            db.execSQL(DBTables.CREATE_STATE_TABLE);
            db.execSQL(DBTables.CREATE_DISTRICT_TABLE);
            db.execSQL(DBTables.CREATE_TALUK_TABLE);
            db.execSQL(DBTables.CREATE_CUSTOMER_TABLE);
            db.execSQL(DBTables.CREATE_MASTER_TABLE);
            db.execSQL(DBTables.CREATE_STAR_RATING);
            db.execSQL(DBTables.CREATE_APPLIANCE);

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("db helper oncreate", "exception...." + e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try{

            switch (oldVersion){
                case 26:
                    db.execSQL(DBTables.CREATE_STAR_RATING);
                    db.execSQL(DBTables.CREATE_APPLIANCE);
                    break;
                default:
                    break;
            }


        }catch (Exception e){
            e.printStackTrace();
        }



    }


    public void insertCountry(List<countryDto> countryList) {
        try {
            if (!countryList.isEmpty()) {
                for (countryDto country : countryList) {
                    ContentValues values = new ContentValues();
                    values.put(DBConstants.KEY_ID, 1);
                    values.put(DBConstants.KEY_NAME, "India");
                    database.insertWithOnConflict(DBTables.TABLE_COUNTRY, DBConstants.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception_insert", e.toString());
        }

    }

    public void insertApplianceValue(ApplianceDto appliancedto) {
        try {
            ContentValues values = new ContentValues();
            values.put("appliance_code", appliancedto.getApplianceCode());
            values.put("appliance_name", appliancedto.getApplianceName());
            values.put("capacity_in_watts", appliancedto.getCapacityInWatts());
            values.put("is_star_rated", returnInteger(appliancedto.isStarRated()));
           // values.put("units_per_month", appliancedto.getUnits_per_hour());
            database.insertWithOnConflict(DBTables.TABLE_APPLIANCE, DBConstants.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception_insert", e.toString());
        }
    }
    private int returnInteger(boolean value) {
        if (value)
            return 1;
        return 0;
    }

    public void insertStarRated(int star_rated,int percentage_of_saving){
        try{
            ContentValues values = new ContentValues();
            values.put("star_rating",star_rated);
            values.put("percentage_of_savings",percentage_of_saving);
            database.insertWithOnConflict(DBTables.TABLE_STAR_RATING, DBConstants.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<ApplianceDto> getApplianceList(){

            List<ApplianceDto> applianceList = new ArrayList<>();
            String selectQuery = "select * from appliance";
            Log.i("query:  ", selectQuery);
            Cursor cursor =database .rawQuery(selectQuery,null);
            cursor.moveToFirst();

        int[] img = {R.drawable.tv,R.drawable.audio_system,R.drawable.refrigerator,R.drawable.lamp_bulb,R.drawable.flu_lamp1,
                R.drawable.tube_light1,R.drawable.dry_iron,R.drawable.micro_waveoven,R.drawable.electric_toaster,R.drawable.storage_water_heater,
                R.drawable.instant_water_heater,R.drawable.washing_machine1,R.drawable.mixer,R.drawable.grinder,R.drawable.charger,R.drawable.emerency_lam
                ,R.drawable.air_conditioner,R.drawable.pump_set1};

            if (cursor.getCount() == 0) {
                cursor.close();
                Log.e("query:  ", selectQuery);
                return null;
            } else {
                for (int i = 0; i < cursor.getCount(); i++) {
                    ApplianceDto applianceDto =new ApplianceDto();
                    applianceDto.setApplianceCode(cursor.getString(cursor.getColumnIndex("appliance_code")));
                    applianceDto.setApplianceName(cursor.getString(cursor.getColumnIndex("appliance_name")));
                    applianceDto.setStarRated(returnBoolean(cursor.getInt(cursor.getColumnIndex("is_star_rated"))));
                    applianceDto.setCapacityInWatts(cursor.getFloat(cursor.getColumnIndex("capacity_in_watts")));
                    applianceDto.setImageDrawableId(img[i]);
                    applianceList.add(applianceDto);
                    cursor.moveToNext();
                }
                cursor.close();
            }

       return applianceList;
    }

    private boolean returnBoolean(int value) {
        return value == 1;

    }
    public List<countryDto> getCountryList() {
        List<countryDto> country = new ArrayList<>();
        String selectQuery = "select * from country";
        Log.i("query:  ", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            Log.e("query:  ", selectQuery);
            return null;
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                country.add(new countryDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return country;
        }
    }


    public void insertCountry1() {
        try {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY_ID, 1);
            values.put(DBConstants.KEY_NAME, "India");
            database.insertWithOnConflict(DBTables.TABLE_COUNTRY, DBConstants.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception_insert", e.toString());
        }

    }

    public void insertCustomer(CustomerDto customerdto) {
        ContentValues values = new ContentValues();
        try {
            values.put(DBTables.KEY_ID, "1");
            values.put(KEY_ID, customerdto.getId());
            values.put(DBConstants.KEY_FIRST_NAME, customerdto.getFirstName());
            values.put(DBConstants.KEY_MIDDLE_NAME, customerdto.getMiddleName());
            values.put(DBConstants.KEY_LAST_NAME, customerdto.getLastName());
            values.put(DBConstants.KEY_EMAIL_NAME, customerdto.getEmail());
            values.put(DBConstants.KEY_ADDRESS1, customerdto.getAddressLine1());
            values.put(DBConstants.KEY_ADDRESS2, customerdto.getAddressLine2());
            values.put(DBConstants.KEY_DOB, customerdto.getDob());
            values.put(DBConstants.KEY_COUNTRY_CODE, customerdto.getCountryCode());
            values.put(DBConstants.KEY_MOBILE_NUMBER, customerdto.getMobileNumber());
            values.put(DBConstants.KEY_PINCODE, customerdto.getPinCode());
            if (customerdto.isStatus()) {
                values.put(DBConstants.KEY_STATUS, 1);
            } else {
                values.put(DBConstants.KEY_STATUS, 0);
            }
            values.put(DBConstants.KEY_GENDER, "" + customerdto.getGender());


            values.put(DBConstants.KEY_COUNTRY, customerdto.getCountry().getName());
            values.put(DBConstants.KEY_STATE, customerdto.getState().getName());
            values.put(DBConstants.KEY_DISTRICT, customerdto.getDistrict().getName());
            values.put(DBConstants.KEY_TALUK, customerdto.getTaluk().getName());
            values.put(DBConstants.KEY_VILLAGE, customerdto.getVillage().getName());
            if(customerdto.getMaritalStatus() !=null){
                values.put(DBConstants.KEY_MSTATUS_ID, customerdto.getMaritalStatus().getId());
                values.put(DBConstants.KEY_MSTATUS, customerdto.getMaritalStatus().getName());
            }
            if(customerdto.getOccupation() != null){
                values.put(DBConstants.KEY_OCCUAPTION_ID, customerdto.getOccupation().getId());
                values.put(DBConstants.KEY_OCCUAPTION, customerdto.getOccupation().getName());
            }


            values.put(DBConstants.KEY_COUNTRY_ID, customerdto.getCountry().getId());
            values.put(DBConstants.KEY_STATE_ID, customerdto.getState().getId());
            values.put(DBConstants.KEY_DISTRICT_ID, customerdto.getDistrict().getId());
            values.put(DBConstants.KEY_TALUK_ID, customerdto.getTaluk().getId());
            values.put(DBConstants.KEY_VILLAGE_ID, customerdto.getVillage().getId());
            database.insertWithOnConflict(DBTables.TABLE_USERS, DBTables.KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception_insert", e.toString());
        }

    }

    public void deleteConnection() {
        try {
            String sql = "Delete from users";
            database.execSQL(sql);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("deleteConenction", e.toString(), e);
        }


    }

    public int getStarPercentage(int starrating) {

        String sql = "select  percentage_of_savings from star_rating where star_rating =" + starrating;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int starpercentage = cursor.getInt(cursor.getColumnIndex("percentage_of_savings"));
        cursor.close();
        return starpercentage;

    }
    public CustomerDto getcustomerData() {
        try {
            CustomerDto cusomerRecord;
            String selectQuery = "select * from users";
            Log.e("query:  ", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                Log.e("query:  ", selectQuery);
                return null;
            } else {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    cusomerRecord = new CustomerDto();
                    MaritalStatusDto mstatus = new MaritalStatusDto();
                    OccupationDto occupationDto = new OccupationDto();
                    countryDto countrydto = new countryDto();
                    StateDto state = new StateDto();
                    DistrictDto district = new DistrictDto();
                    TalukDto taluk = new TalukDto();
                    VillageDto village = new VillageDto();
                    cusomerRecord.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    cusomerRecord.setFirstName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_FIRST_NAME)) + "");
                    cusomerRecord.setMiddleName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_MIDDLE_NAME)) + "");
                    cusomerRecord.setLastName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_LAST_NAME)) + "");
                    cusomerRecord.setEmail(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_EMAIL_NAME)) + "");
                    cusomerRecord.setAddressLine1(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_ADDRESS1)) + "");
                    cusomerRecord.setAddressLine2(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_ADDRESS2)) + "");
                    cusomerRecord.setDob(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_DOB)) + "");
                    cusomerRecord.setMobileNumber(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER)) + "");
                    cusomerRecord.setPinCode(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_PINCODE)) + "");
                    cusomerRecord.setGender(Gender.valueOf(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_GENDER)) + ""));
                    mstatus.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_MSTATUS)));
                    occupationDto.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_OCCUAPTION)));
                    countrydto.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_COUNTRY)) + "");
                    state.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_STATE)) + "");
                    district.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_DISTRICT)) + "");
                    taluk.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_TALUK)) + "");
                    village.setName(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_VILLAGE)) + "");
                    mstatus.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_MSTATUS_ID)));
                    occupationDto.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_OCCUAPTION_ID)));
                    countrydto.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_COUNTRY_ID)));
                    state.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_STATE_ID)));
                    district.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_DISTRICT_ID)));
                    taluk.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_TALUK_ID)));
                    village.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.KEY_VILLAGE_ID)));
                    cusomerRecord.setCountryCode(cursor.getString(cursor.getColumnIndex(DBConstants.KEY_COUNTRY_CODE)) + "");
                    cusomerRecord.setMaritalStatus(mstatus);
                    cusomerRecord.setOccupation(occupationDto);
                    cusomerRecord.setCountry(countrydto);
                    cusomerRecord.setState(state);
                    cusomerRecord.setDistrict(district);
                    cusomerRecord.setTaluk(taluk);
                    cusomerRecord.setVillage(village);
                    return cusomerRecord;
                }
                cursor.close();

            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString());
        }
        return null;
    }

    public int getCustomerCount() {
        try {
            String countQuery = "SELECT  * FROM " + DBTables.TABLE_USERS;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();
            return cnt;

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error in get count", e.toString());
        }

        return 0;
    }

    public void insertValues() {
   //   insertMaserData("serverUrl", "http://192.168.2.165:9292/apk");// saranya
//       insertMaserData("serverUrl", "http://192.168.2.165:9292/apk");// saranya
      //---->  insertMaserData("serverUrl", "http://192.168.1.64:9292/apk");// jayasri
     //   insertMaserData("serverUrl", "http://192.168.1.110:9241/apk");
        insertMaserData("serverUrl", "http://192.168.1.110:9241/apk");

      //   insertMaserData("serverUrl", "http://192.168.1.110:9241/apk");
        // insertMaserData("serverUrl", "http://52.77.146.57:9401/apk");
        //192.1682.165:9292
       //  insertMaserData("serverUrl", "http://52.77.146.57:9401/apk");//         insertMaserData("serverUrl", "http://192.168.2.94:9292/apk");
    //    insertMaserData("serverUrl", "http://192.168.1.110:9241/apk");  //vaithi
//        insertMaserData("serverUrl", "http://192.168.1.109:9251/apk");  //munish
//        insertMaserData("serverUrl", "http://52.77.146.57:9401/apk");  //cloud
        insertMaserData("purgeBill", "30");
        insertMaserData("syncTime", null);
        insertMaserData("status", null);
        insertMaserData("printer", null);
        insertMaserData("language", "ta");
        insertMaserData("autoNumber", "0");
        insertMaserData("currentDate", getCurrentDate());


    }

    private void insertMaserData(String name, String value) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("value", value);
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
        database.insertWithOnConflict(DBTables.TABLE_CONFIG_TABLE, "name", values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    //This funciton returns current Date
    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;

    }

    //get MasterData
    public String getMasterData(String key) {
        String selectQuery = "SELECT  * FROM " + DBTables.TABLE_CONFIG_TABLE + " where name = '" + key + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String value = null;
        try {
            if (cursor.moveToFirst()) {
                value = cursor.getString(cursor.getColumnIndex("value"));
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
        cursor.close();
        return value;
    }

    //update MasterData
    public void updateMaserData(String name, String value) {
        ContentValues values = new ContentValues();
        try {
            values.put("name", name);
            values.put("value", value);
            database.update(DBTables.TABLE_CONFIG_TABLE, values, "name='" + name + "'", null);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString(), e);
        }
    }

    //This function loads data to language table
    public boolean getCheck() {
        String selectQuery = "SELECT  * FROM " + DBTables.TABLE_CONFIG_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean value = cursor.getCount() == 0;
        cursor.close();
        return value;

    }


    public int getCustomerId() {
        int cusotmer_id = 1;
        try {
            String selectQuery = "select * from users";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            cusotmer_id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);

        }
        return cusotmer_id;
    }

}
