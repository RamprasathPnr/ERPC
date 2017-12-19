package com.omneagate.erbc.Activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.BackPressedDialog;
import com.omneagate.erbc.Activity.Dialog.CalendarDialog;
import com.omneagate.erbc.Dto.AadharDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DistrictDto;
import com.omneagate.erbc.Dto.EnumDto.Gender;
import com.omneagate.erbc.Dto.EnumDto.Genderta;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.LoginResponseDto;
import com.omneagate.erbc.Dto.MaritalStatusDto;
import com.omneagate.erbc.Dto.OccupationDto;
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
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by user1 on 26/4/16.
 */
public class RegisterationActivity extends BaseActivity implements View.OnClickListener {



    private AadharDto aadharDto;
    LinearLayout scanner_header;

    ArrayAdapter<String> statusAdpter;
    ArrayAdapter<String> occpationAdpter;


    TextView personalheader_txt, communiheader_txt;
    LinearLayout personal_tab, communi_tab, personal_view, communi_view;
    Button nextBtn, submitBtn;
    LoginResponseDto loginResponse;
    EditText firstName, middleName, lastName, mobilenumber, datepicker, email,aadharid;
    MaterialBetterSpinner genderSpinner, occuaptionSpinner, statusSpinner;
    TextInputLayout firstanameLay, middlenameLay, lastnameLay, mobilenumberLay, dobLay, emailLay,aadhar_req;
    TextInputLayout address1Lay, address2Lay, pincodeLay;
    MaterialBetterSpinner taluk, district, state, country1, village;
    EditText address1, address2, pincode;
    CustomerDto customerInfo;
    List<MaritalStatusDto> mstatusList;
    List<OccupationDto> occupationList;
    String occuaption_name, mstatusname;
    int occuaption_id, mstatus_id;
    Gender gender_select;
    //    Genderta gender_select_;
    String countryName, stateName, districtName, talukName, villageName;
    int country_id, state_id, district_id, taluk_id, village_id;
    List<countryDto> countryList;
    List<StateDto> statelist;
    List<DistrictDto> districtList;
    List<TalukDto> talukList;
    List<VillageDto> villageList;
    ArrayAdapter<String> stateadapter, villageAdapter, talukAdapter, districtAdapter;
    ArrayList<String> districtArray = new ArrayList<String>();
    ArrayList<String> talukListArray = new ArrayList<String>();
    ArrayList<String> villageListArray = new ArrayList<String>();
    CustomProgressDialog progressBar;
    String languageCode = GlobalAppState.language;
    private static final String TAG = RegisterationActivity.class.getName();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CalendarDialog alertdialog;
    private int mYear, mMonth, mDay;
    String months[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};
    int paddyCategoryPosId;
    ///stateadapter, villageAdapter, talukAdapter, districtAdapter // taluk, district, state, country1, village

    private String emailBlockCharacterSet = "!#$%^&*()-+=/><?':;~`";
    private String addressBlockCharacterSet = "@#$%&+=()*\":;?";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerationactivity);
        aadharDto = new AadharDto();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        configureInitialPage();
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
        aadharid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(aadharid.getText().toString().length()==12)
                {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(aadharid.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        country1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                countryName = country1.getText().toString();
                country_id = countryList.get(position).getId();
                countryDto countrydto = new countryDto();
                countrydto.setId(countryList.get(position).getId());
                countrydto.setName(country1.getText().toString());
                state.setFocusable(true);
                countryclick(countrydto);
            }
        });
        occuaptionSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    occuaption_name = occuaptionSpinner.getText().toString();
                    occuaption_id = occupationList.get(position).getId();

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }
            }
        });
        statusSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    mstatusname = statusSpinner.getText().toString();
                    mstatus_id = mstatusList.get(position).getId();


                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e(TAG, e.toString());
                }

            }
        });

        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    stateName = state.getText().toString();
                    state_id = statelist.get(position).getId();
                    StateDto statedata = new StateDto();
                    statedata.setId(statelist.get(position).getId());
                    statedata.setName(state.getText().toString());
                    district.setText("");
                    taluk.setText("");
                    village.setText("");
                    district.setFocusable(true);
                    stateClick(statedata);

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("state_error", e.toString(), e);
                }


            }
        });
        district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    talukName = taluk.getText().toString();
                    taluk_id = talukList.get(position).getId();
                    TalukDto talukdata = new TalukDto();
                    talukdata.setId(talukList.get(position).getId());
                    talukdata.setName(taluk.getText().toString());
                    village.setText("");
                    village.setFocusable(true);
                    talukClick(talukdata);
                } catch (Exception e) {
                    Log.e("errortaluk", e.toString(), e);
                }


            }
        });

        village.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    villageName = village.getText().toString();
                    village_id = villageList.get(position).getId();

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("Error", e.toString(), e);
                }


            }
        });

        genderSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    if (languageCode.equalsIgnoreCase("ta")) {
//                        gender_select_ = (Genderta)parent.getItemAtPosition(position);
//                        gender_select = (Gender)parent.getItemAtPosition(position);
//                    }else{
                    gender_select = (Gender) parent.getItemAtPosition(position);
//                    }

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("error meessage", e.toString(), e);
                }
            }
        });

        genderSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setCursorVisible(false);
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setCursorVisible(true);
                email.requestFocus();
            }
        });
    }

    private void configureInitialPage() {

        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (languageCode.equalsIgnoreCase("ta")) {
            setTitle("பதிவு");
        } else {
            setTitle("Registration");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        personaldetail_Info();
        scanner_header=(LinearLayout)findViewById(R.id.scanner_header);
        scanner_header.setOnClickListener(this);
        personalheader_txt = (TextView) findViewById(R.id.personaltxt);
        communiheader_txt = (TextView) findViewById(R.id.communicationtxt);
        personal_tab = (LinearLayout) findViewById(R.id.personal_header);
        communi_tab = (LinearLayout) findViewById(R.id.communication_header);
        personal_view = (LinearLayout) findViewById(R.id.personal_layout);
        communi_view = (LinearLayout) findViewById(R.id.communication_layout);
        submitBtn = (Button) findViewById(R.id.submitbutton);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        //personal info
        firstanameLay = (TextInputLayout) findViewById(R.id.layout_firstname);
        aadhar_req = (TextInputLayout) findViewById(R.id.layout_aadharid);
        middlenameLay = (TextInputLayout) findViewById(R.id.layout_middlename);
        lastnameLay = (TextInputLayout) findViewById(R.id.layout_lastname);
        mobilenumberLay = (TextInputLayout) findViewById(R.id.layout_mobilenumber);
        dobLay = (TextInputLayout) findViewById(R.id.layout_dob);
        emailLay = (TextInputLayout) findViewById(R.id.layout_email);
        firstName = (EditText) findViewById(R.id.firstname);

        middleName = (EditText) findViewById(R.id.middlename);
        aadharid = (EditText) findViewById(R.id.aadharid);
        aadharid.requestFocus();
        lastName = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);

        mobilenumber = (EditText) findViewById(R.id.mobilenumber);
        mobilenumber.setText(/*"+91-" + */LoginActivity.mobile_number);
        mobilenumber.setEnabled(false);
        datepicker = (EditText) findViewById(R.id.dates);
        datepicker.setShowSoftInputOnFocus(false);
        genderSpinner = (MaterialBetterSpinner) findViewById(R.id.genderid);
        occuaptionSpinner = (MaterialBetterSpinner) findViewById(R.id.occupation);
        statusSpinner = (MaterialBetterSpinner) findViewById(R.id.mstatus);
        //communication_info
        address1Lay = (TextInputLayout) findViewById(R.id.layout_address1);
        address2Lay = (TextInputLayout) findViewById(R.id.layout_address2);
        pincodeLay = (TextInputLayout) findViewById(R.id.layout_pincode);
        address1 = (EditText) findViewById(R.id.address1);
        address2 = (EditText) findViewById(R.id.address2);
        village = (MaterialBetterSpinner) findViewById(R.id.village);
        pincode = (EditText) findViewById(R.id.pincode);
        taluk = (MaterialBetterSpinner) findViewById(R.id.taluk);
        district = (MaterialBetterSpinner) findViewById(R.id.district);
        state = (MaterialBetterSpinner) findViewById(R.id.state);
        country1 = (MaterialBetterSpinner) findViewById(R.id.country);
        countryList = DBHelper.getInstance(getApplicationContext()).getCountryList();
        ArrayList<String> countryArray = new ArrayList<String>();


        for (int i = 0; i < countryList.size(); i++) {
            countryArray.add(countryList.get(i).getName());
        }
        ArrayAdapter<String> countryadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, countryArray);
        country1.setAdapter(countryadapter);
        datepicker.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        personal_tab.setOnClickListener(this);
        communi_tab.setOnClickListener(this);

        address1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int length = 225;
                if (address1.getText().toString().length() == length) {
                    address1Lay.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        address2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = 225;
                if (address2.getText().toString().length() == length) {
                    address2Lay.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.setFilters(new InputFilter[]{Util.inputFilter(emailBlockCharacterSet)});
     /*   address1.setFilters(new InputFilter[]{Util.inputFilter(addressBlockCharacterSet)});
        address2.setFilters(new InputFilter[]{Util.inputFilter(addressBlockCharacterSet)});*/
        address2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(75)});
        address1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(75)});
    }





    @Override
    public void onBackPressed() {
        BackPressedDialog dialog = new BackPressedDialog(RegisterationActivity.this,
                getResources().getString(R.string.title_registration));
        dialog.show();
        /*Intent registerPage = new Intent(RegisterationActivity.this, LoginActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.personal_header:
                personal_header();

                break;
            case R.id.communication_header:
                personalheader_txt.setBackgroundColor(getResources().getColor(R.color.white));
                communiheader_txt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
                communiheader_txt.setTextColor(getResources().getColor(R.color.black));
                personalheader_txt.setTextColor(getResources().getColor(R.color.gray_2));
                personal_view.setVisibility(View.GONE);
                communi_view.setVisibility(View.VISIBLE);
                break;
            case R.id.dates:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                try {
                    String dateOfBirth = datepicker.getText().toString();
                    final Calendar c = Calendar.getInstance();
                    if (dateOfBirth != null
                            && dateOfBirth.length() > 0
                            && !dateOfBirth.equalsIgnoreCase(" ")) {
                        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy");
                        c.setTime(dt.parse(dateOfBirth));
                    }
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    datepicker.setText(dayOfMonth + "-" + months[monthOfYear] + "-" + year);
                                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                    datePickerDialog.show();
                } catch (ParseException e) {
                    GlobalAppState.getInstance().trackException(e);
                    e.printStackTrace();
                }
                break;
            case R.id.nextBtn:
                validateforPersonalInfo();
                break;
            case R.id.submitbutton:
                validateforCommunicationInfo();
                break;


            case R.id.scanner_header:
                if (ActivityCompat.checkSelfPermission(RegisterationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.
                    requestCameraPermission();
                } else {
                    launchQRScanner();
                }

                break;
        }
    }

    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
    }

    private void launchQRScanner() {
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    private void personal_header() {
        personalheader_txt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
        communiheader_txt.setBackgroundColor(getResources().getColor(R.color.white));
        communiheader_txt.setTextColor(getResources().getColor(R.color.gray_2));
        personalheader_txt.setTextColor(getResources().getColor(R.color.black));
        personal_view.setVisibility(View.VISIBLE);
        communi_view.setVisibility(View.GONE);
    }


    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }


    private void personaldetail_Info() {
        try {
            progressBar = new CustomProgressDialog(RegisterationActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/personaldetails";
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.PERSONAL_INFO, SyncHandler, RequestType.GET, null, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void personal_Response(Bundle message) {

        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto personalResponse;
            personalResponse = gson.fromJson(response, personalDto.class);
            if (personalResponse != null) {
                if (personalResponse.getStatusCode() == 0) {
                    DBHelper.getInstance(getApplicationContext()).insertCountry(personalResponse.getCountry());
                    mstatusList = personalResponse.getMaritalStatus();
                    occupationList = personalResponse.getOccupation();
                    Collections.sort(mstatusList, new Comparator<MaritalStatusDto>() {
                        @Override
                        public int compare(MaritalStatusDto dto1, MaritalStatusDto dto2) {
                            return dto1.getName().compareTo(dto2.getName());
                        }
                    });
                    ArrayList<String> mstatusArray = new ArrayList<String>();
                    for (int i = 0; i < mstatusList.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            mstatusArray.add(mstatusList.get(i).getRegionalName());
                        } else {
                            mstatusArray.add(mstatusList.get(i).getName());
                        }
                    }
                    Collections.sort(occupationList, new Comparator<OccupationDto>() {
                        @Override
                        public int compare(OccupationDto occupationDto, OccupationDto t1) {
                            return occupationDto.getName().compareTo(t1.getName());
                        }
                    });
                    ArrayList<String> occupationArray = new ArrayList<String>();
                    for (int i = 0; i < occupationList.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            occupationArray.add(occupationList.get(i).getRegionalName());
                        } else {
                            occupationArray.add(occupationList.get(i).getName());
                        }

                    }
                    ArrayAdapter<Gender> genderAdpter;
//                    ArrayAdapter<Genderta> genderAdpter_;
//                    if (languageCode.equalsIgnoreCase("ta")) {
//                        genderAdpter_ = new ArrayAdapter<Genderta>(getApplicationContext(),R.layout.dropdownrow, Genderta.values());
//                        genderSpinner.setAdapter(genderAdpter_);
//
//                        genderAdpter = new ArrayAdapter<Gender>(getApplicationContext(),R.layout.dropdownrow, Gender.values());
//
//                        ArrayAdapter<String> statusAdpter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdownrow, mstatusArray);
//                        ArrayAdapter<String> occpationAdpter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdownrow, occupationArray);
//                        statusSpinner.setAdapter(statusAdpter);
//                        occuaptionSpinner.setAdapter(occpationAdpter);
//                    }else {
                    genderAdpter = new ArrayAdapter<Gender>(getApplicationContext(), R.layout.dropdownrow, Gender.values());
                    genderSpinner.setAdapter(genderAdpter);


                    statusAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, mstatusArray);
                    occpationAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, occupationArray);
                    statusSpinner.setAdapter(statusAdpter);
                    occuaptionSpinner.setAdapter(occpationAdpter);
//                    }


                    genderSpinner.setFocusable(true);
                    statusSpinner.setFocusable(true);
                    occuaptionSpinner.setFocusable(true);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString());
        }
    }

    public void setToTextDate(String textDate) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        datepicker.setText(textDate);
        alertdialog.cancel();

    }

    private void validateforPersonalInfo() {
        try {
            if(!aadharid.getText().toString().trim().isEmpty()){
                if(!validateAAdharNumber())
                    return;
            }else{
                aadhar_req.setErrorEnabled(false);
            }

            if (!validateFirstName()) {
                return;
            }
            if (!validateLastName()) {
                return;
            }

            if (!validateMobileNumber()) {
                return;
            }

            if (!validateEmailid()) {
                return;
            }
            String gender_str = genderSpinner.getText().toString();
            if (StringUtils.isEmpty(gender_str)) {
                personal_header();
                genderSpinner.setError(getString(R.string.gender_err));
                return;
            }
            if (!validateDOB()) {
                return;
            }
            String[] items1 = datepicker.getText().toString().split("-");
            String d1 = items1[0];
            String m1 = items1[1];
            String y1 = items1[2];
            int d = Integer.parseInt(d1);
            int y = Integer.parseInt(y1);
            Calendar dateOfYourBirth = new GregorianCalendar(y, 1, d);
            Calendar today = Calendar.getInstance();
            int yourAge = today.get(Calendar.YEAR) - dateOfYourBirth.get(Calendar.YEAR);
            dateOfYourBirth.add(Calendar.YEAR, yourAge);
            if (today.before(dateOfYourBirth)) {
                yourAge--;
            }
            Log.e("You are ", "" + yourAge + " old!");

            if (yourAge <= 17) {
                personal_header();
                dobLay.setError(getString(R.string.minimam_age));
                datepicker.setText("");
                return;
            }

            String occupation_str = occuaptionSpinner.getText().toString();
            String status_str = statusSpinner.getText().toString();


          /*  if (StringUtils.isEmpty(occupation_str)) {
                personal_header();
                occuaptionSpinner.setError(getString(R.string.occupation_err));
                return;
            }
            if (StringUtils.isEmpty(status_str)) {
                personal_header();
                statusSpinner.setError(getString(R.string.marital_err));
                return;
            }*/

            personalheader_txt.setBackgroundColor(getResources().getColor(R.color.white));
            communiheader_txt.setBackgroundColor(getResources().getColor(R.color.tabcolor));
            communiheader_txt.setTextColor(getResources().getColor(R.color.black));
            personalheader_txt.setTextColor(getResources().getColor(R.color.gray_2));
            personal_view.setVisibility(View.GONE);
            communi_view.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }


    }

    private void validateforCommunicationInfo() {

        try {
            validateforPersonalInfo();

            if (!email.getText().toString().matches(emailPattern)) {
                personal_header();
                emailLay.setError(getString(R.string.emailinvalid_err));
                requestFocus(email);
                return;
            }

            if (!validateAddress1()) {
                return;
            }
            if (!validateAddress2()) {
                return;
            }
            CustomerDto customer_details = getCommunicationDetails();


            String village_str = village.getText().toString();
            String taluk_str = taluk.getText().toString();
            String district_str = district.getText().toString();
            String state_str = state.getText().toString();
            String country_str = country1.getText().toString();

            if (StringUtils.isEmpty(country_str)) {
                country1.setError(getString(R.string.country_err));
                return;
            }
            if (StringUtils.isEmpty(state_str)) {
                state.setError(getString(R.string.state_err));
                return ;
            }
            if (StringUtils.isEmpty(district_str)) {
                district.setError(getString(R.string.district_err));
                return;
            }
            if (StringUtils.isEmpty(taluk_str)) {
                taluk.setError(getString(R.string.taluk_err));
                return;
            }
            if (StringUtils.isEmpty(village_str)) {
                village.setError(getString(R.string.village_err));
                return;
            }
            if (pincode.getText().toString().trim().isEmpty()) {
                pincodeLay.setError(getString(R.string.pincode_err));
                requestFocus(pincode);
                return;
            } else if (pincode.length() < 6) {
                pincodeLay.setError(getString(R.string.pinvalid_err));
                requestFocus(pincode);
                return;
            } else if (pincode.getText().toString().trim().equalsIgnoreCase("000000")) {
                pincodeLay.setError(getString(R.string.pinvalid_err));
                requestFocus(pincode);
                return;

            }





            else {
                registerUser(customer_details);
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }


    private CustomerDto getCommunicationDetails() {

        customerInfo = new CustomerDto();
        String first_str = firstName.getText().toString();
        String middle_str = middleName.getText().toString();
        String last_str = lastName.getText().toString();
        String email_str = email.getText().toString();
        String mobile_str = LoginActivity.mobile_number;
        String dob_str = datepicker.getText().toString();
        String aadhar_str=aadharid.getText().toString();
        customerInfo.setFirstName(first_str);
        customerInfo.setMiddleName(middle_str);
        customerInfo.setLastName(last_str);
        customerInfo.setMobileNumber(mobile_str);
        customerInfo.setEmail(email_str);
        customerInfo.setDob(dob_str);
        customerInfo.setAdhaarNumber(aadhar_str);
//        if (languageCode.equalsIgnoreCase("ta")) {
//            customerInfo.setGender(gender_select);
//        }else{
        customerInfo.setGender(gender_select);
//        }

        OccupationDto occupation = new OccupationDto();
        occupation.setId(occuaption_id);
        occupation.setName(occuaption_name);
        customerInfo.setOccupation(occupation);
        MaritalStatusDto mstatus = new MaritalStatusDto();
        mstatus.setId(mstatus_id);
        mstatus.setName(mstatusname);
        customerInfo.setMaritalStatus(mstatus);
        String address1_str = address1.getText().toString();
        String address2_str = address2.getText().toString();
        String village_str = village.getText().toString();
        String pincode_str = pincode.getText().toString();
        String taluk_str = taluk.getText().toString();
        String district_str = district.getText().toString();
        String state_str = state.getText().toString();
        String country_str = country1.getText().toString();

        if (StringUtils.isEmpty(country_str)) {
            country1.setError(getString(R.string.country_err));
            return null;
        }
        if (StringUtils.isEmpty(state_str)) {
            state.setError(getString(R.string.state_err));
            return null;
        }
        if (StringUtils.isEmpty(district_str)) {
            district.setError(getString(R.string.district_err));
            return null;
        }
        if (StringUtils.isEmpty(taluk_str)) {
            taluk.setError(getString(R.string.taluk_err));
            return null;
        }
        if (StringUtils.isEmpty(village_str)) {
            village.setError(getString(R.string.village_err));
            return null;
        }
        customerInfo.setAddressLine1(address1_str);
        customerInfo.setAddressLine2(address2_str);
        TalukDto taluk = new TalukDto();
        taluk.setId(taluk_id);
        taluk.setName(talukName);
        customerInfo.setTaluk(taluk);
        DistrictDto district = new DistrictDto();
        district.setId(district_id);
        district.setName(districtName);
        customerInfo.setDistrict(district);
        VillageDto village = new VillageDto();
        village.setId(village_id);
        village.setName(villageName);
        customerInfo.setVillage(village);
        countryDto country = new countryDto();
        country.setId(country_id);
        country.setName(countryName);
        customerInfo.setCountry(country);
        customerInfo.setCountryCode("+91");
        StateDto state = new StateDto();
        state.setId(state_id);
        state.setName(stateName);
        customerInfo.setState(state);
        customerInfo.setPinCode(pincode_str);
        return customerInfo;
    }


    private boolean validateFirstName() {
        if (firstName.getText().toString().trim().isEmpty()) {
            personal_header();
            firstanameLay.setError(getString(R.string.firstname_err));
            requestFocus(firstName);
            return false;
        } else {
            firstanameLay.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateLastName() {

        if (lastName.getText().toString().trim().isEmpty()) {
            personal_header();
            lastnameLay.setError(getString(R.string.lastname_err));
            requestFocus(lastName);
            return false;
        } else {
            lastnameLay.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAAdharNumber() {
       /* if (aadharid.getText().toString().trim().isEmpty()) {
            personal_header();
            aadhar_req.setError(getString(R.string.aadharnumber_err));
            requestFocus(aadharid);
            return false;
        }*/
        if (aadharid.length() < 12) {
            personal_header();
            aadhar_req.setError(getString(R.string.aadhaar_err));
            requestFocus(aadharid);
            return false;

        } else {
            aadhar_req.setErrorEnabled(false);
        }
        return true;
    }












    private boolean validateMobileNumber() {
        if (mobilenumber.getText().toString().trim().isEmpty()) {
            personal_header();
            mobilenumberLay.setError(getString(R.string.mobilenumber_err));
            requestFocus(mobilenumber);
            return false;
        } else if (mobilenumber.length() < 10) {
            personal_header();
            mobilenumberLay.setError(getString(R.string.mobile_err));
            requestFocus(mobilenumber);
            return false;

        } else {
            mobilenumberLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmailid() {
        if (email.getText().toString().trim().isEmpty()) {
            personal_header();
            emailLay.setError(getString(R.string.email_err));
            requestFocus(email);
            return false;
        } else if (!email.getText().toString().matches(emailPattern)) {
            personal_header();
            emailLay.setError(getString(R.string.emailinvalid_err));
            requestFocus(email);
            return false;
        } else {
            emailLay.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateDOB() {
        if (datepicker.getText().toString().trim().isEmpty()) {
            personal_header();
            dobLay.setError(getString(R.string.dob_err));
            requestFocus(datepicker);
            return false;
        } else {
            dobLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress1() {
        if (address1.getText().toString().trim().isEmpty()) {
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
            address2Lay.setError(getString(R.string.address2_err));
            requestFocus(address2);
            return false;
        } else {
            address2Lay.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void countryclick(countryDto countrydata) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/state/getallbycountry";
                String login = new Gson().toJson(countrydata);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GETSTATE, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void countryResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto stateResponse = gson.fromJson(response, personalDto.class);
        if (stateResponse != null) {
            statelist = stateResponse.getStates();
        }
        ArrayList<String> stateArray = new ArrayList<String>();
        for (int i = 0; i < statelist.size(); i++) {
            if (languageCode.equalsIgnoreCase("ta")) {
                stateArray.add(statelist.get(i).getRegionalName());
            } else {
                stateArray.add(statelist.get(i).getName());
            }

        }
        stateadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, stateArray);
        state.setAdapter(stateadapter);
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
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto districtResponse = gson.fromJson(response, personalDto.class);
        if (districtResponse != null) {

            districtList = districtResponse.getDistricts();
        }
        Log.e("districtlist", "" + districtResponse.getDistricts().size());
        if (districtResponse.getDistricts().size() == 0) {
            district.setError("Districts not found.");

        }
        districtArray.clear();
        for (int i = 0; i < districtList.size(); i++) {
            if (languageCode.equalsIgnoreCase("ta")) {
                districtArray.add(districtList.get(i).getRegionalName());
            } else {
                districtArray.add(districtList.get(i).getName());
            }
        }
        districtAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, districtArray);
        district.setAdapter(districtAdapter);
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
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void talukResponse(Bundle message) {
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
    }

    private boolean validatePincode() {

        /*String regex = "^([1-9])([0-9]){5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pincode.toString());
        if (matcher.matches()) {
            Toast.makeText(RegisterationActivity.this, "True", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterationActivity.this, "False", Toast.LENGTH_SHORT).show();
        }*/

        if (pincode.getText().toString().trim().isEmpty()) {
            pincodeLay.setError(getString(R.string.pincode_err));
            requestFocus(pincode);
            return false;
        } else if (pincode.length() < 6) {

            pincodeLay.setError(getString(R.string.pinvalid_err));
            requestFocus(pincode);
            return false;
        } else if (1 > Integer.parseInt(pincode.getText().toString())) {
            pincodeLay.setError(getString(R.string.pinvalid_err));
            requestFocus(pincode);
            return false;
        } else {
            pincodeLay.setErrorEnabled(false);
        }

        return true;
    }

    private void registerUser(CustomerDto customerdto) {
        try {
            if (!validatePincode()) {
                return;
            }

            progressBar = new CustomProgressDialog(RegisterationActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/add";
                String login = new Gson().toJson(customerdto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.REGISTER_USER, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void registerResponse(Bundle message) {

        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            loginResponse = gson.fromJson(response, LoginResponseDto.class);

            if (loginResponse != null) {
                if (loginResponse.getStatusCode() == 0) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    AlertDialog alertdialog = new AlertDialog(RegisterationActivity.this, getString(R.string.register_mdg));
                    alertdialog.show();
                } else if (loginResponse.getStatusCode() == 2025) {
                    personal_header();
                    dobLay.setError(getString(R.string.minimam_age));
                    datepicker.setText("");
                    requestFocus(datepicker);
                    InputMethodManager ii = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    ii.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getStatusCode() == 6666) {
                    personal_header();
                    emailLay.setError("Already Email id Used");
                    email.setText("");
                    requestFocus(email);
                    InputMethodManager ii = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    ii.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getStatusCode() == 5) {
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getStatusCode() == 2029) {
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getStatusCode() == 2022) {
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getErrorDescription().length() > 0
                        && loginResponse.getErrorDescription() != null) {
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case PERSONAL_INFO:
                dismissProgress();
                personal_Response(message);
                break;
            case REGISTER_USER:
                dismissProgress();
                registerResponse(message);
                break;
            case GETSTATE:
                dismissProgress();
                countryResponse(message);
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
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                Intent back_page = new Intent(RegisterationActivity.this, LoginActivity.class);
                back_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(back_page);
                finish();
                break;
        }
    }

    public void call_otp() {
        Intent otp_page = new Intent(RegisterationActivity.this, OTP_activity.class);
        otp_page.putExtra("message", new Gson().toJson(loginResponse));
        otp_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(otp_page);
        finish();
    }























    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        if (contents.contains("<PrintLetterBarcodeData")) {
                            String resultString = null;
                            StringBuilder sb = new StringBuilder(contents);
                            if ((sb.charAt(1) == '/')) {
                                sb.deleteCharAt(1);
                                resultString = sb.toString();
                            } else {
                                resultString = contents;
                            }
                            xmlParsing(resultString);
                        } else {
                            stringParsing(contents);
                        }
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(ProcurementActivity.class.getSimpleName(), "Scan cancelled");
                }
                break;

            default:
                break;
        }
    }




    private void xmlParsing(String xmlData) {
        try {
//            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called xmlData ->" + xmlData);
            String xmlRecords = xmlData;
            Log.e("xmlRecords",""+xmlData);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("PrintLetterBarcodeData");
            Log.e("xmlRecords",""+l.getLength());
            for (int j = 0; j < l.getLength(); ++j) {
                Node prop = l.item(j);
                NamedNodeMap attr = prop.getAttributes();
                if (null != attr) {
                    //Node nodeUid = attr.getNamedItem("uid");
                    String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                            state = "", pc = "", dob = "";
                    try {
                        uid = attr.getNamedItem("uid").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        name = attr.getNamedItem("name").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        gender = attr.getNamedItem("gender").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        yob = attr.getNamedItem("yob").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        co = attr.getNamedItem("co").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        house = attr.getNamedItem("house").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        street = attr.getNamedItem("street").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        lm = attr.getNamedItem("lm").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        loc = attr.getNamedItem("loc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        vtc = attr.getNamedItem("vtc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        po = attr.getNamedItem("po").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        dist = attr.getNamedItem("dist").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        subdist = attr.getNamedItem("subdist").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        state = attr.getNamedItem("state").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        pc = attr.getNamedItem("pc").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    try {
                        dob = attr.getNamedItem("dob").getNodeValue();
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    }

                    aadharDto.setAadhaarNum(Long.parseLong(uid));
                    aadharDto.setUid(uid);
                    aadharDto.setName(name);
                    aadharDto.setGender(gender.charAt(0));
                    aadharDto.setCo(co);
                    aadharDto.setHouse(house);
                    aadharDto.setStreet(street);
                    aadharDto.setLm(lm);
                    aadharDto.setLoc(loc);
                    aadharDto.setVtc(vtc);
                    aadharDto.setPo(po);
                    aadharDto.setDist(dist);
                    aadharDto.setSubdist(subdist);
                    aadharDto.setState(state);
                    aadharDto.setPc(pc);
                    if (!yob.equalsIgnoreCase("")) {
                        aadharDto.setYob(Long.parseLong(yob));
                    }
                    try {
                        if (dob != null && !dob.isEmpty()) {
                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());
                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());

                                aadharDto.setDob(date1.getTime());
                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
//                                        + " Time in millisec -> " + date1.getTime());
                                aadharDto.setDob(date1.getTime());
                            } else {
//                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob Unkown Pattern ->" + dob
//                                );
                            }
                        } else {
                            aadharDto.setDob(null);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        GregorianCalendar gc = new GregorianCalendar();
                        String date = sdf.format(gc.getTime());
                        Date createdDate = sdf.parse(date);
                        aadharDto.setCreatedDate(createdDate.getTime());
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);

                    }

                    Log.e("UIDValue", uid);
                    Log.e("name", name);
                    Log.e("gender", gender);
                    Log.e("yob", yob);
                    Log.e("co", co);
                    Log.e("house", house);
                    Log.e("street", street);
                    Log.e("lm", lm);
                    Log.e("loc", loc);
                    Log.e("vtc", vtc);
                    Log.e("po", po);
                    Log.e("dist", dist);
                    Log.e("subdist", subdist);
                    Log.e("state", state);
                    Log.e("pc", pc);
                    Log.e("dob", dob);
                    firstName.setText("" + name);
                    address1.setText(" "+house+" "+street+" "+lm);
                    address2.setText(" " + loc + " " + vtc + " " + po);
                    pincode.setText(pc);
                    aadharid.setText(""+ uid);
                    aadharid.setEnabled(false);
                  /*  DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
                    Date date1 = df1.parse(dob);
                    String dateob=df1.format(date1);*/
                    //Log.e("date:---",date1);




                    DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    Date date = originalFormat.parse(dob);
                    String formattedDate = targetFormat.format(date);
                    datepicker.setText(formattedDate);
                    Log.e("dob", formattedDate);


                   /* try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                        Date date = formatter.parse(dob);
                        System.out.println(date);
                     //   System.out.println(formatter.format(date));
                        String s=formatter.format(date);
                        datepicker.setText(String.valueOf(s));


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
*/
                    ArrayAdapter<Gender> genderAdpter;
                    genderAdpter = new ArrayAdapter<Gender>(getApplicationContext(), R.layout.dropdownrow, Gender.values());
                    String genders;
                    if (aadharDto.getGender() == 'M') {
                        genders = "Male";
                        gender_select=Gender.Male;
                    } else if (aadharDto.getGender() == 'F') {
                        gender_select=Gender.Female;
                        genders = "Female";
                    } else {
                        gender_select=Gender.Others;
                        genders = "Others";
                    }

                    for(int k=0; k < genderAdpter.getCount(); k++) {
                        if(genders.trim().equals(genderAdpter.getItem(k).toString())){
                            genderSpinner.setText(genderAdpter.getItem(k).toString());
                            break;
                        }
                    }
                    genderSpinner.setAdapter(genderAdpter);

                    country1.setText("India");



                    ArrayList<String> stateArray = new ArrayList<String>();
                    for (int i = 0; i < statelist.size(); i++) {
                        if (languageCode.equalsIgnoreCase("ta")) {
                            stateArray.add(statelist.get(i).getRegionalName());
                        } else {
                            stateArray.add(statelist.get(i).getName());
                        }

                    }
                    stateadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, stateArray);

                    for(int k=0; k < stateadapter.getCount(); k++) {
                        if(state.trim().equals(stateadapter.getItem(k).toString())){
                            this.state.setText(stateadapter.getItem(k).toString());
                            break;
                        }
                    }
                    this.state.setAdapter(stateadapter);

                    for(int k=0; k < districtAdapter.getCount(); k++) {
                        if(dist.trim().equals(districtAdapter.getItem(k).toString())){
                            district.setText(districtAdapter.getItem(k).toString());
                            break;
                        }
                    }
                    this.district.setAdapter(districtAdapter);
                    //taluk, district, state, country1, villa
                    ///stateadapter, villageAdapter, talukAdapter, districtAdapter // taluk, district, state, country1, village

                    for(int k=0; k < talukAdapter.getCount(); k++) {
                        if(subdist.trim().equals(talukAdapter.getItem(k).toString())){
                            taluk.setText(talukAdapter.getItem(k).toString());
                            break;
                        }
                    }
                    this.taluk.setAdapter(talukAdapter);

                    /*for(int k=0; k < villageAdapter.getCount(); k++) {
                        if(v.trim().equals(villageAdapter.getItem(k).toString())){
                            village.setText(villageAdapter.getItem(k).toString());
                            break;
                        }
                    }
                    this.village.setAdapter(villageAdapter);*/

                    /*   ArrayAdapter<String> statusAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, mstatusArray);
                    ArrayAdapter<String> occpationAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, occupationArray);
                    statusSpinner.setAdapter(statusAdpter);
                    occuaptionSpinner.setAdapter(occpationAdpter);*/


                    //genderSpinner, occuaptionSpinner, statusSpinner;
                   /* if(gender.equalsIgnoreCase("M")){
                        genderSpinner.setText("Male");
                    }else{
                        genderSpinner.setText("Female");
                    }*/
                    //  genderSpinner.setText(gender);
                  /*  state.contains("Male");*/

                  /*    Gender [] gender1=Gender.values();
                     for (Gender gen :gender1) {
                        String genders;
                        if (aadharDto.getGender() == 'M') {
                            genders = "Male";
                        } else if(aadharDto.getGender() == 'F') {
                            genders = "Female";
                        }else
                        {
                            genders="Others";
                        }
                        if (Gender.values()[i].equals(genders)){
                          paddyCategoryPosId = i;
                      }

                    }*/
                    // genderSpinner.setSelection(paddyCategoryPosId);


                 /*   for (int i = 0; i < mstatusList.size(); i++) {
                        String genders;

                        if (aadharDto.getGender() == 'M') {
                            genders = "male";
                        } else {
                            genders = "female";
                        }

                        if (mstatusList.get(i).toString().equals(genders)) {
                            paddyCategoryPosId = i;
                        }
                    }*/







                    //                scan_text.setText(""+uid+" "+name+" "+gender+" "+yob+" "+co+" "+house+" "+street+" "+lm+" "+loc+" "+vtc+" "+po);

                    String addr = "";
                    if (!house.equalsIgnoreCase("")) {
                        addr = house;
                    }
                    if (!street.equalsIgnoreCase("")) {
                        addr = addr + ", " + street;
                    }
                    if (!lm.equalsIgnoreCase("")) {
                        addr = addr + ", " + lm;
                    }
                    if (!loc.equalsIgnoreCase("")) {
                        addr = addr + ", " + loc;
                    }
                    if (!po.equalsIgnoreCase("")) {
                        addr = addr + ", " + po;
                    }
                    if (!subdist.equalsIgnoreCase("")) {
                        addr = addr + ", " + subdist;
                    }
                    if (!dist.equalsIgnoreCase("")) {
                        addr = addr + ", " + dist;
                    }
                    if (!state.equalsIgnoreCase("")) {
                        addr = addr + ", " + state;
                    }
                    if (!pc.equalsIgnoreCase("")) {
                        addr = addr + ", " + pc;
                    }
                    if (addr.startsWith(", ")) {
                        addr = addr.substring(2);
                    }
                    addr = addr + ".";
//                    addressTv.setText(addr);
                }
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }


    private void stringParsing(String text) {
        String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                state = "", pc = "", dob = "";
        String[] strArr = text.split(",");
        for (int i = 0; i < strArr.length; i++) {
            try {
                Log.e("AddFarmerActivity", "strArr contents" + strArr[i].toString());
                String element = strArr[i].toString();
                String[] strArr2 = element.split(":");

                if (strArr2[0].equalsIgnoreCase(" aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" DOB")) {
                    dob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("DOB")) {
                    dob = strArr2[1];
                }

            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("AddFarmerActivity", "string exception" + e);
            }
        }

        Log.e("THE AADHAR CARD NUMBER", "THE AADHAR " + uid);
        aadharDto.setAadhaarNum(Long.parseLong(uid));
        aadharDto.setUid(uid);
        aadharDto.setName(name);
        aadharDto.setGender(gender.charAt(0));
        aadharDto.setCo(co);
        aadharDto.setHouse(house);
        aadharDto.setStreet(street);
        aadharDto.setLm(lm);
        aadharDto.setLoc(loc);
        aadharDto.setVtc(vtc);
        aadharDto.setPo(po);
        aadharDto.setDist(dist);
        aadharDto.setSubdist(subdist);
        aadharDto.setState(state);
        aadharDto.setPc(pc);
        if (!yob.equalsIgnoreCase("")) {
            aadharDto.setYob(Long.parseLong(yob));
        }
        try {
            if (dob != null && !dob.isEmpty()) {
                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
//                            + " Time in millisec -> " + date1.getTime());
                    aadharDto.setDob(date1.getTime());
                } else {
//                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
//                    );
                }
            } else {
                aadharDto.setDob(null);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String date = sdf.format(gc.getTime());
            Date createdDate = sdf.parse(date);
            aadharDto.setCreatedDate(createdDate.getTime());

            Log.e("UIDValue", uid);
            Log.e("name", name);
            Log.e("gender", gender);
            Log.e("yob", yob);
            Log.e("co", co);
            Log.e("house", house);
            Log.e("street", street);
            Log.e("lm", lm);
            Log.e("loc", loc);
            Log.e("vtc", vtc);
            Log.e("po", po);
            Log.e("dist", dist);
            Log.e("subdist", subdist);
            Log.e("state", state);
            Log.e("pc", pc);
            Log.e(" dob", dob);


            String addr = "";
            if (!house.equalsIgnoreCase("")) {
                addr = house;
            }
            if (!street.equalsIgnoreCase("")) {
                addr = addr + ", " + street;
            }
            if (!lm.equalsIgnoreCase("")) {
                addr = addr + ", " + lm;
            }
            if (!loc.equalsIgnoreCase("")) {
                addr = addr + ", " + loc;
            }
            if (!po.equalsIgnoreCase("")) {
                addr = addr + ", " + po;
            }
            if (!subdist.equalsIgnoreCase("")) {
                addr = addr + ", " + subdist;
            }
            if (!dist.equalsIgnoreCase("")) {
                addr = addr + ", " + dist;
            }
            if (!state.equalsIgnoreCase("")) {
                addr = addr + ", " + state;
            }
            if (addr.startsWith(", ")) {
                addr = addr.substring(1);
            }
            if (!pc.equalsIgnoreCase("")) {
                addr = addr + ", " + pc;
            }
            if (addr.startsWith(", ")) {
                addr = addr.substring(2);
            }
            addr = addr + ".";

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

}