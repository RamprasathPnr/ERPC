package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.LogoutDialog;
import com.omneagate.erbc.Adapter.ViewPagerAdapter;
import com.omneagate.erbc.Dto.ConnectionCheckDto;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

/**
 * Created by user1 on 4/5/16.
 */
public class ConnectionDetailActivity extends BaseActivity {

    public static ConnectionDto ConnectionDetail;
    public static String flag;
    CustomProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectiondetail);
        configureInitialPage();
    }

    private void configureInitialPage() {
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        TextView connectionNumber = (TextView) findViewById(R.id.connectionhead);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(ConnectionDetailActivity.this, ConnectionListActivity.class);
                startActivity(registerPage);
                finish();
                //  onBackPressed();
            }
        });
        FloatingActionButton deleteConnection = (FloatingActionButton) findViewById(R.id.fab);
        Intent intent = getIntent();
        String connenction = intent.getStringExtra("connenctionDto");
        flag = intent.getStringExtra("flag");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ConnectionDetail = gson.fromJson(connenction, ConnectionDto.class);
        connectionNumber.setText("" + ConnectionDetail.getConsumerNumber());
        Log.e("ConnectionDetail", "" + ConnectionDetail.toString());
        deleteConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutDialog alertdialog = new LogoutDialog(ConnectionDetailActivity.this, getString(R.string.connnecion_delte_mdg));
                alertdialog.show();
            }
        });
        Log.e("flag", "" + flag);
        if (flag.equals("0")) {
            deleteConnection.setVisibility(View.INVISIBLE);
        } else {
            deleteConnection.setVisibility(View.VISIBLE);
        }
    }

    public void deleteConnection() {
        try {
            CustomerDto customerdto = new CustomerDto();
            customerdto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            ConnectionDetail.setCustomer(customerdto);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/deleteconsumer";
                String login = new Gson().toJson(ConnectionDetail);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_DELETE, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ViewCustomerFragment(), getString(R.string.consumer));
        adapter.addFragment(new ViewConnectionFragment(), getString(R.string.connection));
        adapter.addFragment(new ViewMeterFragment(), getString(R.string.meter));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent registerPage = new Intent(ConnectionDetailActivity.this, ConnectionListActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();
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

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case CONNECTION_DELETE:
                dismissProgress();
                connectionDelete_Response(message);
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void connectionDelete_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("connectionRespoonse", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto connectionList;
            connectionList = gson.fromJson(response, ConnectionCheckDto.class);
            if (connectionList != null) {
                if (connectionList.getStatusCode() == 0) {
                    Intent backpage = new Intent(getApplicationContext(), ConnectionListActivity.class);
                    startActivity(backpage);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
        }
    }


}
