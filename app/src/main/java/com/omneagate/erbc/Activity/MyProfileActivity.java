package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;

/**
 * Created by user1 on 12/5/16.
 */
public class MyProfileActivity extends BaseActivity {

    TextView name, phonenumber, email, occuaption, dob, mstatus, address1, address2, taluk, district, country_pincode;
    FloatingActionButton fab;

    public MyProfileActivity() {
        // Required empty public constructor
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilefragment);
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
        configureInitialPage();
        setCustomerValue();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerPage = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(registerPage);
                finish();
            }
        });
    }

    private void configureInitialPage() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        name = (TextView) findViewById(R.id.name);
        phonenumber = (TextView) findViewById(R.id.phonenumber);
        email = (TextView) findViewById(R.id.email_id);
        occuaption = (TextView) findViewById(R.id.occupation_id);
        dob = (TextView) findViewById(R.id.dob_id);
        mstatus = (TextView) findViewById(R.id.mstatus_id);
        address1 = (TextView) findViewById(R.id.address_id1);
        address2 = (TextView) findViewById(R.id.address_id2);
        taluk = (TextView) findViewById(R.id.taluk_id);
        district = (TextView) findViewById(R.id.district_id);
        country_pincode = (TextView) findViewById(R.id.state_country_pincode_id);
    }

    private void setCustomerValue() {
        try {
            CustomerDto customerRecord = DBHelper.getInstance(getApplicationContext()).getcustomerData();
            if (customerRecord != null) {
                name.setText("" + customerRecord.getFirstName().toUpperCase() + " " + customerRecord.getMiddleName().toUpperCase() + " " + customerRecord.getLastName().toUpperCase());
                phonenumber.setText("+ " + customerRecord.getCountryCode() + " " + customerRecord.getMobileNumber());
                email.setText("" + customerRecord.getEmail());

                dob.setText("" + customerRecord.getDob());
                Log.e("MyProfile",""+customerRecord.toString());
                Log.e("MyProfile",""+customerRecord.getMaritalStatus());
                Log.e("MyProfile",""+customerRecord.getMaritalStatus().getName());
                if(customerRecord.getMaritalStatus() !=null){
                    if(customerRecord.getMaritalStatus().getName() == null
                            ||customerRecord.getMaritalStatus().getName().equalsIgnoreCase("") ) {
                        mstatus.setText("----");
                    }else{
                        mstatus.setText("" + customerRecord.getMaritalStatus().getName());

                    }
                }else{
                    mstatus.setText("-----");
                }
                Log.e("MyProfile",""+customerRecord.toString());
                Log.e("MyProfile",""+customerRecord.getMaritalStatus());
                Log.e("MyProfile",""+customerRecord.getMaritalStatus().getName());
                if(customerRecord.getOccupation() !=null){
                    if(customerRecord.getOccupation().getName() ==null
                            || customerRecord.getOccupation().getName().equalsIgnoreCase("")) {

                        occuaption.setText("----");
                    }else{
                         occuaption.setText("" + customerRecord.getOccupation().getName());
                    }
                }else{
                    occuaption.setText("----");
                }

                address1.setText("" + customerRecord.getAddressLine1() + ",");
                address2.setText("" + customerRecord.getAddressLine2());
                taluk.setText("" + customerRecord.getVillage().getName() + "," + customerRecord.getTaluk().getName() + ",");
                district.setText("" + customerRecord.getDistrict().getName() + ",");
                country_pincode.setText("" + customerRecord.getState().getName() + " , " + customerRecord.getCountry().getName() + " - " + customerRecord.getPinCode() + ".");
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent registerPage = new Intent(MyProfileActivity.this, LandingActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();
    }
}
