package com.omneagate.erbc.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Dto.CityDto;
import com.omneagate.erbc.Dto.ConnectionListDto;
import com.omneagate.erbc.Dto.GrievanceDto;
import com.omneagate.erbc.Dto.ConnectionCheckDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GenericDto;
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
 * Created by user1 on 19/7/16.
 */


public class ComplaintsRegisterActivity extends BaseActivity {

    CustomProgressDialog progressBar;
    private static final String TAG = ComplaintsRegisterActivity.class.getName();
    List<GenericDto> categoryList = new ArrayList<>();
    List<GenericDto> subcategoryList = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter, subcategoryAdapter;
    MaterialBetterSpinner category, sub_category, connectionType;
    int category_id, subcategory_id;
    Button submitBtn;
    EditText summary;
    private List<CityDto> connectionList;
    private ArrayAdapter<String> connectionAdapter;
    private String connectionId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaintactivity);
        try {
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            categoryList();
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            category = (MaterialBetterSpinner) findViewById(R.id.category_id);
            sub_category = (MaterialBetterSpinner) findViewById(R.id.sub_category);
            connectionType = (MaterialBetterSpinner) findViewById(R.id.connection_id);
            submitBtn = (Button) findViewById(R.id.complain_submit);
            summary = (EditText) findViewById(R.id.summary);
            final TextView txtleft = (TextView) findViewById(R.id.txtleft);
            category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        category_id = categoryList.get(position).getId();
                        Log.e("category_id", "" + category_id);
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

            connectionList = new ArrayList<>();
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void getConnectionList() {

        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/customer/getcustomerconnections";
                JSONObject requestObject = new JSONObject();
                requestObject.put("id", "" + DBHelper.getInstance(this).getCustomerId());
                String login = new Gson().toJson(requestObject);
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST,
                        SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void connectionResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
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
                    connectionAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.dropdownrow, connectionName);
                    connectionType.setAdapter(connectionAdapter);
                }
            }
        }
    }

    @Override
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
    }


    private void categoryList() {

        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/grievancecategory";
                httpConnection.sendRequest(url, null, ServiceListenerType.GRIEVANCE_CAT, SyncHandler,
                        RequestType.GET, null, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError),
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

    private void categoryResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ConnectionCheckDto categoryResponse = gson.fromJson(response, ConnectionCheckDto.class);
        if (categoryResponse != null) {
            categoryList = categoryResponse.getComplaintCategory();
        }
        ArrayList<String> categoryArray = new ArrayList<String>();
        for (int i = 0; i < categoryList.size(); i++) {
            categoryArray.add(categoryList.get(i).getName());
        }
        categoryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.dropdownrow, categoryArray);
        category.setAdapter(categoryAdapter);
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
                        SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void subcategoryResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
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
        subcategoryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.dropdownrow, subcategoryArray);
        sub_category.setAdapter(subcategoryAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

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
                Toast.makeText(getApplicationContext(), "" +
                        getString(R.string.summary_err), Toast.LENGTH_SHORT).show();
                requestFocus(summary);
                return;
            }
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            GrievanceDto complaints = new GrievanceDto();
            GenericDto customerDto = new GenericDto();
            customerDto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            GenericDto categoryDto = new GenericDto();
            categoryDto.setId(category_id);
            GenericDto subcategoryDto = new GenericDto();
            subcategoryDto.setId(subcategory_id);
            complaints.setCustomer(customerDto);
            complaints.setGrievanceCategory(categoryDto);
            complaints.setGrievanceSubCategory(subcategoryDto);
            complaints.setDescription("" + summary.getText().toString());
            complaints.setConnectionId(connectionId);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/grievance/add";
                String login = new Gson().toJson(complaints);
                Log.e("requestcompalint", "" + login);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.COMPLAINT_SUBMIT,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void complaintsResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        GrievanceDto complaintResponse = gson.fromJson(response, GrievanceDto.class);
        if (complaintResponse != null) {
            if (complaintResponse.getStatusCode() == 0) {
                AlertDialog alertdialog = new AlertDialog(ComplaintsRegisterActivity.this,
                        getString(R.string.complaint_reg_success));
                alertdialog.show();
            } else {
                Toast.makeText(getApplicationContext(),
                        complaintResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
