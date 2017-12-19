package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.PaymentHistoryDto;
import com.omneagate.erbc.Dto.PaymentHistoryList;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by user1 on 17/6/16.
 */
public class PaymentHistoryActivity extends BaseActivity {

    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    SwipeMenuListView listView;
    List<PaymentHistoryDto> paymentListDto = new ArrayList<>();
    ConnectionDto ConnectionDetail;
    String connection_id;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private boolean isTamil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionlist);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        calendar = Calendar.getInstance();
        configureInitialPage();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent nextpage = new Intent(PaymentHistoryActivity.this, PaymentHistorydetail.class);
                nextpage.putExtra("paymentdetails", new Gson().toJson(paymentListDto.get(position)));
                startActivity(nextpage);
                finish();
            }
        });
    }

    private void configureInitialPage() {
        Intent intent = getIntent();
        connection_id = intent.getStringExtra("connenction_id");
        Log.e("connection_id", "" + connection_id);
        networkConnection = new NetworkConnection(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getString(R.string.bottom_payment));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        BillpayDto cusotmer = new BillpayDto();
        cusotmer.setCustomerId("" + DBHelper.getInstance(getApplicationContext()).getCustomerId());
        cusotmer.setConnectionId(connection_id);

        if (!Util.checkAppLanguage(PaymentHistoryActivity.this).equalsIgnoreCase("en"))
            isTamil = true;


        JSONObject requestObject = new JSONObject();

        try {
            requestObject.put("customerId", "" + DBHelper.getInstance(getApplicationContext()).getCustomerId());
            requestObject.put("connectionId", "" + connection_id);
            paymenthistory_Info(requestObject);
            Log.e("requestDto", "" + requestObject);
        } catch (JSONException e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void paymenthistory_Info(JSONObject billdato) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            httpConnection = new HttpClientWrapper();
            if (networkConnection.isNetworkAvailable()) {
                String url = "/paymenthistory/getconnectionpaymenthistory";
                String reqestStr = new Gson().toJson(billdato);
                StringEntity se = new StringEntity(billdato.toString(), HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.PAYMENT_LIST, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));

            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        Intent in =new Intent(PaymentHistoryActivity.this,LandingActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {

            case PAYMENT_LIST:
                dismissProgress();
                paymentlist_Response(message);
                break;

            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
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


    private void paymentlist_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("payment_history_res", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            PaymentHistoryList payment_historyList;
            payment_historyList = gson.fromJson(response, PaymentHistoryList.class);
            if (payment_historyList != null) {
                if (payment_historyList.getStatusCode() == 0) {
                    paymentListDto = payment_historyList.getPaymentHistoryList();
                    listView.setAdapter(mAdapter);
                    if (paymentListDto.size() != 0) {
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    }
                } else {
                    ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.paymenteError));
                    // Toast.makeText(getApplicationContext(), ""+payment_historyList.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionError));
                // Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
        }
    }


    class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return paymentListDto.size();
        }

        @Override
        public PaymentHistoryDto getItem(int position) {
            return paymentListDto.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.usagelist, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.connectionNumber = (TextView) convertView.findViewById(R.id.connection_number);
                holder.connectionName = (TextView) convertView.findViewById(R.id.connectionname);
                holder.createdays = (TextView) convertView.findViewById(R.id.cdays);
                holder.month_year = (TextView) convertView.findViewById(R.id.month_year);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.kvah_id = (TextView) convertView.findViewById(R.id.kvah_id);
                holder.payment_status = (TextView) convertView.findViewById(R.id.payment_status);
                holder.connectionNumber.setText(paymentListDto.get(position).getConsumerDisplayNo());
                //holder.connectionName.setText(paymentListDto.get(position).getConsumerName()+" / "+paymentListDto.get(position).getConsumerNumber());
                // As per Tester request it's display only Name
                holder.connectionName.setText(paymentListDto.get(position).getConsumerName());
                holder.payment_status.setVisibility(View.VISIBLE);
                if (paymentListDto.get(position).isPaymentStatus()) {
                    holder.payment_status.setText(getString(R.string.pay_success));
                    holder.payment_status.setTextColor(Color.parseColor("#39b54a"));
                } else {
                    holder.payment_status.setText(getString(R.string.payment_faill));
                    holder.payment_status.setTextColor(Color.parseColor("#ff0000"));
                }

                if (paymentListDto.get(position).getTransactionDate() == null || paymentListDto.get(position).getTransactionDate() == "") {
                    Log.e("payment", "paymentconnectionempty");
                } else {

                    Date date = dateFormat.parse(paymentListDto.get(position).getTransactionDate());
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DATE);
                    int year = calendar.get(Calendar.YEAR);
                    /*String[] items1 = paymentListDto.get(position).getTransactionDate().split("-");
                    String d1 = items1[0];
                    String m1 = items1[1];
                    String y1 = items1[2];
                    int d = Integer.parseInt(d1);
                    int y = Integer.parseInt(y1);*/
                    holder.createdays.setText("" + day);
                    holder.month_year.setText(new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + year);
                    holder.price.setText("" + paymentListDto.get(position).getAmountPaid());
                    holder.kvah_id.setText("" + paymentListDto.get(position).getConsumption() + " kWh");
                }


            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }

            return convertView;
        }

        class ViewHolder {
            public TextView connectionNumber, connectionName, createdays, month_year, price, kvah_id, payment_status;

            public ViewHolder(View view) {
                view.setTag(this);
            }
        }
    }
}

