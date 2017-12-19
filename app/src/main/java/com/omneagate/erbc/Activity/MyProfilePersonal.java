package com.omneagate.erbc.Activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
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
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.Gender;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.MaritalStatusDto;
import com.omneagate.erbc.Dto.OccupationDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.personalDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 13/5/16.
 */
public class MyProfilePersonal extends BaseActivityFragment {
    View view;
    EditText firstName,middleName,lastName,mobilenumber,datepicker,email;
    MaterialBetterSpinner genderSpinner,occuaptionSpinner,statusSpinner;
    TextInputLayout firstanameLay,middlenameLay,lastnameLay,mobilenumberLay,dobLay,emailLay;
    List<MaritalStatusDto> mstatusList;
    List<OccupationDto> occupationList;
    public  static CustomerDto customerInfo;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    int occuaption_id,mstatus_id;
    String occuaption_name,mstatusname;
    Gender gender_select;
    CustomerDto customerRecord;
    public MyProfilePersonal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkConnection = new NetworkConnection(getActivity());
        httpConnection = new HttpClientWrapper();
        personaldetail_Info();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.personal_info, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        firstanameLay = (TextInputLayout)view.findViewById(R.id.layout_firstname);
        middlenameLay = (TextInputLayout)view.findViewById(R.id.layout_middlename);
        lastnameLay = (TextInputLayout)view.findViewById(R.id.layout_lastname);
        mobilenumberLay = (TextInputLayout)view.findViewById(R.id.layout_mobilenumber);
        dobLay = (TextInputLayout)view.findViewById(R.id.layout_dob);
        emailLay = (TextInputLayout)view.findViewById(R.id.layout_email);
        firstName = (EditText) view.findViewById(R.id.firstname);
        middleName = (EditText) view.findViewById(R.id.middlename);
        lastName = (EditText) view.findViewById(R.id.lastname);
        email = (EditText) view.findViewById(R.id.email);
        email.setEnabled(false);
        mobilenumber = (EditText) view.findViewById(R.id.mobilenumber);
        mobilenumber.setEnabled(false);
        datepicker =  (EditText)view.findViewById(R.id.dates);
        datepicker.setShowSoftInputOnFocus(false);
        Button nextBtn = (Button) view.findViewById(R.id.nextBtn);
        genderSpinner = (MaterialBetterSpinner)view.findViewById(R.id.genderid);
        occuaptionSpinner = (MaterialBetterSpinner)view.findViewById(R.id.occupation);
        statusSpinner = (MaterialBetterSpinner)view.findViewById(R.id.mstatus);
        setCustomerValue();
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        genderSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender_select = (Gender)parent.getItemAtPosition(position);
            }
        });
        occuaptionSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                occuaption_name = occuaptionSpinner.getText().toString();
                occuaption_id = occupationList.get(position).getId();
            }
        });
        statusSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mstatusname = statusSpinner.getText().toString();
                mstatus_id = mstatusList.get(position).getId();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!validateFirstName()) {
                        return;
                    }
                  /*  if (!validateMiddleName()) {
                        return;
                    }
*/
                    if (!validateLastName()) {
                        return;
                    }

                    if (!validateMobileNumber()) {
                        return;
                    }

                    if (!validateEmailid()) {
                        return;
                    }

                    if (!validateDOB()) {
                        return;
                    }
                    String gender_str = genderSpinner.getText().toString();
                    String occupation_str = occuaptionSpinner.getText().toString();
                    String status_str = statusSpinner.getText().toString();

                    if (StringUtils.isEmpty(gender_str)) {
                        genderSpinner.setError(getString(R.string.gender_err));
                        return ;
                    }
                  /*  if (StringUtils.isEmpty(occupation_str)) {
                        occuaptionSpinner.setError(getString(R.string.occupation_err));
                        return ;
                    }
                    if (StringUtils.isEmpty(status_str)) {
                        statusSpinner.setError(getString(R.string.marital_err));
                        return ;
                    }*/
                    CustomerDto customer_details = getpersonalDetails();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_place, new MyProfileCommunication())
                            .commit();


                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("personalActivity", e.toString(), e);
                }


            }
        });
        return view;
    }

    private void  setCustomerValue()
    {
        try{
              customerRecord = DBHelper.getInstance(getActivity()).getcustomerData();
              Log.e("customerRecord",""+customerRecord.toString());
              if(customerRecord != null)
              {
                firstName.setText(""+customerRecord.getFirstName());
                middleName.setText(""+customerRecord.getMiddleName());
                lastName.setText(""+customerRecord.getLastName());
                mobilenumber.setText(customerRecord.getMobileNumber());
                email.setText(""+customerRecord.getEmail());
                occuaptionSpinner.setText(""+customerRecord.getOccupation().getName());
                datepicker.setText(""+customerRecord.getDob());
                statusSpinner.setText(""+customerRecord.getMaritalStatus().getName());
                genderSpinner.setText(""+customerRecord.getGender());
                gender_select = customerRecord.getGender();
                occuaption_id = customerRecord.getOccupation().getId();
                occuaption_name = customerRecord.getOccupation().getName();
                mstatus_id = customerRecord.getMaritalStatus().getId();
                mstatusname = customerRecord.getMaritalStatus().getName();
              }
        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error",e.toString());
        }
    }

    private void personaldetail_Info() {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/personaldetails";
                httpConnection.sendRequest(url, null, ServiceListenerType.PERSONAL_INFO,SyncHandler, RequestType.GET, null, getActivity());
            } else {
                Toast.makeText(getActivity(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
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
            case PERSONAL_INFO:
                personal_Response(message);
                break;
            default:
                Toast.makeText(getActivity(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void personal_Response(Bundle message) {

        try
        {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("personal_Response", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            personalDto personalResponse;
            personalResponse = gson.fromJson(response, personalDto.class);
            if (personalResponse != null) {
                if (personalResponse.getStatusCode() == 0) {

                    DBHelper.getInstance(getActivity()).insertCountry(personalResponse.getCountry());
                    mstatusList = personalResponse.getMaritalStatus();
                    occupationList = personalResponse.getOccupation();
                    ArrayList<String> mstatusArray = new ArrayList<String>();
                    for(int i=0;i<mstatusList.size();i++)
                    {
                        mstatusArray.add(mstatusList.get(i).getName());
                    }
                    ArrayList<String> occupationArray = new ArrayList<String>();
                    for(int i=0;i<occupationList.size();i++)
                    {
                        occupationArray.add(occupationList.get(i).getName());
                    }
                    ArrayAdapter<Gender> genderAdpter = new ArrayAdapter<Gender>(getActivity(),android.R.layout.simple_dropdown_item_1line, Gender.values());
                    ArrayAdapter<String> statusAdpter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, mstatusArray);
                    ArrayAdapter<String> occpationAdpter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, occupationArray);
                    genderSpinner.setAdapter(genderAdpter);
                    statusSpinner.setAdapter(statusAdpter);
                    occuaptionSpinner.setAdapter(occpationAdpter);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error",e.toString(),e);
        }
    }

    private boolean validateFirstName() {
        if (firstName.getText().toString().trim().isEmpty()) {
            firstanameLay.setError(getString(R.string.firstname_err));
            requestFocus(firstName);
            return false;
        } else {
            firstanameLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMiddleName() {
        if (middleName.getText().toString().trim().isEmpty()) {
            middlenameLay.setError(getString(R.string.middlename_err));
            requestFocus(middleName);
            return false;
        } else {
            middlenameLay.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (lastName.getText().toString().trim().isEmpty()) {
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
            mobilenumberLay.setError(getString(R.string.mobilenumber_err));
            requestFocus(mobilenumber);
            return false;
        } else if(mobilenumber.length()<10){
            mobilenumberLay.setError(getString(R.string.mobile_err));
            requestFocus(mobilenumber);
            return false;

        }  else
        {
            mobilenumberLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmailid() {
        if (email.getText().toString().trim().isEmpty()) {
            emailLay.setError(getString(R.string.email_err));
            requestFocus(email);
            return false;
        } else if(!email.getText().toString().matches(emailPattern)) {
            emailLay.setError(getString(R.string.emailinvalid_err));
            requestFocus(email);
            return false;
        }
        else
        {
            emailLay.setErrorEnabled(false);
        }
        return true;
    }



    private boolean validateDOB() {
        if (datepicker.getText().toString().trim().isEmpty()) {
            dobLay.setError(getString(R.string.dob_err));
            requestFocus(datepicker);
            return false;
        } else {
            dobLay.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private CustomerDto getpersonalDetails() {

        customerInfo = new CustomerDto();
        String first_str = firstName.getText().toString();
        String middle_str = middleName.getText().toString();
        String last_str = lastName.getText().toString();
        String email_str= email.getText().toString();
        String moible_str= mobilenumber.getText().toString();
        String dob_str = datepicker.getText().toString();
        customerInfo.setId(customerRecord.getId());
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
        Log.e("customerInfo_personal",""+customerInfo.toString());
        return customerInfo;
    }
}
