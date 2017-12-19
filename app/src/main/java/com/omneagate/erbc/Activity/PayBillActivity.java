package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Adapter.PayBillAdapter;
import com.omneagate.erbc.Dto.BillhistoryList;
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

public class PayBillActivity extends BaseActivity {

    private RecyclerView mRvPayBillList;
    private CustomProgressDialog progressBar;
    private TextView mTvNoBill;
    private PayBillAdapter billAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.INVISIBLE);

        setupView();
        // To get un paid bill details from server
        new GetUnPaidBillDetails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupView() {
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();

        progressBar = new CustomProgressDialog(PayBillActivity.this);
        progressBar.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getString(R.string.pay_bill));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTvNoBill = (TextView) findViewById(R.id.txt_no_bill);

        mRvPayBillList = (RecyclerView) findViewById(R.id.pay_recycler);
        mRvPayBillList.setLayoutManager(new LinearLayoutManager(PayBillActivity.this));
    }

    @Override
    public void onBackPressed() {
        Intent backpage = new Intent(PayBillActivity.this, LandingActivity.class);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            backpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(backpage);
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

    private void showProgress() {
        try {
            if (progressBar != null) {
                progressBar.show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

    private class GetUnPaidBillDetails extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                CustomerDto customer = new CustomerDto();
                customer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());

                if (networkConnection.isNetworkAvailable()) {
                    String url = "/customer/getUnpaidBills";
                    String login = new Gson().toJson(customer);
                    StringEntity se = new StringEntity(login, HTTP.UTF_8);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST,
                            SyncHandler, RequestType.POST, se, PayBillActivity.this);
                } else {
                    dismissProgress();
                    mTvNoBill.setText(getString(R.string.connectionRefused));
                    //Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("LoginActivity", e.toString(), e);
            }
            return null;
        }
    }

    private void hideImageView() {

        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.INVISIBLE);

    }

    private void setupAdapter(BillhistoryList billHistory) {

        if (billHistory != null) {
            if (billHistory.getStatusCode() == 0
                    && billHistory.getBillHistoryList().size() > 0) {
                hideImageView();
                billAdapter = new PayBillAdapter(PayBillActivity.this,
                        billHistory.getBillHistoryList());
                mRvPayBillList.setAdapter(billAdapter);
            } else/*(billHistory.getErrorDescription().contains("Bill history Not available"))*/
            {
                ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.VISIBLE);
                mTvNoBill.setText(getString(R.string.connectionRefused1));

            }
                /*mTvNoBill.setText(getString(R.string.connectionRefused1));*/
           /* ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.GONE);*/
        } else {
            ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.VISIBLE);
            mTvNoBill.setText(getString(R.string.connectionRefused));
          /*  ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txt_no_bill)).setVisibility(View.GONE);*/
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {

            case CONNECTION_LIST:
                dismissProgress();
                String response = message.getString(DBConstants.RESPONSE_DATA);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                BillhistoryList billHistory = gson.fromJson(response, BillhistoryList.class);
                setupAdapter(billHistory);
                break;

            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
