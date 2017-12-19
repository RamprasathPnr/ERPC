package com.omneagate.erbc.Util;

import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.Dto.EnumDto.ApplianceDetailsDto;

import java.util.ArrayList;
import java.util.List;

/**
 * FPS database helper constant  variables.
 */
public class DBConstants {

    // local url
//    public static final String ERBC_SERVER_URL = "http://192.168.1.24:8089";
    public static final String ERBC_SERVER_URL = "http://192.168.2.165:9292/";
    public static final String RESPONSE_DATA = "RESPONSE_DATA";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String KEY_MIDDLE_NAME = "middlename";
    public static final String KEY_EMAIL_NAME = "email";
    public static final String KEY_ADDRESS1 = "address1";
    public static final String KEY_ADDRESS2 = "address2";
    public static final String KEY_DOB = "dob";
    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_PINCODE = "pincode";
    public static final String KEY_STATUS = "status";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_MSTATUS = "maritalStatus";
    public static final String KEY_OCCUAPTION = "occupation";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_STATE = "state";
    public static final String KEY_DISTRICT = "district";
    public static final String KEY_TALUK = "taluk";
    public static final String KEY_VILLAGE = "village";

    public static final String KEY_MSTATUS_ID = "maritalStatus_id";
    public static final String KEY_OCCUAPTION_ID = "occupation_id";
    public static final String KEY_COUNTRY_ID = "country_id";
    public static final String KEY_STATE_ID = "state_id";
    public static final String KEY_DISTRICT_ID = "district_id";
    public static final String KEY_TALUK_ID = "taluk_id";
    public static final String KEY_VILLAGE_ID = "village_id";

    // Intent constants
    public static final String INTENT_URL = "intent_url";
    public static final String INTENT_TITLE = "intent_title";

    public static final String ABOUT_URL = "intent_title";
    public static final String TREAMS_URL = "intent_title";



    public static ArrayList<ApplianceDto> applianceDetailsDtoArrayList = new ArrayList<ApplianceDto>();

    public static final List<ApplianceDto> aNewApplianceQuantityAryList = new ArrayList<>();
}
//applianceDetailsDtoArrayList aNewApplianceQuantityAryList