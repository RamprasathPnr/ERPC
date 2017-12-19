package com.omneagate.erbc.Activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.DistrictDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.LoginResponseDto;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 13/5/16.
 */
public class MyProfileCommunication extends BaseActivityFragment {

    TextInputLayout address1Lay,address2Lay,villageLay,pincodeLay;
    MaterialBetterSpinner taluk,district,state,country1,village;
    EditText address1,address2,pincode;
    CustomerDto customerInfo;
    CustomProgressDialog progressBar;
    LoginResponseDto loginResponse;
    List<countryDto> countryList;
    String countryName,stateName,districtName,talukName,villageName;
    int country_id,state_id,district_id,taluk_id,village_id;
    List<StateDto> statelist;
    List<DistrictDto> districtList;
    List<TalukDto> talukList;
    List<VillageDto> villageList;
    ArrayAdapter<String> stateadapter,villageAdapter,talukAdapter,districtAdapter;
    ArrayList<String> districtArray = new ArrayList<String>();
    ArrayList<String> talukListArray = new ArrayList<String>();
    ArrayList<String> villageListArray = new ArrayList<String>();
    Button submitBtn;
    CustomerDto customerRecord = DBHelper.getInstance(getActivity()).getcustomerData();

    public MyProfileCommunication() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.communication_info, container, false);
        configureInitialPage(view);

        return view;
    }

    private void configureInitialPage(View view) {


        networkConnection = new NetworkConnection(getActivity());
        httpConnection = new HttpClientWrapper();
        countryList = DBHelper.getInstance(getActivity()).getCountryList();
        ArrayList<String> countryArray = new ArrayList<String>();
        for(int i=0;i<countryList.size();i++)
        {
            countryArray.add(countryList.get(i).getName());
        }
        ArrayAdapter<String> countryadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, countryArray);
        address1Lay = (TextInputLayout)view.findViewById(R.id.layout_address1);
        address2Lay = (TextInputLayout)view.findViewById(R.id.layout_address2);
        pincodeLay = (TextInputLayout)view.findViewById(R.id.layout_pincode);
        address1 = (EditText) view.findViewById(R.id.address1);
        address2 = (EditText) view.findViewById(R.id.address2);
        village = (MaterialBetterSpinner) view.findViewById(R.id.village);
        pincode = (EditText) view.findViewById(R.id.pincode);
        submitBtn = (Button) view.findViewById(R.id.button);
        taluk = (MaterialBetterSpinner)view.findViewById(R.id.taluk);
        district = (MaterialBetterSpinner)view.findViewById(R.id.district);
        state = (MaterialBetterSpinner)view.findViewById(R.id.state);
        country1 = (MaterialBetterSpinner)view.findViewById(R.id.country);
        country1.setAdapter(countryadapter);
        setCustomerValue();
        countryclick(customerRecord.getCountry());
        stateClick(customerRecord.getState());
        districtClick(customerRecord.getDistrict());
        talukClick(customerRecord.getTaluk());

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
                }catch(Exception e){
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("error_district",e.toString());
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
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!validateAddress1()) {
                        return;
                    }
                    if (!validateAddress2()) {
                        return;
                    }

                    if (!validatePincode()) {
                        return;
                    }
                    CustomerDto customer_details = getCommunicationDetails();
                    registerUser(customer_details);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("communicationActivity", e.toString(), e);
                }
            }
        });

}


    private void  setCustomerValue()
    {
        try{
           // CustomerDto customerRecord = DBHelper.getInstance(getActivity()).getcustomerData();
            Log.e("customerRecord",""+customerRecord.toString());
            if(customerRecord != null)
            {
                address1.setText(""+customerRecord.getAddressLine1());
                address2.setText(""+customerRecord.getAddressLine2());
                country1.setText(""+customerRecord.getCountry().getName());
                state.setText(""+customerRecord.getState().getName());
                district.setText(customerRecord.getDistrict().getName());
                taluk.setText(""+customerRecord.getTaluk().getName());
                village.setText(""+customerRecord.getVillage().getName());
                pincode.setText(""+customerRecord.getPinCode());
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
        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error",e.toString());
        }
    }



    private CustomerDto getCommunicationDetails() {

        customerInfo = new CustomerDto();
        customerInfo = MyProfilePersonal.customerInfo;
        String address1_str = address1.getText().toString();
        String address2_str = address2.getText().toString();
        String village_str = village.getText().toString();
        String pincode_str= pincode.getText().toString();
        String taluk_str = taluk.getText().toString();
        String district_str = district.getText().toString();
        String state_str = state.getText().toString();
        String country_str = country1.getText().toString();

        if (StringUtils.isEmpty(country_str)) {
            country1.setError(getString(R.string.country_err));
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
        if (StringUtils.isEmpty(state_str)) {
            state.setError(getString(R.string.state_err));
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
        customerInfo.setCountryCode("91");
        StateDto state = new StateDto();
        state.setId(state_id);
        state.setName(stateName);
        customerInfo.setState(state);
        customerInfo.setPinCode(pincode_str);
        Log.e("customerInfo",""+customerInfo.toString());
        return customerInfo;
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

    private boolean validatePincode() {
        if (pincode.getText().toString().trim().isEmpty()) {
            pincodeLay.setError(getString(R.string.pincode_err));
            requestFocus(pincode);
            return false;
        } else if (pincode.length()<6)
        {
            pincodeLay.setError(getString(R.string.pinvalid_err));
            requestFocus(pincode);
            return false;
        }else {
            pincodeLay.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void registerUser(CustomerDto customerdto) {

        try {
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/add";
                String login = new Gson().toJson(customerdto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.REGISTER_USER,SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Util.messageBar(getActivity(), getString(R.string.connectionRefused));
            }
        }
        catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
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
                Toast.makeText(getActivity(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

    private void registerResponse(Bundle message) {
        try
        {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            loginResponse = gson.fromJson(response, LoginResponseDto.class);
            if (loginResponse != null) {
                if (loginResponse.getStatusCode() == 0 && loginResponse.getCustomerDto2()!= null)
                {
                    DBHelper.getInstance(getActivity()).insertCustomer(loginResponse.getCustomerDto2());
                    AlertDialog alertdialog = new AlertDialog(getActivity(),getString(R.string.profileupdate));
                    alertdialog.show();
                }
                else
                {
                    Toast.makeText(getActivity(),""+loginResponse.getErrorDescription(),Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getActivity(),getString(R.string.connectionError),Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e("myprofile_error",""+e.toString(),e);
        }

    }

    private  void  countryclick(countryDto countrydata)
    {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/state/getallbycountry";
                String login = new Gson().toJson(countrydata);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GETSTATE,SyncHandler, RequestType.POST, se, getActivity());
            } else {
                Toast.makeText(getActivity(),getString(R.string.connectionError),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void countryResponse(Bundle message)
    {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto stateResponse = gson.fromJson(response, personalDto.class);
        if(stateResponse!=null)
        {
            statelist = stateResponse.getStates();
        }
        ArrayList<String> stateArray = new ArrayList<String>();
        for(int i=0;i<statelist.size();i++)
        {
            stateArray.add(statelist.get(i).getName());
        }
        stateadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, stateArray);
        state.setAdapter(stateadapter);
    }

    private void stateClick(StateDto states)
    {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/district/getallbystate";
                String login = new Gson().toJson(states);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_DISTRICT,SyncHandler, RequestType.POST, se, getActivity());
            } else {
                Toast.makeText(getActivity(),getString(R.string.connectionError),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void stateResponse(Bundle message)
    {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto districtResponse = gson.fromJson(response, personalDto.class);
        if(districtResponse!=null)
        {
            districtList = districtResponse.getDistricts();
        }
        districtArray.clear();
        for(int i=0;i<districtList.size();i++)
        {
            districtArray.add(districtList.get(i).getName());
        }
        districtAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, districtArray);
        district.setAdapter(districtAdapter);
    }

    private void districtClick(DistrictDto district)
    {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/taluk/getallbydistrict";
                String login = new Gson().toJson(district);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_TALUK,SyncHandler, RequestType.POST, se, getActivity());
            } else {
                Toast.makeText(getActivity(),getString(R.string.connectionError),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void districtResponse(Bundle message)
    {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto districtResponse = gson.fromJson(response, personalDto.class);
        if(districtResponse!=null)
        {
            talukList = districtResponse.getTaluks();
        }
        talukListArray.clear();
        for(int i=0;i<talukList.size();i++)
        {
            talukListArray.add(talukList.get(i).getName());
        }
        talukAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, talukListArray);
        taluk.setAdapter(talukAdapter);
    }

    private void talukClick(TalukDto talukdto)
    {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/village/getallbytaluk";
                String login = new Gson().toJson(talukdto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.GET_VILLAGE,SyncHandler, RequestType.POST, se, getActivity());
            } else {
                Toast.makeText(getActivity(),getString(R.string.connectionError),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void talukResponse(Bundle message)
    {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        personalDto villageResponse = gson.fromJson(response, personalDto.class);
        if(villageResponse!=null)
        {
            villageList = villageResponse.getVillages();
        }
        villageListArray.clear();
        for(int i=0;i<villageList.size();i++)
        {
            villageListArray.add(villageList.get(i).getName());
        }
        villageAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, villageListArray);
        village.setAdapter(villageAdapter);
    }

}
