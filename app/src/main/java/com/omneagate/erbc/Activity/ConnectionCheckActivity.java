package com.omneagate.erbc.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.ConnectionCheckDto;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.ConnenctionChkResponseDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DiscomDto;
import com.omneagate.erbc.Dto.DistributionDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.RegionDto;
import com.omneagate.erbc.Dto.SectinoDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.StateDto;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user1 on 21/5/16.
 */
public class ConnectionCheckActivity extends BaseActivity {

    List<RegionDto> regionList;
    List<SectinoDto> sectinoList;
    List<DistributionDto> distribtionList;
    public static ConnectionDto connectioncheck;
    MaterialBetterSpinner region, section, distribution, state, discomm;
    ArrayAdapter<String> regionadapter, sectionAdapter, distributionAdapter;
    ArrayList<String> regionListArray = new ArrayList<String>();
    ArrayList<String> sectionListArray = new ArrayList<String>();
    ArrayList<String> distributionListArray = new ArrayList<String>();
    String regionStr, sectinonStr, distributionStr;
    String regionId;
    EditText connectionNumber1, connectionNumber2, connectionNumber3;
    Button submitBtn, nextBtn;
    CustomProgressDialog progressBar;
    String consumer_number;
    List<StateDto> statelist;
    List<DiscomDto> discomList;
    DiscomDto discomdto;
    private StateDto statedata;
    countryDto country;
    private List<String> discomArray;
    private boolean isEnglish = true;
    String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectioncheck);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        configureInitialPage();
        connectionNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                if (connectionNumber3.getText().toString().length() == 4) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(connectionNumber3.getWindowToken(), 0);
                }
            }
        });

        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    statedata = new StateDto();
                    statedata.setId(statelist.get(position).getId());
                    statedata.setName(state.getText().toString());
                    region.setText("");
                    discomm.setText("");
                    connectionNumber_Info(statedata);

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("Error", e.toString(), e);
                }
            }
        });

        region.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
        discomm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
        region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    regionStr = region.getText().toString();
                    regionId = regionList.get(position).getCode();
                    RegionDto regiondto = new RegionDto();
                    regiondto.setCode(regionList.get(position).getCode());
                    regiondto.setName(regionList.get(position).getName());
                    Log.e("regionid", "" + regionId);

                    regionclick(regiondto);

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("error", e.toString());
                }
            }
        });

        discomm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    discomdto = new DiscomDto();
                    discomdto.setId(discomList.get(position).getId());
                     ids =String.valueOf( discomList.get(position).getId());

                    region.setText("");



                    //discomdto.setName(discomList.get(position).getName());
                    discommClick(discomdto);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("discommerrror", e.toString());
                }
            }
        });

        connectionNumber1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String str = connectionNumber1.getText().toString();
                if (str.length() == 3) {
                    connectionNumber2.setFocusableInTouchMode(true);
                    connectionNumber2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        connectionNumber2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String str = connectionNumber2.getText().toString();
                if (str.length() == 3) {
                    connectionNumber3.setFocusableInTouchMode(true);
                    connectionNumber3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        connectionNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionNumber1.setCursorVisible(true);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Log.e("ConnectionCheck","State  :" + state.getText().toString());
                Log.e("ConnectionCheck","Discom :"+  discomm.getText().toString());
                Log.e("ConnectionCheck","Region :"+  region.getText().toString());


                if (StringUtils.isEmpty(state.getText().toString())) {
                    state.setError(getString(R.string.state_err));
                    return;
                }

                if (StringUtils.isEmpty(discomm.getText().toString())) {
                    discomm.setError(getString(R.string.discom_err));
                    return;
                }

                if (StringUtils.isEmpty(region.getText().toString())) {
                    region.setError(getString(R.string.regionerr));
                    return;
                }

                if (!validateconnectionNumber1()) {
                    return;
                }

                if (!validateconnectionNumber2()) {
                    return;
                }
                if (!validateconnectionNumber3()) {
                    return;
                }

                consumer_number = connectionNumber1.getText().toString() + connectionNumber2.getText().toString() + connectionNumber3.getText().toString();
                try {
                    JSONObject requestObject = new JSONObject();
                    requestObject.put("regionCode", "" + regionId);
                    requestObject.put("consumerNumber", consumer_number);
                 //   requestObject.put("discom",ids);
                 //  requestObject.put("",);
                    JSONObject stateObject = new JSONObject();
                    JSONObject discomObject=new JSONObject();
                    stateObject.put("id", statedata.getId());
                    discomObject.put("id",ids);

                    requestObject.put("state", stateObject);
                    requestObject.put("discom",discomObject);
                    Log.e("jsondata", "" + requestObject.toString());
                    checkAvaibilityClick(requestObject);
                } catch (JSONException e) {
                    GlobalAppState.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectioncheck = new ConnectionDto();
                CustomerDto customerdto = new CustomerDto();
                customerdto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
                connectioncheck.setCustomer(customerdto);
                connectioncheck.setRegionCode(regionId);
                connectioncheck.setConsumerNumber(consumer_number);
                Intent nextpage = new Intent(getApplicationContext(), ConnectionRegisterationActivity.class);
                nextpage.putExtra("regionId", "" + regionId);
                nextpage.putExtra("consumer_number", "" + consumer_number);
                nextpage.putExtra("discomId", new Gson().toJson(discomdto));
                nextpage.putExtra("stateId", new Gson().toJson(statedata));
                nextpage.putExtra("countryId", new Gson().toJson(country));
                startActivity(nextpage);
                finish();
            }
        });
    }

    private void configureInitialPage() {
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        state = (MaterialBetterSpinner) findViewById(R.id.statess);
        discomm = (MaterialBetterSpinner) findViewById(R.id.discomm);
        region = (MaterialBetterSpinner) findViewById(R.id.region);
        connectionNumber1 = (EditText) findViewById(R.id.cousumer_number1);
        connectionNumber2 = (EditText) findViewById(R.id.cousumer_number2);
        connectionNumber3 = (EditText) findViewById(R.id.cousumer_number3);
        submitBtn = (Button) findViewById(R.id.checkbrn);
        nextBtn = (Button) findViewById(R.id.nextbtn);
        country = new countryDto();
        country.setId(1);
        country.setName("India");
        if (Util.checkAppLanguage(ConnectionCheckActivity.this).equalsIgnoreCase("ta"))
            isEnglish = false;
        countryclick(country);
    }

    private void hideKeyboard() {
        InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.hideSoftInputFromWindow(connectionNumber1.getWindowToken(), 0);
        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(connectionNumber2.getWindowToken(), 0);
        InputMethodManager imm3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm3.hideSoftInputFromWindow(connectionNumber3.getWindowToken(), 0);
    }

    private boolean validateconnectionNumber1() {
        if (connectionNumber1.getText().toString().trim().isEmpty()) {
            connectionNumber2.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_red_back));
            connectionNumber3.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_red_back));
            connectionNumber1.requestFocus();
            connectionNumber1.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_red_back));

            Toast.makeText(getApplicationContext(), getString(R.string.consumernumber1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber1);
            return false;
        } else if (connectionNumber1.length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.consumer1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber1);
            return false;

        }
        return true;
    }

    private boolean validateconnectionNumber2() {
        if (connectionNumber2.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.consumernumber1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber2);
            return false;
        } else if (connectionNumber2.length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.consumer1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber2);
            return false;

        }
        return true;
    }

    private boolean validateconnectionNumber3() {
        if (connectionNumber3.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.consumernumber1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber3);
            return false;
        } else if (connectionNumber3.length() < 4) {
            Toast.makeText(getApplicationContext(), getString(R.string.consumer1_err), Toast.LENGTH_SHORT).show();
            requestFocus(connectionNumber3);
            return false;

        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void connectionNumber_Info(StateDto states) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/discom/getalldiscombystate";
                String login = new Gson().toJson(states);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                Log.e("stateDto", "" + states.toString());
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_DISCOM, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("connection_register", e.toString(), e);
        }
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case GETSTATE:
                dismissProgress();
                countryResponse(message);
                break;
            case GET_DISCOM:
                dismissProgress();
                discom_Response(message);
                break;
            case GET_REGION:
                dismissProgress();
                region_Response(message);
                break;
            case GET_CONNECTIONCHECK:
                dismissProgress();
                connectioncheck_Response(message);
                break;
            case GET_SECTION:
                selection_Response(message);
                break;
            case GET_DISTRIBUTION:
                distribution_Response(message);
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void discom_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto regiondata = gson.fromJson(response, ConnectionCheckDto.class);
            if (regiondata != null) {
                if (regiondata.getStatusCode() == 2103) {
                    Toast.makeText(getApplicationContext(),
                            regiondata.getErrorDescription(), Toast.LENGTH_LONG).show();
                    discomm.setText("");
                    discomm.setFocusable(false);
                    discomList.clear();
                } else {
                    discomList = regiondata.getDiscom();
                    Collections.sort(discomList, new Comparator<DiscomDto>() {
                                public int compare(DiscomDto lhs, DiscomDto rhs) {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            }
                    );
                    discomArray = new ArrayList<String>();
                    for (int i = 0; i < discomList.size(); i++) {
                        if (isEnglish) {
                            Log.e("","Name discom"+discomList.get(i).getName());
                            discomArray.add(discomList.get(i).getName());
                        }
                        else {
                            Log.e("","Regional Name discom"+discomList.get(i).getRegionalName());
                            discomArray.add(discomList.get(i).getRegionalName());
                        }
                    }
                    //Collections.sort(discomArray, String.CASE_INSENSITIVE_ORDER);
                    /*Collections.sort(discomArray, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });*/
                    ArrayAdapter<String> discomTypeAdpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, discomArray);
                    discomm.setAdapter(discomTypeAdpter);
                    discomm.setFocusable(true);
                }

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }

    private void regionclick(RegionDto region) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/section/getallsectionbyregion";
                String login = new Gson().toJson(region);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_SECTION, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }


    }


    private void selection_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto regiondata = gson.fromJson(response, ConnectionCheckDto.class);
            if (regiondata != null) {

                sectinoList = regiondata.getSections();
                sectionListArray.clear();

                for (int i = 0; i < sectinoList.size(); i++) {
                    if(isEnglish) {
                        sectionListArray.add(sectinoList.get(i).getName() + " - " + sectinoList.get(i).getName());
                    }else{
                        sectionListArray.add(sectinoList.get(i).getName() + " - " + sectinoList.get(i).getRegionalName());
                    }
                }
                sectionAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sectionListArray);
                section.setAdapter(sectionAdapter);
                section.setFocusable(true);
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }

    }


    private void sectionclick(SectinoDto selction) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/distribution/getalldistributionbysection";
                String login = new Gson().toJson(selction);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_SECTION, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void distribution_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto regiondata = gson.fromJson(response, ConnectionCheckDto.class);
            if (regiondata != null) {
                distribtionList = regiondata.getDistributions();
            }
            distributionListArray.clear();
            for (int i = 0; i < distribtionList.size(); i++) {
                if(isEnglish) {
                    distributionListArray.add(distribtionList.get(i).getName() + " - " + distribtionList.get(i).getName());
                }else{
                    distributionListArray.add(distribtionList.get(i).getName() + " - " + distribtionList.get(i).getRegionalName());
                }
            }
            distributionAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, distributionListArray);
            distribution.setAdapter(distributionAdapter);
            distribution.setFocusable(true);


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void checkAvaibilityClick(JSONObject jsondata) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/checkconsumer";
                String login = new Gson().toJson(jsondata);
                StringEntity se = new StringEntity(jsondata.toString(), HTTP.UTF_8);
                Log.e("login", "" + se.toString());
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_CONNECTIONCHECK, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void connectioncheck_Response(Bundle message) {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnenctionChkResponseDto ConnenctionResponseDto = gson.fromJson(response, ConnenctionChkResponseDto.class);
            if (ConnenctionResponseDto != null) {
                if (ConnenctionResponseDto.getStatusCode() == 3102) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    connectionNumber1.setFocusable(false);
                    connectionNumber2.setFocusable(false);
                    connectionNumber3.setFocusable(false);
                    region.setAdapter(null);
                    state.setAdapter(null);
                    discomm.setAdapter(null);
                    region.setFocusable(false);
                    state.setFocusable(false);
                    discomm.setFocusable(false);
                    if(isEnglish) {
                        ((EditText) findViewById(R.id.sectiontxt)).setText(
                                ConnenctionResponseDto.getMaster().getSection().getCode()
                                        + " - " + ConnenctionResponseDto.getMaster().getSection().getName());
                        ((EditText) findViewById(R.id.distrubuton)).setText(ConnenctionResponseDto.getMaster().getDistribution().getCode()
                                + " - " + ConnenctionResponseDto.getMaster().getDistribution().getName());
                    }else{
                        ((EditText) findViewById(R.id.sectiontxt)).setText(
                                ConnenctionResponseDto.getMaster().getSection().getCode()
                                        + " - " + ConnenctionResponseDto.getMaster().getSection().getRegionalName());
                        ((EditText) findViewById(R.id.distrubuton)).setText(ConnenctionResponseDto.getMaster().getDistribution().getCode()
                                + " - " + ConnenctionResponseDto.getMaster().getDistribution().getRegionalName());
                    }
                    ((TextInputLayout) findViewById(R.id.lay_section)).setVisibility(View.VISIBLE);
                    ((TextInputLayout) findViewById(R.id.lay_distrib)).setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                    connectionNumber1.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_popup_border));
                    connectionNumber2.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_popup_border));
                    connectionNumber3.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_popup_border));
                } else if (ConnenctionResponseDto.getStatusCode() == 0) {
                    Intent pagedetail = new Intent(ConnectionCheckActivity.this, ConnectionDetailActivity.class);
                    pagedetail.putExtra("connenctionDto", new Gson().toJson(ConnenctionResponseDto.getConnection()));
                    pagedetail.putExtra("flag", "0");
                    startActivity(pagedetail);
                    finish();
                } else if (ConnenctionResponseDto.getStatusCode() == 2101) {
                    //region not found
                    connectionNumber1.setText("");
                    connectionNumber2.setText("");
                    connectionNumber3.setText("");
                    connectionNumber1.requestFocus();
                    connectionNumber1.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_red_back));

                    Toast.makeText(getApplicationContext(),
                            "" + getString(R.string.section_not_found), Toast.LENGTH_SHORT).show();
                } else if (ConnenctionResponseDto.getStatusCode() == 2102) {
                    //distribution not found
                    connectionNumber2.setText("");
                    connectionNumber2.requestFocus();
                    connectionNumber2.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_red_back));
                    Toast.makeText(getApplicationContext(),
                            "" + getString(R.string.distribution_not_found), Toast.LENGTH_SHORT).show();
                } else if (ConnenctionResponseDto.getErrorDescription() != null) {
                    Toast.makeText(getApplicationContext(), "" + ConnenctionResponseDto.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "" + getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Exception", e.toString(), e);
        }
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
            Log.e("consumerfragment", e.toString(), e);
        }

    }

    private void countryResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto stateResponse = gson.fromJson(response, personalDto.class);
            if (stateResponse != null) {
                statelist = stateResponse.getStates();
                ArrayList<String> stateArray = new ArrayList<String>();
                if (statelist.size() != 0) {

                    for (int i = 0; i < statelist.size(); i++) {
                        if (isEnglish) {
                            stateArray.add(statelist.get(i).getName());
                        } else {
                            stateArray.add(statelist.get(i).getRegionalName());
                        }
                    }
                    ArrayAdapter<String> stateadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdownrow, stateArray);
                    state.setAdapter(stateadapter);
                    state.setFocusable(true);
                }
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception_country", e.toString());
        }

    }

    private void discommClick(DiscomDto discomm) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/region/getallregionbydiscom";
                String login = new Gson().toJson(discomm);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_REGION, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("consumerfragment", e.toString(), e);
        }
    }

    private void region_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto regiondata = gson.fromJson(response, ConnectionCheckDto.class);
            if (regiondata != null) {
                if (regiondata.getStatusCode() == 2100) {
                    Toast.makeText(getApplicationContext(), regiondata.getErrorDescription(), Toast.LENGTH_LONG).show();
                    region.setText("");
                    region.setAdapter(null);
                    regionListArray.clear();
                    region.setFocusable(false);


                } else {
                    regionList = regiondata.getRegions();
                    regionListArray.clear();
                    for (int i = 0; i < regionList.size(); i++) {
                      if(isEnglish) {
                          regionListArray.add(regionList.get(i).getCode()
                                  + " - " + regionList.get(i).getName());
                      }else{
                          regionListArray.add(regionList.get(i).getCode()
                                  + " - " + regionList.get(i).getRegionalName());
                      }
                    }
                    region.setFocusable(true);
                }
                regionadapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.dropdownrow, regionListArray);
                region.setAdapter(regionadapter);

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString(), e);
        }
    }


}
