package com.omneagate.erbc.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
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
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.CalendarDialog;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DistrictDto;
import com.omneagate.erbc.Dto.EnumDto.Gender;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by user1 on 13/5/16.
 */
public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    TextView personalheader_txt, communiheader_txt;
    LinearLayout personal_tab, communi_tab, personal_view, communi_view;
    Button nextBtn, submitBtn;
    LoginResponseDto loginResponse;
    EditText firstName, middleName, lastName, mobilenumber, datepicker, email;
    MaterialBetterSpinner genderSpinner, occuaptionSpinner, statusSpinner;
    TextInputLayout firstanameLay, middlenameLay, lastnameLay, mobilenumberLay, dobLay, emailLay;
    TextInputLayout address1Lay, address2Lay, villageLay, pincodeLay;
    MaterialBetterSpinner taluk, district, state, country1, village;
    EditText address1, address2, pincode;
    CustomerDto customerInfo;
    private int mYear, mMonth, mDay, mHour, mMinute;
    List<MaritalStatusDto> mstatusList;
    List<OccupationDto> occupationList;
    String gendername, occuaption_name, mstatusname;
    int occuaption_id, mstatus_id;
    Gender gender_select;
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
    private static final String TAG = RegisterationActivity.class.getName();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CalendarDialog alertdialog;
    CustomerDto customerRecord;
    private LinearLayout scannerLayout;
    String months[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};

    //private String blockCharacterSet = "@#$%&+=()*\":;?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerationactivity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        configureInitialPage();
        setCustomerValue();
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

                stateName = state.getText().toString();
                state_id = statelist.get(position).getId();
                StateDto statedata = new StateDto();
                statedata.setId(statelist.get(position).getId());
                statedata.setName(state.getText().toString());
                district.setText("");
                taluk.setText("");
                village.setText("");
                pincode.setText("");
                district.setFocusable(true);
                stateClick(statedata);

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

        genderSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    gender_select = (Gender) parent.getItemAtPosition(position);

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("error meessage", e.toString(), e);
                }
            }
        });
    }

    private void configureInitialPage() {

        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
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
        personaldetail_Info();
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
        middlenameLay = (TextInputLayout) findViewById(R.id.layout_middlename);
        lastnameLay = (TextInputLayout) findViewById(R.id.layout_lastname);
        mobilenumberLay = (TextInputLayout) findViewById(R.id.layout_mobilenumber);
        dobLay = (TextInputLayout) findViewById(R.id.layout_dob);
        emailLay = (TextInputLayout) findViewById(R.id.layout_email);
        firstName = (EditText) findViewById(R.id.firstname);
        middleName = (EditText) findViewById(R.id.middlename);
        lastName = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        mobilenumber = (EditText) findViewById(R.id.mobilenumber);
       // mobilenumber.setEnabled(false);
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
        scannerLayout=(LinearLayout)findViewById(R.id.scanadhaar);
        scannerLayout.setVisibility(View.GONE);
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


        /*address1.setFilters(new InputFilter[]{Util.inputFilter(blockCharacterSet)});
        address2.setFilters(new InputFilter[]{Util.inputFilter(blockCharacterSet)});*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent registerPage = new Intent(EditProfileActivity.this, MyProfileActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();
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
                // Get Current Date
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
        }
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
            progressBar = new CustomProgressDialog(EditProfileActivity.this);
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
                        mstatusArray.add(mstatusList.get(i).getName());
                    }
                    Collections.sort(occupationList, new Comparator<OccupationDto>() {
                        @Override
                        public int compare(OccupationDto occupationDto, OccupationDto t1) {
                            return occupationDto.getName().compareTo(t1.getName());
                        }
                    });
                    ArrayList<String> occupationArray = new ArrayList<String>();
                    for (int i = 0; i < occupationList.size(); i++) {
                        occupationArray.add(occupationList.get(i).getName());
                    }
                    ArrayAdapter<Gender> genderAdpter = new ArrayAdapter<Gender>(getApplicationContext(), R.layout.dropdownrow, Gender.values());
                    ArrayAdapter<String> statusAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, mstatusArray);
                    ArrayAdapter<String> occpationAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, occupationArray);
                    genderSpinner.setAdapter(genderAdpter);
                    statusSpinner.setAdapter(statusAdpter);
                    occuaptionSpinner.setAdapter(occpationAdpter);
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

          /*  if(yourAge<=17)
            {
               personal_header();
                dobLay.setError("Minimum age should be more than 18");
                datepicker.setText("");
                //requestFocus(datepicker);
                return ;
            }*/

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
            }
*/
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
            if (!validateAddress1()) {
                return;
            }
            if (!validateAddress2()) {
                return;
            }
            if (!validateMobileNumber()) {
                return;
            }
            CustomerDto customer_details = getCommunicationDetails();
            if (pincode.getText().toString().trim().isEmpty()) {
                return;
            } else {
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
        String moible_str = mobilenumber.getText().toString();
        String dob_str = datepicker.getText().toString();
        customerInfo.setFirstName(first_str);
        customerInfo.setMiddleName(middle_str);
        customerInfo.setLastName(last_str);
        customerInfo.setMobileNumber(moible_str);
        customerInfo.setEmail(email_str);
        customerInfo.setDob(dob_str);
        customerInfo.setGender(gender_select);
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
        customerInfo.setId(customerRecord.getId());
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
            stateArray.add(statelist.get(i).getName());
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
            // Toast.makeText(getActivity(),"Districts not found.",Toast.LENGTH_LONG).show();
        }
        districtArray.clear();
        for (int i = 0; i < districtList.size(); i++) {
            districtArray.add(districtList.get(i).getName());
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
            talukListArray.add(talukList.get(i).getName());
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
            villageListArray.add(villageList.get(i).getName());
        }
        villageAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, villageListArray);
        village.setAdapter(villageAdapter);
    }


    private boolean validatePincode() {
        if (pincode.getText().toString().trim().isEmpty()) {
            pincodeLay.setError(getString(R.string.pincode_err));
            requestFocus(pincode);
            return false;
        } else if (pincode.length() < 6) {
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

            progressBar = new CustomProgressDialog(EditProfileActivity.this);
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
                    DBHelper.getInstance(getApplicationContext()).insertCustomer(loginResponse.getCustomerDto2());
                    AlertDialog alertdialog = new AlertDialog(EditProfileActivity.this, getString(R.string.profileupdate));
                    alertdialog.show();
                } else if (loginResponse.getStatusCode() == 2025) {
                    personal_header();
                    dobLay.setError("Minimum age should be more than 18");
                    datepicker.setText("");
                    requestFocus(datepicker);
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else if (loginResponse.getStatusCode() == 5) {
                    Toast.makeText(getApplicationContext(), "" + loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("customer_reg_error", e.toString(), e);
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
                break;
        }
    }

    private void setCustomerValue() {
        try {
            customerRecord = DBHelper.getInstance(getApplicationContext()).getcustomerData();
            countryclick(customerRecord.getCountry());
            stateClick(customerRecord.getState());
            districtClick(customerRecord.getDistrict());
            talukClick(customerRecord.getTaluk());
            Log.e("customerRecord", "" + customerRecord.toString());
            if (customerRecord != null) {
                firstName.setText("" + customerRecord.getFirstName());
                middleName.setText("" + customerRecord.getMiddleName());
                lastName.setText("" + customerRecord.getLastName());
                mobilenumber.setText(customerRecord.getMobileNumber());
                email.setText("" + customerRecord.getEmail());

                datepicker.setText("" + customerRecord.getDob());

                genderSpinner.setText("" + customerRecord.getGender());
                gender_select = customerRecord.getGender();
                if (customerRecord.getOccupation() != null) {
                    if (customerRecord.getOccupation().getName() != null) {
                        occuaption_id = customerRecord.getOccupation().getId();
                        occuaption_name = customerRecord.getOccupation().getName();
                        occuaptionSpinner.setText("" + customerRecord.getOccupation().getName());
                    } else {
                        occuaptionSpinner.setText("----");
                    }
                } else {
                    occuaptionSpinner.setText("----");
                }

                if (customerRecord.getMaritalStatus() != null) {
                    if (customerRecord.getMaritalStatus().getName() != null) {
                        mstatus_id = customerRecord.getMaritalStatus().getId();
                        mstatusname = customerRecord.getMaritalStatus().getName();
                        statusSpinner.setText("" + customerRecord.getMaritalStatus().getName());
                    } else {
                        statusSpinner.setText("----");
                    }
                } else {
                    statusSpinner.setText("----");
                }

                //communication dateils
                address1.setText("" + customerRecord.getAddressLine1());
                address2.setText("" + customerRecord.getAddressLine2());
                country1.setText("" + customerRecord.getCountry().getName());
                state.setText("" + customerRecord.getState().getName());
                district.setText(customerRecord.getDistrict().getName());
                taluk.setText("" + customerRecord.getTaluk().getName());
                village.setText("" + customerRecord.getVillage().getName());
                pincode.setText("" + customerRecord.getPinCode());
                country_id = customerRecord.getCountry().getId();
                countryName = customerRecord.getCountry().getName();
                state_id = customerRecord.getState().getId();
                stateName = customerRecord.getState().getName();
                district_id = customerRecord.getDistrict().getId();
                districtName = customerRecord.getDistrict().getName();
                taluk_id = customerRecord.getTaluk().getId();
                talukName = customerRecord.getTaluk().getName();
                village_id = customerRecord.getVillage().getId();
                villageName = customerRecord.getVillage().getName();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString());
        }
    }
}


