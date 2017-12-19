package com.omneagate.erbc.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.BackPressedDialog;
import com.omneagate.erbc.Activity.Dialog.ImageRemoveDialog;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.ConnectionTypeDto;
import com.omneagate.erbc.Dto.ConsumerTypeDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DiscomDto;
import com.omneagate.erbc.Dto.DistrictDto;
import com.omneagate.erbc.Dto.EnumDto.ConnectionUserType;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.MeterBrandDto;
import com.omneagate.erbc.Dto.MeterTypeDto;
import com.omneagate.erbc.Dto.NewConnectionDto;
import com.omneagate.erbc.Dto.PhaseDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.StateDto;
import com.omneagate.erbc.Dto.TalukDto;
import com.omneagate.erbc.Dto.VillageDto;
import com.omneagate.erbc.Dto.countryDto;
import com.omneagate.erbc.Dto.personalDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.DateUtil;
import com.omneagate.erbc.Util.GPSService;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by user1 on 5/7/16.
 */

public class ConnectionRegisterationActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout connectionlay, consumerLay, meterLay, connection_view, consuner_view, meter_view;
    TextView connectionHeaderTxt, consumerHeaderTxt, MeterTxt;
    DatePickerDialog datePickerDialog;
    private static final int CAMERA_REQUEST_BILL = 2;
    private static final int CAMERA_REQUEST_CARD = 4;
    private static final int CAMERA_REQUEST_METER = 5;
    MaterialBetterSpinner phase, connection_type, consumer_type, serviceType;
    List<ConnectionTypeDto> connectionList;
    List<ConsumerTypeDto> consumerList;
    List<PhaseDto> phaseList;
    final Calendar newCalendar = Calendar.getInstance();
    int phase_id, connection_id, consumer_id;
    String phaseStr, connectionStr, consumerStr;
    String eb_card_photo, eb_bill_photo, ebmeter_photo;
    ImageView uploadebcard, uploadebbill, uploadebMeter;
    TextInputLayout consumer_name_lay, address1Lay, address2Lay, pincodeLay;
    EditText consumer_name, address1, address2, pincode;
    MaterialBetterSpinner village, taluk, district;
    ConnectionDto consumerInfo = new ConnectionDto();
    List<DistrictDto> districtList;
    List<TalukDto> talukList;
    List<VillageDto> villageList;
    String districtName, talukName, villageName;
    int district_id, taluk_id, village_id;
    ArrayAdapter<String> villageAdapter, talukAdapter, districtAdapter;
    ArrayList<String> districtArray = new ArrayList<String>();
    ArrayList<String> talukListArray = new ArrayList<String>();
    ArrayList<String> villageListArray = new ArrayList<String>();
    ArrayList<String> consumerArray = new ArrayList<String>();
    ArrayList<String> phaseArray = new ArrayList<String>();
    Button nextBtn, nextBtn_Consumer, submitbtn;
    ImageView layuploadbill, layuploadcard, latuploadmeter, delete_cardImg, delete_billImg, delete_meterImg;
    List<MeterBrandDto> meterBrandList;
    List<MeterTypeDto> meterTypeList;
    MaterialBetterSpinner meterBrand, meterType;
    CustomProgressDialog progressBar;
    EditText lastreadDate, fromDate, toDate, serialnumber, lastmeter_value;
    TextInputLayout serialnumberLay, fromdateLay, todateLay, lastreadingLay, lastmeterLay;
    String metertypeStr, meterbrandStr;
    int metertype_id, meterbrand_id, last_date, last_month, last_year, from_date, from_month, from_year;
    static final int DATE_DIALOG_ID = 999;
    ImageView uploadMeterimage;
    String languageCode = GlobalAppState.language;
    DiscomDto discomDto;
    String months[] = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String TAG = ConnectionRegisterationActivity.class.getName();
    private String addressBlockCharacterSet = "@#$%&+=()*\":;!?";
    String sDate;

    Uri imageUri = null;
    private String fileNameBill, fileNameCard, fileNameMeter;
    private String folder_name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/";
    Bitmap bitpmap_bill, bitpmap_card, bitpmap_meter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionregisteration);
        uploadebMeter = (ImageView) findViewById(R.id.noimage5);
        uploadebMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(R.layout.image_confirmation, bitpmap_meter,"Meter");
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        String regionId = intent.getStringExtra("regionId");
        String consumer_number = intent.getStringExtra("consumer_number");
        String discomId = intent.getStringExtra("discomId");
        String stateId = intent.getStringExtra("stateId");
        String countryId = intent.getStringExtra("countryId");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        discomDto = gson.fromJson(discomId, DiscomDto.class);
        StateDto stateDto = gson.fromJson(stateId, StateDto.class);
        countryDto countryDto = gson.fromJson(countryId, countryDto.class);
        CustomerDto customerdto = new CustomerDto();
        customerdto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
        consumerInfo.setCustomer(customerdto);
        consumerInfo.setCountry(countryDto);
        consumerInfo.setState(stateDto);
        consumerInfo.setCustomer(customerdto);
        consumerInfo.setConsumerNumber(consumer_number);
        consumerInfo.setRegionCode(regionId);
        consumerInfo.setDiscom(discomDto);
        Log.e("dicomm", "" + discomDto.toString());
        configureInitialPage();
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();
        connection_Info();
        meter_Info();
        stateClick(stateDto);


//        newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        last_date = newCalendar.get(Calendar.DAY_OF_MONTH);
        last_month = newCalendar.get(Calendar.MONTH);
        last_year = newCalendar.get(Calendar.YEAR);

        from_date = newCalendar.get(Calendar.DAY_OF_MONTH);
        from_month = newCalendar.get(Calendar.MONTH);
        from_year = newCalendar.get(Calendar.YEAR);

        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                if (pincode.getText().toString().length() == 6) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pincode.getWindowToken(), 0);
                }
            }
        });

        phase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    phaseStr = phase.getText().toString();
                    phase_id = phaseList.get(position).getId();
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });
        connection_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    connectionStr = connection_type.getText().toString();
                    connection_id = connectionList.get(position).getId();
                    consumer_type.setText("");
                    phase.setText("");
                    getConsumer_type(connection_id);
                    consumerArray.clear();
                    phaseArray.clear();
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });
        consumer_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    consumerStr = consumer_type.getText().toString();
                    consumer_id = consumerList.get(position).getId();
                    phase.setText("");
                    getphase_type(consumer_id);

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });

        district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    districtName = district.getText().toString();
                    district_id = districtList.get(position).getId();
                    DistrictDto districtdata = new DistrictDto();
                    districtdata.setId(districtList.get(position).getId());
                    districtdata.setName(district.getText().toString());
                    taluk.setText("");
                    village.setText("");
                    taluk.setFocusable(true);
                    districtClick(districtdata);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });

        taluk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                talukName = taluk.getText().toString();
                taluk_id = talukList.get(position).getId();
                TalukDto talukdata = new TalukDto();
                talukdata.setId(talukList.get(position).getId());
                talukdata.setName(taluk.getText().toString());
                village.setText("");
                village.setFocusable(true);
                talukClick(talukdata);
            }
        });

        village.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                villageName = village.getText().toString();
                village_id = villageList.get(position).getId();
            }
        });


        meterType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    metertypeStr = meterType.getText().toString();
                    metertype_id = meterTypeList.get(position).getId();
                    MeterTypeDto metertype = new MeterTypeDto();
                    metertype.setId(metertype_id);
                    metertype.setName(metertypeStr);
                    Log.e("meterTYpe", "" + metertype.toString());
                    consumerInfo.setMeterType(metertype);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });
        meterBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    meterbrandStr = meterBrand.getText().toString();
                    meterbrand_id = meterBrandList.get(position).getId();
                    MeterBrandDto meterbrand = new MeterBrandDto();
                    meterbrand.setId(meterbrand_id);
                    meterbrand.setName(meterbrandStr);
                    Log.e("meterbrand", "" + meterbrand.toString());
                    consumerInfo.setMeterBrand(meterbrand);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });

        /*address1.setFilters(new InputFilter[]{Util.inputFilter(addressBlockCharacterSet)});
        address2.setFilters(new InputFilter[]{Util.inputFilter(addressBlockCharacterSet)});*/
        address2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(75)});
        address1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(75)});
    }


    private void getConsumer_type(int connectiontype) {

        {
            try {
                networkConnection = new NetworkConnection(getBaseContext());
                if (networkConnection.isNetworkAvailable()) {
                    JSONObject requestObject = new JSONObject();
                    requestObject.put("id", "" + connectiontype);
                    String url = "/consumertype/getbyconnectiontype";
                    String login = new Gson().toJson(requestObject);
                    StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                    httpConnection.sendRequest(url, null, ServiceListenerType.CONSUMER_TYPE, SyncHandler, RequestType.POST, se, getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    private void getphase_type(int consumertype) {

        {
            try {
                networkConnection = new NetworkConnection(getBaseContext());
                if (networkConnection.isNetworkAvailable()) {
                    JSONObject requestObject = new JSONObject();
                    requestObject.put("id", "" + consumertype);
                    String url = "/phase/getbyconsumertype";
                    String login = new Gson().toJson(requestObject);
                    StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                    httpConnection.sendRequest(url, null, ServiceListenerType.PHASE_TYPE, SyncHandler, RequestType.POST, se, getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e(TAG, e.toString(), e);
            }
        }
    }


    private void stateClick(StateDto states) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/district/getallbystate";
                String login = new Gson().toJson(states);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_DISTRICT, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void stateResponse(Bundle message) {
        try {

            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto districtResponse = gson.fromJson(response, personalDto.class);
            if (districtResponse != null) {
                districtList = districtResponse.getDistricts();
            }
            districtArray.clear();
            Log.e("districtlist", "" + districtResponse.getDistricts().size());
            if (districtResponse.getDistricts().size() == 0) {
                district.setError("Districts not found.");
                // Toast.makeText(getActivity(),"Districts not found.",Toast.LENGTH_LONG).show();
            }
            for (int i = 0; i < districtList.size(); i++) {

                if (languageCode.equalsIgnoreCase("ta")) {
                    districtArray.add(districtList.get(i).getRegionalName());
                } else {
                    districtArray.add(districtList.get(i).getName());
                }

            }
            districtAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, districtArray);
            district.setAdapter(districtAdapter);

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString());
        }


    }

    private void districtClick(DistrictDto district) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/taluk/getallbydistrict";
                String login = new Gson().toJson(district);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_TALUK, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void districtResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto districtResponse = gson.fromJson(response, personalDto.class);
            if (districtResponse != null) {
                talukList = districtResponse.getTaluks();
            }
            talukListArray.clear();
            for (int i = 0; i < talukList.size(); i++) {
                if (languageCode.equalsIgnoreCase("ta")) {
                    talukListArray.add(talukList.get(i).getRegionalName());
                } else {
                    talukListArray.add(talukList.get(i).getName());
                }

            }
            talukAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, talukListArray);
            taluk.setAdapter(talukAdapter);

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString());
        }


    }

    private void talukClick(TalukDto talukdto) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/village/getallbytaluk";
                String login = new Gson().toJson(talukdto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_VILLAGE, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private void talukResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto villageResponse = gson.fromJson(response, personalDto.class);
            if (villageResponse != null) {
                villageList = villageResponse.getVillages();
            }
            villageListArray.clear();
            for (int i = 0; i < villageList.size(); i++) {
                if (languageCode.equalsIgnoreCase("ta")) {
                    villageListArray.add(villageList.get(i).getRegionalName());
                } else {
                    villageListArray.add(villageList.get(i).getName());
                }

            }
            villageAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, villageListArray);
            village.setAdapter(villageAdapter);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    private boolean validateConsumerName() {
        if (consumer_name.getText().toString().trim().isEmpty()) {
            consumer_header();
            consumer_name_lay.setError(getString(R.string.consumername_err));
            requestFocus(consumer_name);
            return false;
        } else {
            consumer_name_lay.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateAddress1() {
        if (address1.getText().toString().trim().isEmpty()) {
            consumer_header();
            address1Lay.setError(getString(R.string.address1_err));
            requestFocus(address1);
            return false;
        } else {
            address1Lay.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAddress2() {
        if (address2.getText().toString().trim().isEmpty()) {
            consumer_header();
            address2Lay.setError(getString(R.string.address2_err));
            requestFocus(address2);
            return false;
        } else {
            address2Lay.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePincode() {
        if (pincode.getText().toString().trim().isEmpty()) {
            pincodeLay.setError(getString(R.string.pincode_err));
            consumer_header();
            requestFocus(pincode);
            return false;
        } else if (pincode.length() < 6) {
            consumer_header();
            pincodeLay.setError(getString(R.string.pinvalid_err));
            requestFocus(pincode);
            return false;
        } else if (pincode.getText().toString().trim().equalsIgnoreCase("000000")) {
            pincodeLay.setError(getString(R.string.pinvalid_err));
            requestFocus(pincode);
            return false;
        } else {
            pincodeLay.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void configureInitialPage() {

        try {
            nextBtn = (Button) findViewById(R.id.button);
            uploadebcard = (ImageView) findViewById(R.id.noimage1);
            uploadebbill = (ImageView) findViewById(R.id.noimage2);
            uploadebMeter = (ImageView) findViewById(R.id.noimage5);
            phase = (MaterialBetterSpinner) findViewById(R.id.phase);
            connection_type = (MaterialBetterSpinner) findViewById(R.id.connectiontype);
            serviceType = (MaterialBetterSpinner) findViewById(R.id.select_serviceType_spinner);
            String[] SERVICE_TYPE_LIST = {getResources().getString(R.string.select_service_value), getResources().getString(R.string.select_service_value1)};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.dropdownrow, SERVICE_TYPE_LIST);
            serviceType.setAdapter(arrayAdapter);


            consumer_type = (MaterialBetterSpinner) findViewById(R.id.consumer_type);
            layuploadbill = (ImageView) findViewById(R.id.upload_bill);
            layuploadcard = (ImageView) findViewById(R.id.upload_card);
            latuploadmeter = (ImageView) findViewById(R.id.upload_ebmeter);
            delete_cardImg = (ImageView) findViewById(R.id.capture_card);
            delete_billImg = (ImageView) findViewById(R.id.capture_bill);
            delete_meterImg = (ImageView) findViewById(R.id.capture_ebmeter);
            nextBtn = (Button) findViewById(R.id.nextBtn);
            nextBtn_Consumer = (Button) findViewById(R.id.button);
            submitbtn = (Button) findViewById(R.id.submitBtn);
            consumer_name_lay = (TextInputLayout) findViewById(R.id.consumerwid);
            address1Lay = (TextInputLayout) findViewById(R.id.layout_address1);
            address2Lay = (TextInputLayout) findViewById(R.id.layout_address2);

            consumer_name = (EditText) findViewById(R.id.cousumer_name);
            consumer_name.requestFocus();
            serviceType.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(consumer_name.getWindowToken(), 0);
                    return false;
                }
            });
            address1 = (EditText) findViewById(R.id.address1);
            address2 = (EditText) findViewById(R.id.address2);
            village = (MaterialBetterSpinner) findViewById(R.id.village);
            taluk = (MaterialBetterSpinner) findViewById(R.id.taluk);
            district = (MaterialBetterSpinner) findViewById(R.id.district);

            meterType = (MaterialBetterSpinner) findViewById(R.id.metertype);
            meterBrand = (MaterialBetterSpinner) findViewById(R.id.meterbrand);
            serialnumber = (EditText) findViewById(R.id.serialnumber);
            serialnumber.requestFocus();
            lastmeter_value = (EditText) findViewById(R.id.meterreading);
            serialnumberLay = (TextInputLayout) findViewById(R.id.layout_serial);
            lastreadingLay = (TextInputLayout) findViewById(R.id.layout_reading);
            fromdateLay = (TextInputLayout) findViewById(R.id.layout_from);
            todateLay = (TextInputLayout) findViewById(R.id.layout_to);
            lastmeterLay = (TextInputLayout) findViewById(R.id.layoutmeterreadingLay);

            connectionHeaderTxt = (TextView) findViewById(R.id.connectiontxt);
            consumerHeaderTxt = (TextView) findViewById(R.id.consumertxt);
            MeterTxt = (TextView) findViewById(R.id.metertxt);
            connectionlay = (LinearLayout) findViewById(R.id.connection_header);
            consumerLay = (LinearLayout) findViewById(R.id.consumer_header);
            meterLay = (LinearLayout) findViewById(R.id.meter_header);
            connection_view = (LinearLayout) findViewById(R.id.connectionlay);
            consuner_view = (LinearLayout) findViewById(R.id.customerlay);
            meter_view = (LinearLayout) findViewById(R.id.meterlay);
            lastreadDate = (EditText) findViewById(R.id.dates);
            fromDate = (EditText) findViewById(R.id.from_date);
            toDate = (EditText) findViewById(R.id.to_date);
            lastreadDate.setShowSoftInputOnFocus(false);
            fromDate.setShowSoftInputOnFocus(false);
            toDate.setShowSoftInputOnFocus(false);
            lastreadDate.setOnClickListener(showDatePicker);
            fromDate.setOnClickListener(showDatePicker_);
            connectionlay.setOnClickListener(this);
            consumerLay.setOnClickListener(this);
            meterLay.setOnClickListener(this);
            pincodeLay = (TextInputLayout) findViewById(R.id.layout_pincode);
            pincode = (EditText) findViewById(R.id.pincode);


            latuploadmeter.setOnClickListener(this);
            layuploadbill.setOnClickListener(this);
            layuploadcard.setOnClickListener(this);
            nextBtn.setOnClickListener(this);
            nextBtn_Consumer.setOnClickListener(this);
            uploadebcard.setOnClickListener(this);
            uploadebbill.setOnClickListener(this);
            submitbtn.setOnClickListener(this);
            delete_cardImg.setOnClickListener(this);
            delete_billImg.setOnClickListener(this);
            delete_meterImg.setOnClickListener(this);
            meterType.setOnClickListener(this);
            district.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.consumer_header:
                consumer_header();
                break;

            case R.id.connection_header:
                connection_header();
                break;

            case R.id.meter_header:
                meter_header();
                break;

            // For sathiya re work to hide done by shanthakumar on 01-08-2016
            /*case R.id.upload_bill:
                Intent cameraClick = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraClick, CAMERA_REQUEST_BILL);
                break;
            case R.id.upload_card:
                Intent cameraClick1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraClick1, CAMERA_REQUEST_CARD);
                break;
            case R.id.upload_ebmeter:
                Intent cameraClick3 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraClick3, CAMERA_REQUEST_METER);
                break;*/

            case R.id.upload_bill:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.

                    requestCameraPermission(CAMERA_REQUEST_BILL);


                } else {

                    takePicture(CAMERA_REQUEST_BILL);

                }

//                Intent cameraClick = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraClick, CAMERA_REQUEST_BILL);
                break;
            case R.id.upload_card:

                hideKeyBoard();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.

                    requestCameraPermission(CAMERA_REQUEST_CARD);


                } else {

                    takePicture(CAMERA_REQUEST_CARD);

                }

//                Intent cameraClick1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraClick1, CAMERA_REQUEST_CARD);

                break;
            case R.id.upload_ebmeter:
                hideKeyBoard();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Camera permission has not been granted.
                    requestCameraPermission(CAMERA_REQUEST_METER);

                } else {

                    takePicture(CAMERA_REQUEST_METER);

                }

//                Intent cameraClick3 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraClick3, CAMERA_REQUEST_METER);
                break;
            case R.id.capture_card:
                /*eb_card_photo = "";
                ((ImageView) findViewById(R.id.capture_card)).setVisibility(View.INVISIBLE);
                ((ImageView) findViewById(R.id.noimage1)).setVisibility(View.INVISIBLE);*/
                ImageRemoveDialog cardDialog = new ImageRemoveDialog(ConnectionRegisterationActivity.this,
                        getString(R.string.remove_image), 1);
                cardDialog.show();
                hideKeyBoard();
                break;
            case R.id.capture_bill:
                ImageRemoveDialog billDialog = new ImageRemoveDialog(ConnectionRegisterationActivity.this,
                        getString(R.string.remove_image), 2);
                billDialog.show();
                hideKeyBoard();
                /*eb_bill_photo = "";
                ((ImageView) findViewById(R.id.capture_bill)).setVisibility(View.INVISIBLE);
                ((ImageView) findViewById(R.id.noimage2)).setVisibility(View.INVISIBLE);*/
                break;

            case R.id.capture_ebmeter:

                ImageRemoveDialog meterDialog = new ImageRemoveDialog(ConnectionRegisterationActivity.this,
                        getString(R.string.remove_image), 3);
                meterDialog.show();
                hideKeyBoard();
                /*ebmeter_photo = "";
                ((ImageView) findViewById(R.id.capture_ebmeter)).setVisibility(View.INVISIBLE);
                ((ImageView) findViewById(R.id.noimage5)).setVisibility(View.INVISIBLE);*/
                break;
            case R.id.nextBtn:
                nextBtnClick();
                break;
            case R.id.button:
                nextbtnConsumer();
                break;
            case R.id.noimage1:
                //  previewImageDialog(uploadebcard);
                dialog(R.layout.image_confirmation, bitpmap_card,"Card");
                break;
            case R.id.noimage2:
                dialog(R.layout.image_confirmation, bitpmap_bill,"Bill");
                //  previewImageDialog(uploadebbill);
                break;
            case R.id.noimage5:
                dialog(R.layout.image_confirmation, bitpmap_meter,"Meter");
                // previewImageDialog(uploadebMeter);
                break;
            case R.id.submitBtn:
                completeBtnClick();
                break;

            case R.id.layout_reading:
                hideKeyBoard();
                break;

            case R.id.metertype:
                hideKeyBoard();
                break;

            case R.id.district:
                hideKeyBoard();
                break;

        }
    }

    public void hideCaptureCard() {
        eb_card_photo = "";
        ((ImageView) findViewById(R.id.capture_card)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.noimage1)).setVisibility(View.INVISIBLE);
    }

    public void hideCaptureBill() {

        eb_bill_photo = "";
        ((ImageView) findViewById(R.id.capture_bill)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.noimage2)).setVisibility(View.INVISIBLE);
    }

    public void hideCaptureMeter() {
        ebmeter_photo = "";
        ((ImageView) findViewById(R.id.capture_ebmeter)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.noimage5)).setVisibility(View.INVISIBLE);
    }

    private void hideKeyBoard() {

        Util.hideKeyBoard(ConnectionRegisterationActivity.this,
                ConnectionRegisterationActivity.this.getCurrentFocus());
    }

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission(int REQUEST_CAMERA) {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CAMERA);
    }

    /**
     * Method to launch camera after permission accepted from user
     */
   /* void takePicture(int selected) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, selected);
    }*/

    void takePicture(int selected) {

        File outputFile;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/");

        if (selected == CAMERA_REQUEST_BILL) {
            fileNameBill = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            outputFile = new File(file.toString(), fileNameBill);
        } else if (selected == CAMERA_REQUEST_CARD) {
            fileNameCard = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            outputFile = new File(file.toString(), fileNameCard);
        } else {
            fileNameMeter = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            outputFile = new File(file.toString(), fileNameMeter);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (!file.exists()) {
            file.mkdirs();
        } else System.out.println("Sham Enter the File Else  method :::");

        imageUri = Uri.fromFile(outputFile);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, selected);
      /*  Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, selected);*/
    }


    public View dialog(int cus_layout, Bitmap bm, final String type) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View layout = inflater.inflate(cus_layout,
                null);

        ImageView img = (ImageView) layout.findViewById(R.id.imageView10);
//        bm = Bitmap.createScaledBitmap(bm, 300, 500, true);
        bm = Bitmap.createBitmap(bm);
        img.setImageBitmap(bm);
        builder.setView(layout);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button mLlCancel = (Button) layout.findViewById(R.id.cancel);
        mLlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
        Button mLlSelect = (Button) layout.findViewById(R.id.delete);

        mLlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equalsIgnoreCase("Meter")){
                    uploadebMeter.setImageBitmap(null);
                    ((ImageView) findViewById(R.id.capture_ebmeter)).setVisibility(View.GONE);

                }else if(type.equalsIgnoreCase("Bill")){
                    uploadebbill.setImageBitmap(null);
                    ((ImageView) findViewById(R.id.capture_bill)).setVisibility(View.GONE);

                }else{
                    uploadebcard.setImageBitmap(null);
                    ((ImageView) findViewById(R.id.capture_card)).setVisibility(View.GONE);

                }

                alertDialog.dismiss();

            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();

        return layout;
    }

    private Bitmap getbitmap(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(folder_name + filename, options);
    }

    public void clearTempFile() {
        try {
            File dir = new File(folder_name);
            if (dir != null && dir.listFiles() != null) {
                for (File tempFile : dir.listFiles()) {
                    tempFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void consumer_header() {
        consumerHeaderTxt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
        consumerHeaderTxt.setTextColor(getResources().getColor(R.color.black));
        consuner_view.setVisibility(View.VISIBLE);
        connectionHeaderTxt.setBackgroundColor(getResources().getColor(R.color.white));
        connectionHeaderTxt.setTextColor(getResources().getColor(R.color.gray_2));
        MeterTxt.setBackgroundColor(getResources().getColor(R.color.white));
        MeterTxt.setTextColor(getResources().getColor(R.color.gray_2));
        meter_view.setVisibility(View.GONE);
        connection_view.setVisibility(View.GONE);
    }


    private void connection_header() {
        connectionHeaderTxt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
        connectionHeaderTxt.setTextColor(getResources().getColor(R.color.black));
        connection_view.setVisibility(View.VISIBLE);
        consumerHeaderTxt.setBackgroundColor(getResources().getColor(R.color.white));
        consumerHeaderTxt.setTextColor(getResources().getColor(R.color.gray_2));
        MeterTxt.setBackgroundColor(getResources().getColor(R.color.white));
        MeterTxt.setTextColor(getResources().getColor(R.color.gray_2));
        meter_view.setVisibility(View.GONE);
        consuner_view.setVisibility(View.GONE);
    }


    private void meter_header() {
        MeterTxt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
        MeterTxt.setTextColor(getResources().getColor(R.color.black));
        meter_view.setVisibility(View.VISIBLE);
        consumerHeaderTxt.setBackgroundColor(getResources().getColor(R.color.white));
        consumerHeaderTxt.setTextColor(getResources().getColor(R.color.gray_2));
        connectionHeaderTxt.setBackgroundColor(getResources().getColor(R.color.white));
        connectionHeaderTxt.setTextColor(getResources().getColor(R.color.gray_2));
        connection_view.setVisibility(View.GONE);
        consuner_view.setVisibility(View.GONE);
    }

   /* private void previewImageDialog(ImageView upload) {
        try {
            Log.e("testing", "testing");
            final MaterialDialog mMaterialDialog = new MaterialDialog(ConnectionRegisterationActivity.this);
            mMaterialDialog.setCanceledOnTouchOutside(true);
            mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                    hideKeyBoard();
                }
            });
            Bitmap bitmap = ((BitmapDrawable) upload.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 10), (int) (bitmap.getHeight() * 10), true);
            Drawable drawe = new BitmapDrawable(getResources(), resized);
            mMaterialDialog.setBackground(drawe);
            mMaterialDialog.show();


        } catch (Exception e) {
            Log.e(TAG, "" + e.toString(), e);
        }
    }*/

    private void nextBtnClick() {

        String phase_str = phase.getText().toString();
        String connection_str = connection_type.getText().toString();
        String consumer_str = consumer_type.getText().toString();


        if (StringUtils.isEmpty(connection_str)) {
            connection_header();
            connection_type.setError(getString(R.string.connection_err));
            return;
        }
        if (StringUtils.isEmpty(consumer_str)) {
            connection_header();
            consumer_type.setError(getString(R.string.consumer_err));
            return;
        }

        if (StringUtils.isEmpty(phase_str)) {
            connection_header();
            phase.setError(getString(R.string.phase_err));
            return;
        }

        if (StringUtils.isEmpty(eb_card_photo)) {
            connection_header();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_EBCard_photo_error), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), "Upload Eb Card Photo is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isEmpty(eb_bill_photo)) {
            connection_header();
//            Toast.makeText(getApplicationContext(), "Upload Bill Photo is required", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_Bill_photo_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ConnectionDto customer_details = getConnectionDetails();
        consumer_header();
        meter_header();

    }


    private ConnectionDto getConnectionDetails() {
        try {


            PhaseDto phasedata = new PhaseDto();
            phasedata.setId(phase_id);
            phasedata.setName(phaseStr);
            consumerInfo.setPhase(phasedata);
            ConnectionTypeDto connectiontype = new ConnectionTypeDto();
            connectiontype.setId(connection_id);
            connectiontype.setName(connectionStr);
            consumerInfo.setConnectionType(connectiontype);
            ConsumerTypeDto consumertype = new ConsumerTypeDto();
            consumertype.setId(consumer_id);
            consumertype.setName(consumerStr);
            consumerInfo.setConsumerType(consumertype);
            consumerInfo.setEbBillImage(eb_bill_photo);
            consumerInfo.setEbCardImage(eb_card_photo);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return consumerInfo;
    }

    private void connection_Info() {
        try {
            networkConnection = new NetworkConnection(getApplicationContext());
            if (networkConnection.isNetworkAvailable()) {
                JSONObject requestObject = new JSONObject();
                requestObject.put("id", "" + discomDto.getId());
                String url = "/connectiontype/getallbydiscom";
                String login = new Gson().toJson(requestObject);
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_INFO, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestCode", "" + requestCode);

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        hideKeyBoard();
        try {
            switch (requestCode) {
                case 2:
                    //  bitpmap_bill = (Bitmap) data.getExtras().get("data");
                    bitpmap_bill = getbitmap(fileNameBill);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitpmap_bill.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                    byte[] byteArray = stream.toByteArray();
                    eb_bill_photo = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    ((ImageView) findViewById(R.id.capture_bill)).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.noimage2)).setVisibility(View.VISIBLE);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitpmap_bill, 100, 150, true);
                   /* ExifInterface exif = new ExifInterface(String.valueOf(scaled));     //Since API Level 5
                    String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);*/
                    ((ImageView) findViewById(R.id.noimage2)).setImageBitmap(scaled);
                    hideKeyBoard();
                    break;

                case 4:
                    hideKeyBoard();
                    //  Bitmap bitpmap_card = (Bitmap) data.getExtras().get("data");
                    bitpmap_card = getbitmap(fileNameCard);
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitpmap_card.compress(Bitmap.CompressFormat.JPEG, 25, stream1);
                    byte[] byteArray1 = stream1.toByteArray();
                    eb_card_photo = Base64.encodeToString(byteArray1, Base64.DEFAULT);
                    ((ImageView) findViewById(R.id.capture_card)).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.noimage1)).setVisibility(View.VISIBLE);
                    Bitmap scaled1 = Bitmap.createScaledBitmap(bitpmap_card, 100, 150, true);
                    ((ImageView) findViewById(R.id.noimage1)).setImageBitmap(scaled1);

                    break;

                case 5:
                    hideKeyBoard_();
                    //bitpmap_meter = (Bitmap) data.getExtras().get("data");
                    bitpmap_meter = getbitmap(fileNameMeter);
                    ByteArrayOutputStream stream_meter = new ByteArrayOutputStream();
                    bitpmap_meter.compress(Bitmap.CompressFormat.JPEG, 25, stream_meter);
                    byte[] byteArray_meter = stream_meter.toByteArray();
                    ebmeter_photo = Base64.encodeToString(byteArray_meter, Base64.DEFAULT);
                    ((ImageView) findViewById(R.id.capture_ebmeter)).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.noimage5)).setVisibility(View.VISIBLE);
                    Bitmap scaled_metr = Bitmap.createScaledBitmap(bitpmap_meter, 100, 150, true);
                    ((ImageView) findViewById(R.id.noimage5)).setImageBitmap(scaled_metr);

                    break;
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void hideKeyBoard_() {

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(serialnumber.getWindowToken(), 0);
        InputMethodManager imm1 = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.hideSoftInputFromWindow(lastmeterLay.getWindowToken(), 0);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case CONNECTION_INFO:
                getConnection_responseData(message);
                dismissProgress();
                break;
            case REGISTER_USER:
                dismissProgress();
                break;
            case GET_DISTRICT:
                dismissProgress();
                stateResponse(message);
                break;
            case GET_TALUK:
                dismissProgress();
                districtResponse(message);
                break;
            case GET_VILLAGE:
                dismissProgress();
                talukResponse(message);
                break;
            case METER_INFO:
                getMeter_responseData(message);
                dismissProgress();
                break;

            case REG_CONNECTION:
                getconnenctionResponse(message);
                dismissProgress();
                break;

            case CONSUMER_TYPE:
                getconsumerTypeResponse(message);
                dismissProgress();
                break;

            case PHASE_TYPE:
                getPhaseResponse(message);
                dismissProgress();
                break;


            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void getConnection_responseData(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            NewConnectionDto connectionResponse;
            connectionResponse = gson.fromJson(response, NewConnectionDto.class);
            if (connectionResponse != null) {
                if (connectionResponse.getStatusCode() == 0) {
                    connectionList = connectionResponse.getConnectionType();
                    ArrayList<String> connectionArray = new ArrayList<String>();
                    for (int i = 0; i < connectionList.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            connectionArray.add(connectionList.get(i).getRegionalName());
                        } else {
                            connectionArray.add(connectionList.get(i).getName());
                        }
                    }
                    ArrayAdapter<String> connectionTypeAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, connectionArray);
                    connection_type.setAdapter(connectionTypeAdpter);
                    connection_type.setFocusable(true);
                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.no_connection_err), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private ConnectionDto getConsumerDetails() {

        String consumernameStr = consumer_name.getText().toString();
        String address1_str = address1.getText().toString();
        String address2_str = address2.getText().toString();
        String pincode_str = pincode.getText().toString();
        String connection_user_Type_str = serviceType.getText().toString();
        consumerInfo.setConsumerName(consumernameStr);
        consumerInfo.setAddressLine1(address1_str);
        consumerInfo.setAddressLine2(address2_str);
        if (connection_user_Type_str.equalsIgnoreCase("OWNER")) {
            consumerInfo.setConnectionUserType(ConnectionUserType.OWNER);
        } else {
            consumerInfo.setConnectionUserType(ConnectionUserType.TENANT);
        }
//        consumerInfo.setConnectionUserType(connection_user_Type_str);
        TalukDto taluk = new TalukDto();
        taluk.setId(taluk_id);
        taluk.setName(talukName);
        consumerInfo.setTaluk(taluk);
        DistrictDto district = new DistrictDto();
        district.setId(district_id);
        district.setName(districtName);
        consumerInfo.setDistrict(district);
        VillageDto village = new VillageDto();
        village.setId(village_id);
        village.setName(villageName);
        consumerInfo.setVillage(village);
        consumerInfo.setPinCode(pincode_str);
        Log.e("consumerInfo", "" + consumerInfo.toString());
        return consumerInfo;
    }


    private void nextbtnConsumer() {
        try {
            if (!validateConsumerName()) {
                return;
            }
            String connectionUserType = serviceType.getText().toString();
            if (StringUtils.isEmpty(connectionUserType)) {
                consumer_header();
                serviceType.setError(getString(R.string.conn_user_category));
                return;
            }
            if (!validateAddress1()) {
                return;
            }
            if (!validateAddress2()) {
                return;
            }
            String village_str = village.getText().toString();
            String pincode_str = pincode.getText().toString();
            String taluk_str = taluk.getText().toString();
            String district_str = district.getText().toString();
            //  String serviceType_str = serviceType.getText().toString();


            if (StringUtils.isEmpty(district_str)) {
                consumer_header();
                district.setError(getString(R.string.district_err));
                return;
            }
            /*if (StringUtils.isEmpty(serviceType_str)) {
                consumer_header();
                district.setError(getString(R.string.service_type_err));
                return;
            }*/
            if (StringUtils.isEmpty(taluk_str)) {
                consumer_header();
                taluk.setError(getString(R.string.taluk_err));
                return;
            }
            if (StringUtils.isEmpty(village_str)) {
                consumer_header();
                village.setError(getString(R.string.village_err));
                return;
            }
            if (!validatePincode()) {
                return;
            }
            getConsumerDetails();
            connection_header();
            // meter_header();


        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }


    private void meter_Info() {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/meterdetails";
                httpConnection.sendRequest(url, null, ServiceListenerType.METER_INFO, SyncHandler, RequestType.GET, null, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private void getMeter_responseData(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("personal_Response", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        NewConnectionDto connectionResponse;
        connectionResponse = gson.fromJson(response, NewConnectionDto.class);
        if (connectionResponse != null) {
            if (connectionResponse.getStatusCode() == 0) {
                meterBrandList = connectionResponse.getMeterBrand();
                meterTypeList = connectionResponse.getMeterType();
                ArrayList<String> meterBrandArray = new ArrayList<String>();
                for (int i = 0; i < meterBrandList.size(); i++) {

                    if (languageCode.equalsIgnoreCase("ta")) {
                        meterBrandArray.add(meterBrandList.get(i).getRegionalName());
                    } else {
                        meterBrandArray.add(meterBrandList.get(i).getName());
                    }

                }
                ArrayList<String> meterTypeArray = new ArrayList<String>();
                for (int i = 0; i < meterTypeList.size(); i++) {
                    if (languageCode.equalsIgnoreCase("ta")) {
                        meterTypeArray.add(meterTypeList.get(i).getRegionalName());
                    } else {
                        meterTypeArray.add(meterTypeList.get(i).getName());
                    }

                }
                ArrayAdapter<String> meterbrandAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, meterBrandArray);
                ArrayAdapter<String> meterTypeAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, meterTypeArray);
                meterBrand.setAdapter(meterbrandAdpter);
                meterType.setAdapter(meterTypeAdpter);
                meterBrand.setFocusable(true);
                meterType.setFocusable(true);
            } else if (connectionResponse.getStatusCode() == 3118) {
                Toast.makeText(getApplicationContext(), "" + connectionResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            } else if (connectionResponse.getStatusCode() == 3117) {
                Toast.makeText(getApplicationContext(), "" + connectionResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
        }
    }

    /*private void previewImageDialog1(ImageView upload) {
        try {                                                        //

            Log.e("testing", "testing");
            final MaterialDialog mMaterialDialog = new MaterialDialog(this);
            mMaterialDialog.setCanceledOnTouchOutside(true);
            mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                }
            });
            Bitmap bitmap = ((BitmapDrawable) upload.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 10), (int) (bitmap.getHeight() * 10), true);
            Drawable drawe = new BitmapDrawable(getResources(), resized);
            mMaterialDialog.setBackground(drawe);
            mMaterialDialog.show();

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }*/


    private void registerConnnction(ConnectionDto customer_details) {
        try {
            progressBar = new CustomProgressDialog(ConnectionRegisterationActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/add";
                String login = new Gson().toJson(customer_details);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.REG_CONNECTION, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private void getconnenctionResponse(Bundle message) {

        try {
            dismissProgress();
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("connection REsponse", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionDto connectionResponse = gson.fromJson(response, ConnectionDto.class);
            if (connectionResponse != null) {
                if (connectionResponse.getStatusCode() == 0) {
                    AlertDialog alertdialog = new AlertDialog(ConnectionRegisterationActivity.this, getString(R.string.connnecion_register_mdg));
                    alertdialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "" + connectionResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("connectionadd Error", e.toString(), e);
        }
    }


    private boolean validateSerialNumber() {
        if (serialnumber.getText().toString().trim().isEmpty()) {
            serialnumberLay.setError(getString(R.string.serialnumber_err));
            requestFocus(serialnumber);
            return false;
        } else {
            serialnumberLay.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isValidateMeterSerialNumber_() {
        String regExpn =
                "[a-zA-Z]+(\\\\.[0-9]+)*@[A-Za-z0-9]+(\\\\.+)*(\\{1,30})$";
//[a-zA-Z]*[0-9]+[a-zA-Z0-9]*{1,30}
        CharSequence inputStr = serialnumber.getText().toString().trim();

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            return true;
        } else {
            serialnumber.setError(getResources().getString(R.string.validate_meterSerialNumber));
            return false;
        }
    }

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();

    }

    private boolean validateSerialNumberMinLength() {
        if (serialnumber.getText().toString().length() < 3) {
            serialnumberLay.setError(getString(R.string.serialnumbercount_err));
            requestFocus(serialnumber);
            return false;
        } else {
            serialnumberLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMeterReadingNumber() {
        if (lastmeter_value.getText().toString().trim().isEmpty()) {
            lastmeterLay.setError(getString(R.string.lastbillreading_err));
            requestFocus(lastmeter_value);
            return false;
        } else {
            lastmeterLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastreading() {
        if (lastreadDate.getText().toString().trim().isEmpty()) {
            lastreadingLay.setError(getString(R.string.readingtaken_err));
            requestFocus(lastreadDate);
            return false;
        } else {
            lastreadingLay.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isValidateMeterSerialNumber() {
        String regExpn =
                "[a-zA-Z]+(\\\\.[0-9]+)*@[A-Za-z0-9]+(\\\\.+)*(\\{1,30})$";
//[a-zA-Z]*[0-9]+[a-zA-Z0-9]*{1,30}
        CharSequence inputStr = serialnumber.getText().toString().trim();

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            return true;
        } else {
            serialnumber.setError(getResources().getString(R.string.validate_meterSerialNumber));
            return false;
        }
    }

    private boolean validateFromDate() {
        if (fromDate.getText().toString().trim().isEmpty()) {
            fromdateLay.setError(getString(R.string.fromdate_err));
            requestFocus(fromDate);
            return false;
        } else {
            fromdateLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateToDate() {
        if (toDate.getText().toString().trim().isEmpty()) {
            todateLay.setError(getString(R.string.todate_err));
            requestFocus(toDate);
            return false;
        } else {
            todateLay.setErrorEnabled(false);
        }
        return true;
    }


    View.OnClickListener showDatePicker = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final View vv = v;
            hideKeyBoard();
            datePickerDialog = new DatePickerDialog(ConnectionRegisterationActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    try {
                        if (vv.getId() == R.id.dates) {

                            if (lastreadDate.getText().toString().trim().length() > 0) {
//                                showDialog(DATE_DIALOG_ID);
                                last_date = dayOfMonth;
                                last_year = year;
                                last_month = monthOfYear;
                                lastreadDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                                Log.e("output lastreadDate", "" + dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                              /*  sDate=lastreadDate.getText().toString();*/
                              /*  sDate=String.valueOf(lastreadDate.getText().toString());*/


                            } else {

                                last_date = dayOfMonth;
                                last_year = year;
                                last_month = monthOfYear;
                                lastreadDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                                Log.e("Intput lastreadDate", "" + dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);

                          /* sDate=String.valueOf(lastreadDate.getText().toString());
                                Log.e("sDate", sDate);*/

                            }

                        } else if (vv.getId() == R.id.from_date) {
                            fromDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                            String dt = dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year;  // Start date
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
                            Date myDate = sdf1.parse(dt);
                            myDate = DateUtil.addDays(myDate);
                            String output_date = sdf1.format(myDate);
                            Log.e("output date_latest", "" + output_date);
                            toDate.setText(output_date);

                           /* sDate=toDate.getText().toString();*/
                        } else {
                            toDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                           /* sDate=toDate.getText().toString();*/

                        }
                    } catch (Exception e) {
                        Log.e("exceptino e", e.toString(), e);
                    }
                }
            }, last_year, last_month, last_date);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
          /*  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = dateFormat.parse(sDate);
                System.out.println(date.toString()); // Wed Dec 04 00:00:00 CST 2013
                Log.e("datetostring1", "" + date.toString());

                String output = dateFormat.format(date);
                System.out.println(output); // 2013-12-04
                Log.e("datetostring2", "" + date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            datePickerDialog.show();
        }
    };

    View.OnClickListener showDatePicker_ = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final View vv = v;
            hideKeyBoard();
            datePickerDialog = new DatePickerDialog(ConnectionRegisterationActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    try {
                        /*if (vv.getId() == R.id.dates) {

                            if(lastreadDate.getText().toString().trim().length()>0){
//                                showDialog(DATE_DIALOG_ID);
                                last_date = dayOfMonth;
                                last_year = year;
                                last_month = monthOfYear;
                                lastreadDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                            }else{

                                last_date = dayOfMonth;
                                last_year = year;
                                last_month = monthOfYear;
                                lastreadDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                            }

                        } else */
                        if (vv.getId() == R.id.from_date) {

                            from_date = dayOfMonth;
                            from_month = monthOfYear;
                            from_year = year;

                            fromDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                            String dt = dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year;  // Start date
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
                            Date myDate = sdf1.parse(dt);
                            myDate = DateUtil.addDays(myDate);
                            String output_date = sdf1.format(myDate);
                            Log.e("output date_latest", "" + output_date);
                            toDate.setText(output_date);
                           /* sDate=toDate.getText().toString();*/
                        } else {
                            toDate.setText(dayOfMonth + "-" + months[monthOfYear + 1] + "-" + year);
                           /* sDate=toDate.getText().toString();*/
                        }
                    } catch (Exception e) {
                        Log.e("exceptino e", e.toString(), e);
                    }
                }
            }, from_year, from_month, from_date);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        /*    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date d = sdf.parse(sDate);
                datePickerDialog.getDatePicker().setMinDate(d.getTime());
                Log.e("d", "" + d);
            }catch (Exception e)
            {
                e.printStackTrace();
            }*/

            datePickerDialog.show();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        last_year, last_month, last_date);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            last_year = selectedYear;
            last_month = selectedMonth;
            last_date = selectedDay;

           /* // set selected date into textview
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            dpResult.init(year, month, day, null);*/

        }
    };


    private ConnectionDto getMeterDetails() {
        try {
            getConnectionDetails();
            getConsumerDetails();
            consumerInfo.setSerialNumber(serialnumber.getText().toString());

            consumerInfo.setBillCycleFromDate(fromDate.getText().toString());
            consumerInfo.setBillCycleToDate(toDate.getText().toString());
            consumerInfo.setLastMeterReading(lastmeter_value.getText().toString());
            consumerInfo.setLastMeterReadingDate(lastreadDate.getText().toString());
            consumerInfo.setMeterImage(ebmeter_photo);
            Calendar c = Calendar.getInstance();
            Log.e("Current time => ", "" + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            String formattedDate = df.format(c.getTime());
            Log.e("Current  => ", "" + formattedDate);
            consumerInfo.setImageTakenDate(formattedDate);
            GPSService mGPSService = new GPSService(getApplicationContext());
            Location locationB = mGPSService.getLocation();
            if (locationB != null) {
                Log.e("Location", locationB.getLatitude() + "----" + locationB.getLongitude());


            } else {

                    showSettingsAlert();




            }
            consumerInfo.setLatitude(locationB.getLatitude());
            consumerInfo.setLongitude(locationB.getLongitude());

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return consumerInfo;
    }


    private void completeBtnClick() {


//        nextbtnConsumer();
        if (!validateConsumerName()) {
            return;
        }
        String connectionUserType = serviceType.getText().toString();
        if (StringUtils.isEmpty(connectionUserType)) {
            consumer_header();
            serviceType.setError(getString(R.string.conn_user_category));
            return;
        }
        if (!validateAddress1()) {
            return;
        }
        if (!validateAddress2()) {
            return;
        }
        String village_str = village.getText().toString();
        String pincode_str = pincode.getText().toString();
        String taluk_str = taluk.getText().toString();
        String district_str = district.getText().toString();
        //  String serviceType_str = serviceType.getText().toString();


        if (StringUtils.isEmpty(district_str)) {
            consumer_header();
            district.setError(getString(R.string.district_err));
            return;
        }
            /*if (StringUtils.isEmpty(serviceType_str)) {
                consumer_header();
                district.setError(getString(R.string.service_type_err));
                return;
            }*/
        if (StringUtils.isEmpty(taluk_str)) {
            consumer_header();
            taluk.setError(getString(R.string.taluk_err));
            return;
        }
        if (StringUtils.isEmpty(village_str)) {
            consumer_header();
            village.setError(getString(R.string.village_err));
            return;
        }
        if (!validatePincode()) {
            return;
        }
        String phase_str = phase.getText().toString();
        String connection_str = connection_type.getText().toString();
        String consumer_str = consumer_type.getText().toString();
        if (StringUtils.isEmpty(connection_str)) {
            connection_header();
            connection_type.setError(getString(R.string.connection_err));
            return;
        }
        if (StringUtils.isEmpty(consumer_str)) {
            connection_header();
            consumer_type.setError(getString(R.string.consumer_err));
            return;
        }
        if (StringUtils.isEmpty(phase_str)) {
            connection_header();
            phase.setError(getString(R.string.phase_err));
            return;
        }

        if (StringUtils.isEmpty(eb_card_photo)) {
            connection_header();
//            Toast.makeText(getApplicationContext(), "Upload Card Photo should not be empty", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_EBCard_photo_error), Toast.LENGTH_SHORT).show();

            return;
        }

        if (StringUtils.isEmpty(eb_bill_photo)) {
            connection_header();
//            Toast.makeText(getApplicationContext(), "Upload Bill Photo should not be empty", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_Bill_photo_error), Toast.LENGTH_SHORT).show();
            return;
        }


        if (!validateSerialNumber()) {
            return;
        }
        /*if(!isValidateMeterSerialNumber()){
            return;
        }*/
        if (!validateSerialNumberMinLength()) {
            return;
        }
        String meterbrand_str = meterBrand.getText().toString();
        String metertype_str = meterType.getText().toString();
        if (StringUtils.isEmpty(metertype_str)) {
            meterType.setError(getString(R.string.metertype_err));
            return;
        }
        if (StringUtils.isEmpty(meterbrand_str)) {
            meterBrand.setError(getString(R.string.meterbrand_err));
            return;
        }
        if (!validateMeterReadingNumber()) {
            return;
        }

        if (!validateFromDate()) {
            return;
        }
        if (!validateToDate()) {
            return;
        }
        if (!validateLastreading()) {
            return;
        }
        if (StringUtils.isEmpty(ebmeter_photo)) {

//            Toast.makeText(getApplicationContext(), "Upload Meter Photo should not be empty", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_Meter_photo_error), Toast.LENGTH_SHORT).show();

            return;
        }


        ConnectionDto customer_details = getMeterDetails();

        Log.e("Register","Latitiude"+customer_details.getLatitude());

         if(customer_details.getLatitude() == null){
             return;
         }



        registerConnnction(customer_details);
    }


    private void getconsumerTypeResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            NewConnectionDto connectionResponse;
            connectionResponse = gson.fromJson(response, NewConnectionDto.class);
            if (connectionResponse != null) {
                if (connectionResponse.getStatusCode() == 0) {
                    consumerList = connectionResponse.getConsumerType();
                    consumerArray.clear();
                    for (int i = 0; i < consumerList.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            consumerArray.add(consumerList.get(i).getRegionalName());
                        } else {
                            consumerArray.add(consumerList.get(i).getName());
                        }
                    }
                    ArrayAdapter<String> consumerTypeAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, consumerArray);
                    consumer_type.setAdapter(consumerTypeAdpter);
                    consumer_type.setFocusable(true);
                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.no_consumer_err), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void getPhaseResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            NewConnectionDto connectionResponse;
            connectionResponse = gson.fromJson(response, NewConnectionDto.class);
            if (connectionResponse != null) {
                if (connectionResponse.getStatusCode() == 0) {
                    phaseList = connectionResponse.getPhases();
                    phaseArray.clear();
                    for (int i = 0; i < phaseList.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            phaseArray.add(phaseList.get(i).getRegionalName());
                        } else {
                            phaseArray.add(phaseList.get(i).getName());
                        }
                    }
                    ArrayAdapter<String> phaseAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, phaseArray);
                    phase.setAdapter(phaseAdpter);
                    phase.setFocusable(true);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed() {

        BackPressedDialog dialog = new BackPressedDialog(ConnectionRegisterationActivity.this,
                getResources().getString(R.string.title_connection));
        dialog.show();
        /*Intent registerPage = new Intent(ConnectionRegisterationActivity.this, ConnectionListActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();*/
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ConnectionRegisterationActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


}




