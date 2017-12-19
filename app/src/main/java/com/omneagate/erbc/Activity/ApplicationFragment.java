package com.omneagate.erbc.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omneagate.erbc.R;

/**
 * Created by ftuser on 24/10/16.
 */


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.FeedbackDialog;
import com.omneagate.erbc.Dto.CityDto;
import com.omneagate.erbc.Dto.ConnectionCheckDto;
import com.omneagate.erbc.Dto.ConnectionListDto;
import com.omneagate.erbc.Dto.EnumDto.GrievanceStatus;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GenericDto;
import com.omneagate.erbc.Dto.GrievanceDto;
import com.omneagate.erbc.Dto.GrievanceType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ftuser on 24/10/16.
 */
public class ApplicationFragment extends Fragment {
    CustomProgressDialog progressBar;
    private static final String TAG = ComplaintsRegisterActivity.class.getName();
    List<GenericDto> categoryList = new ArrayList<>();
    List<GenericDto> subcategoryList = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter, subcategoryAdapter;
    MaterialBetterSpinner category, sub_category, connectionType;
    int category_id, subcategory_id;
    Button submitBtn,cancelBtn;
    EditText summary;
    private List<CityDto> connectionList;
    private ArrayAdapter<String> connectionAdapter;
    private String connectionId = "";
    private NetworkConnection networkConnection;
    private HttpClientWrapper httpConnection;
    View v;

    public ApplicationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.bill_griev));
*/
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        try {
            v = inflater.inflate(R.layout.service_fragment, container, false);
            networkConnection = new NetworkConnection(getActivity());
            httpConnection = new HttpClientWrapper();
            Configuration config = getActivity().getResources().getConfiguration();
            float dimevalue;
            if (config.smallestScreenWidthDp >= 720) {
                dimevalue = getResources().getDimension(R.dimen.title_value_T10);

            } else if (config.smallestScreenWidthDp >= 600) {
                dimevalue = getResources().getDimension(R.dimen.title_value_T7);
            } else {
                dimevalue = getResources().getDimension(R.dimen.title_value_m);
            }
            ((ConnectionRegistrationFlipper) getActivity()).changeSize(getResources().getString(R.string.grievance), dimevalue);
            categoryList();
          /*  Toolbar toolbar = (Toolbar)v. findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
           *//* setSupportActionBar(toolbar);*//*
           *//* getSupportActionBar().setDisplayHomeAsUpEnabled(true);*//*
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //    onBackPressed();
                }
            });*/
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            category = (MaterialBetterSpinner)v. findViewById(R.id.category_id);
            sub_category = (MaterialBetterSpinner)v. findViewById(R.id.sub_category);
            connectionType = (MaterialBetterSpinner) v.findViewById(R.id.connection_id);
            submitBtn = (Button)v. findViewById(R.id.complain_submit);
            cancelBtn = (Button)v. findViewById(R.id.complain_cancel);
            summary = (EditText)v. findViewById(R.id.summary);
            final TextView txtleft = (TextView)v. findViewById(R.id.txtleft);
            category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        category_id = categoryList.get(position).getId();
                        Log.e("category_id", "" + category_id);
                        sub_category.setText("");
                        connectionType.setText("");
                        sub_category.setFocusable(true);
                        categoryClick(category_id);
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("error_category", e.toString(), e);
                    }
                }
            });

            sub_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        subcategory_id = subcategoryList.get(position).getId();
                        connectionType.setText("");
                        sub_category.setFocusable(true);
                        getConnectionList();
                        Log.e("subcategory_id", "" + subcategory_id);

                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("error_category", e.toString(), e);
                    }
                }
            });

            connectionType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    connectionId = String.valueOf(connectionList.get(position).getId());
                }
            });
            summary.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // this will show characters remaining
                    txtleft.setText(150 - s.toString().length() + " " + getText(R.string.char_left));
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    submitBtnClick();

                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i =new Intent(getActivity(),ComplaintListActivity.class);
                    startActivity(i);
                    getActivity().finish();

                }
            });

            connectionList = new ArrayList<>();
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

        return v;

    }


    private void getConnectionList() {

        try {
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/customer/getcustomerconnections";
                JSONObject requestObject = new JSONObject();
                requestObject.put("id", "" + DBHelper.getInstance(getActivity()).getCustomerId());
                String login = new Gson().toJson(requestObject);
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionError),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void connectionResponse(String message) {
        String response = message;
        if (response != null
                && !response.equalsIgnoreCase("Server Connection Error")) {
            Gson gson = new Gson();
            ConnectionListDto connectionResponse = gson.fromJson(response, ConnectionListDto.class);
            if (connectionResponse.getStatusCode() == 0) {

                connectionList = connectionResponse.contents;
                if (connectionList != null
                        && connectionList.size() > 0) {

                    int loopSize = connectionList.size();
                    List<String> connectionName = new ArrayList<>();
                    for (int i = 0; i < loopSize; i++) {
                        connectionName.add(connectionList.get(i).getName());
                    }
                    connectionAdapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.dropdownrow, connectionName);
                    connectionType.setAdapter(connectionAdapter);
                }
            }
        }
    }

    /*   @Override
       protected void processMessage(Bundle message, ServiceListenerType what) {
           switch (what) {
               case GRIEVANCE_CAT:
                   dismissProgress();
                   categoryResponse(message);
                   break;
               case COMPLAINT_SUBCAT:
                   dismissProgress();
                   subcategoryResponse(message);
                   break;
               case COMPLAINT_SUBMIT:
                   dismissProgress();
                   complaintsResponse(message);
                   break;

               case CONNECTION_LIST:
                   dismissProgress();
                   connectionResponse(message);
                   break;
               default:
                   dismissProgress();
                   Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused),
                           Toast.LENGTH_SHORT).show();
                   break;
           }
       }*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("Sham Enter the handleMessage method :::");

            ServiceListenerType type = (ServiceListenerType) msg.obj;
            String response = msg.getData().getString(DBConstants.RESPONSE_DATA);

            switch (type) {



                case GRIEVANCE_CAT:
                    dismissProgress();
                    categoryResponse(response);
                    break;
                case COMPLAINT_SUBCAT:
                    dismissProgress();
                    subcategoryResponse(response);
                    break;
                case COMPLAINT_SUBMIT:
                    dismissProgress();
                    complaintsResponse(response);
                    break;

                case CONNECTION_LIST:
                    dismissProgress();
                    connectionResponse(response);
                    break;
                default:
                    dismissProgress();
                    Toast.makeText(getActivity(), getString(R.string.connectionRefused),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void categoryList() {

        try {
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                Log.e("application Fragment","grievance Type APP Called");
                GrievanceDto grievanceDto = new GrievanceDto();
                grievanceDto.setGrievanceType(GrievanceType.APP);
                String grievance = new Gson().toJson(grievanceDto);
                StringEntity se = new StringEntity(grievance, HTTP.UTF_8);
                Log.e("application Fragment","request"+grievance.toString());
                String url = "/grievancecategory";
                httpConnection.sendRequest(url, null, ServiceListenerType.GRIEVANCE_CAT, SyncHandler,
                        RequestType.POST, se, getActivity());
            } else {
                dismissProgress();

                Toast.makeText(getActivity(), getString(R.string.connectionError),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
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

    private void categoryResponse(String message) {
        try {
            String response = message;
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto categoryResponse = gson.fromJson(response, ConnectionCheckDto.class);
            if (categoryResponse.getStatusCode() == 0) {
                if (categoryResponse != null) {
                    categoryList = categoryResponse.getComplaintCategory();
                }
                ArrayList<String> categoryArray = new ArrayList<String>();
                for (int i = 0; i < categoryList.size(); i++) {
                    categoryArray.add(categoryList.get(i).getName());
                }
                categoryAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.dropdownrow, categoryArray);
                category.setAdapter(categoryAdapter);
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void categoryClick(int category_id) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/getgrievancesubcategory";
                JSONObject requestObject = new JSONObject();
                requestObject.put("id", "" + category_id);
                String login = new Gson().toJson(requestObject);
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.COMPLAINT_SUBCAT,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void subcategoryResponse(String message) {
        String response = message;
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ConnectionCheckDto categoryResponse = gson.fromJson(response, ConnectionCheckDto.class);
        if (categoryResponse != null
                && categoryResponse.getComplaintSubCategory() != null
                && categoryResponse.getComplaintSubCategory().size() > 0) {
            subcategoryList = categoryResponse.getComplaintSubCategory();
        }
        ArrayList<String> subcategoryArray = new ArrayList<String>();
        for (int i = 0; i < subcategoryList.size(); i++) {
            subcategoryArray.add(subcategoryList.get(i).getName());
        }
        subcategoryAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.dropdownrow, subcategoryArray);
        sub_category.setAdapter(subcategoryAdapter);
    }

 /*   @Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    private void submitBtnClick() {
        try {
            String category_str = category.getText().toString();
            String subcategory_str = sub_category.getText().toString();
            if (StringUtils.isEmpty(category_str)) {
                category.setError(getString(R.string.category_err));
                return;
            }
            if (StringUtils.isEmpty(subcategory_str)) {
                sub_category.setError(getString(R.string.subcategory_err));
                return;
            }
            if (summary.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "" +
                        getString(R.string.summary_err), Toast.LENGTH_SHORT).show();
                requestFocus(summary);
                return;
            }
            networkConnection = new NetworkConnection(getContext());
            httpConnection = new HttpClientWrapper();
            GrievanceDto complaints = new GrievanceDto();
            GenericDto customerDto = new GenericDto();
            customerDto.setId(DBHelper.getInstance(getActivity()).getCustomerId());
            GenericDto categoryDto = new GenericDto();
            categoryDto.setId(category_id);
            GenericDto subcategoryDto = new GenericDto();
            subcategoryDto.setId(subcategory_id);
            complaints.setCustomer(customerDto);
            complaints.setGrievanceCategory(categoryDto);
            complaints.setGrievanceSubCategory(subcategoryDto);
            complaints.setDescription("" + summary.getText().toString());
            complaints.setConnectionId(connectionId);
            complaints.setGrievanceType(GrievanceType.APP);
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/grievance/add";
                String login = new Gson().toJson(complaints);
                Log.e("requestcompalint", "" + login);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.COMPLAINT_SUBMIT,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Toast.makeText(getActivity(),
                        getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void complaintsResponse(String message) {
        String response = message;
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        GrievanceDto complaintResponse = gson.fromJson(response, GrievanceDto.class);
        if (complaintResponse != null) {
            if (complaintResponse.getStatusCode() == 0) {
                AlertDialog alertdialog = new AlertDialog(getActivity(),
                        getString(R.string.complaint_reg_success));
                alertdialog.show();
            } else {
                Toast.makeText(getActivity(),
                        complaintResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        GlobalAppState.getInstance().trackScreenView("Application Fragment");
    }
}
