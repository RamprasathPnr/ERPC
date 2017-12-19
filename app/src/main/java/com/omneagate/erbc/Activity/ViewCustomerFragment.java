package com.omneagate.erbc.Activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
/**
 * Created by user1 on 24/5/16.
 */
public class ViewCustomerFragment extends BaseActivityFragment  {
    View view;
    EditText consumer_name,address1,address2,pincode;
    MaterialBetterSpinner village,taluk,district,state,countrySpinner,serviceType;
    Button nextBtn;
    CustomProgressDialog progressBar;
    private static final String TAG = ViewCustomerFragment.class.getName();

//    String[] SERVICE_TYPE_LIST = {"Owner",""};


    String languageCode =  GlobalAppState.language;
    public ViewCustomerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.viewconsumer, container, false);
        configureInitialPage(view);
        return view;
    }
    private void configureInitialPage(View view) {

        consumer_name = (EditText) view.findViewById(R.id.cousumer_name);
        address1 = (EditText) view.findViewById(R.id.address1);
        address2 = (EditText) view.findViewById(R.id.address2);
        village = (MaterialBetterSpinner) view.findViewById(R.id.village);
        taluk = (MaterialBetterSpinner)view.findViewById(R.id.taluk);
        serviceType = (MaterialBetterSpinner)view.findViewById(R.id.select_serviceType_spinner_View);
        district = (MaterialBetterSpinner)view.findViewById(R.id.district);
        state = (MaterialBetterSpinner)view.findViewById(R.id.state);
        countrySpinner = (MaterialBetterSpinner)view.findViewById(R.id.country);
        pincode = (EditText) view.findViewById(R.id.pincode);
        nextBtn = (Button) view.findViewById(R.id.button);
        nextBtn.setVisibility(View.INVISIBLE);
        String[] SERVICE_TYPE_LIST = {getActivity().getResources().getString(R.string.select_service_value),getActivity().getResources().getString(R.string.select_service_value1)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, SERVICE_TYPE_LIST);
    //    serviceType.setAdapter(arrayAdapter);
        /* ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);*/
        //setData

        if (languageCode.equalsIgnoreCase("ta")) {
            village.setText(""+ConnectionDetailActivity.ConnectionDetail.getVillage().getRegionalName());
            taluk.setText(""+ConnectionDetailActivity.ConnectionDetail.getTaluk().getRegionalName());
            serviceType.setText(""+ConnectionDetailActivity.ConnectionDetail.getConnectionUserType());
            state.setText(""+ConnectionDetailActivity.ConnectionDetail.getState().getRegionalName());
            district.setText(""+ConnectionDetailActivity.ConnectionDetail.getDistrict().getRegionalName());
            countrySpinner.setText(""+ConnectionDetailActivity.ConnectionDetail.getCountry().getRegionalName());
        }else{
            village.setText(""+ConnectionDetailActivity.ConnectionDetail.getVillage().getName());
            taluk.setText(""+ConnectionDetailActivity.ConnectionDetail.getTaluk().getName());
            serviceType.setText(""+ConnectionDetailActivity.ConnectionDetail.getConnectionUserType());
            state.setText(""+ConnectionDetailActivity.ConnectionDetail.getState().getName());
            district.setText(""+ConnectionDetailActivity.ConnectionDetail.getDistrict().getName());
            countrySpinner.setText(""+ConnectionDetailActivity.ConnectionDetail.getCountry().getName());
        }
        consumer_name.setText(""+ConnectionDetailActivity.ConnectionDetail.getConsumerName());
        address1.setText(""+ConnectionDetailActivity.ConnectionDetail.getAddressLine1());
        address2.setText(""+ConnectionDetailActivity.ConnectionDetail.getAddressLine2());

        pincode.setText(""+ConnectionDetailActivity.ConnectionDetail.getPinCode());
        consumer_name.setFocusable(false);
        address1.setFocusable(false);
        address2.setFocusable(false);
        pincode.setFocusable(false);

    }




















}

